package ru.repp.chat.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.repp.chat.ChatCommand;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Здесь будет ваша реклама
 *
 * @author den
 * @since 1/12/14
 */
public class ServerChatMessageHandler extends IoHandlerAdapter {

    private final Set<IoSession> sessions = Collections
            .synchronizedSet(new HashSet<IoSession>());

    private final Set<String> users = Collections
            .synchronizedSet(new HashSet<String>());


    private final static Logger LOG = LoggerFactory.getLogger((Server.class));

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        LOG.error("exceptionCaught", cause);
        session.close(true);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        LOG.info("new session");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {

        String theMessage = (String) message;
        String[] result = theMessage.split(" ", 2);
        String theCommand = result[0];

        try {

            ChatCommand command = ChatCommand.valueOf(theCommand);
            LOG.info(theMessage);

            String user = (String) session.getAttribute("user");
            switch (command.toInt()) {

                case ChatCommand.QUIT:
                    session.write(ChatCommand.QUIT_CMD + " " + ChatCommand.OK_STATUS);
                    session.close(true);
                    break;
                case ChatCommand.LOGIN:
                    // проверим имя на занятость
                    if (users.contains(user)) {
                        session.write(ChatCommand.LOGIN_CMD + " Error! Name " + user + " already in use!");
                        return;
                    }

                    // проверить на повторный логин
                    if (user != null) {
                        session.write(ChatCommand.LOGIN_CMD + " Error! User " + user + " already logged in.");
                        return;
                    }

                    // если все ок - сохраняем пользователя в сессии
                    if (result.length == 2) {
                        user = result[1];
                    } else {
                        session.write(ChatCommand.LOGIN_CMD + " Error! Login command is not correct!");
                        return;
                    }

                    sessions.add(session);
                    session.setAttribute("user", user);

                    users.add(user);
                    session.write(ChatCommand.LOGIN_CMD + " " + ChatCommand.OK_STATUS);
                    broadcast("User " + user + " has joined the chat.");
                    break;

                case ChatCommand.SEND:
                        broadcast(user + ": " + theMessage);
                    break;
                default:
                    LOG.error("UNRECOGNIZED COMMAND!");
                    throw new IllegalStateException("UNRECOGNIZED COMMAND!");
            }

        } catch (IllegalArgumentException e) {
            LOG.error("Illegal argument", e);
        }
    }

    /**
     * вещаем по всем клиентам в формате
     * {command} {status} {text}
     * @param message транслируемое сообщение
     */
    public void broadcast(String message) {
        synchronized (sessions) {
            for (IoSession session : sessions) {
                if (session.isConnected()) {
                    session.write(ChatCommand.SEND_CMD + " " + ChatCommand.OK_STATUS +  " " + message);
                }
            }
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        String user = (String) session.getAttribute("user");
        users.remove(user);
        sessions.remove(session);
        broadcast("User " + user + " has left the chat");
    }
}
