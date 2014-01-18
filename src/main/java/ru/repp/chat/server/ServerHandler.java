package ru.repp.chat.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Response;
import ru.repp.chat.utils.Utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * обработчик сетевого взаимодействия сервера
 *
 * @author den
 * @since 1/12/14
 */
public class ServerHandler extends IoHandlerAdapter {

    private final Set<IoSession> sessions = Collections
            .synchronizedSet(new HashSet<IoSession>());

    private final Set<String> users = Collections
            .synchronizedSet(new HashSet<String>());


    private final static Logger LOG = LoggerFactory.getLogger((ServerApp.class));

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        LOG.error("exceptionCaught", cause);
        cause.printStackTrace();
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        LOG.info("new session");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String completeMsg = (String) message;
        LOG.info(completeMsg);
        String[] parts = completeMsg.split(" ", 2);
        String command = parts[0];
        String value = parts[1];

        Command cmd = Command.formString(command);
        String user = (String) session.getAttribute("user");

        switch (cmd) {
            case LOGIN: {

                // проверим имя на занятость
                if (users.contains(value)) {
                    session.write(Utils.makeCustomServerCmd(Command.LOGIN, Response.ERROR, "Error! Name " + user + " already in use!"));
                    return;
                }

                // проверить на повторный логин
                if (user != null) {
                    session.write(Utils.makeCustomServerCmd(Command.LOGIN, Response.ERROR, "Error! You already logged in as " + user));
                    return;
                }

                // если все ок - сохраняем пользователя в сессии
                user = value;

                sessions.add(session);
                session.setAttribute("user", user);

                users.add(user);
                session.write(Utils.makeCustomServerCmd(Command.LOGIN, Response.OK, user));
                broadcast("User " + user + " has joined the chat.");
                break;
            }
            case QUIT: {
                session.write(Utils.makeCustomServerCmd(Command.QUIT, Response.OK, ""));
                break;
            }
            case HELP: {
                session.write(Utils.makeCustomServerCmd(Command.HELP, Response.OK, "Nice try, LOL!"));
                break;
            }
            case LIST: {
                session.write(Utils.makeCustomServerCmd(Command.HELP, Response.OK, "Here comes Users list"));
                break;
            }
            case SEND: default: {
                broadcast(user + ": " + value);
                break;
            }
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
                    session.write(Utils.makeCustomServerCmd(Command.SEND, Response.OK, message));
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
