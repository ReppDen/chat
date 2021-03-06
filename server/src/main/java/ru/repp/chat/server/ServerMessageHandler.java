package ru.repp.chat.server;

import com.google.common.base.Joiner;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.filter.codec.RecoverableProtocolDecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.repp.chat.server.history.HistoryManager;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Constants;
import ru.repp.chat.utils.Response;
import ru.repp.chat.utils.Utils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * обработчик сетевого взаимодействия сервера
 *
 * @author den
 * @since 1/12/14
 */
class ServerMessageHandler extends IoHandlerAdapter {

    private final Set<IoSession> sessions = Collections
            .synchronizedSet(new HashSet<IoSession>());

    private final Set<String> users = Collections
            .synchronizedSet(new HashSet<String>());


    private final static Logger LOG = LoggerFactory.getLogger((ServerApp.class));
    private final HistoryManager historyManager;

    public ServerMessageHandler(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (cause instanceof RecoverableProtocolDecoderException) {
            // кривая комманда
            session.write(Utils.makeCustomServerCmd(Command.SEND, Response.ERROR, "Command is not correct! Check length of message"));
        } else if (cause instanceof WriteToClosedSessionException || cause instanceof IOException) {
            LOG.error("Connection closed");
        } else {
            LOG.error("exceptionCaught", cause);
            cause.printStackTrace();
        }

    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String completeMsg = (String) message;
        LOG.info(completeMsg);
        String[] parts = completeMsg.split(" ", 2);
        String command = parts[0];
        String value = parts.length > 1 ? parts[1] : "";

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
                synchronized (historyManager) {
                    for (String m : historyManager.getLast(Constants.HISTORY_LIMIT)) {
                        session.write(Utils.makeCustomServerCmd(Command.SEND, Response.OK, m));
                    }
                }
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
                session.write(Utils.makeCustomServerCmd(Command.HELP, Response.OK, getUserList()));
                break;
            }
            case SEND:
            default: {
                String chatMessage = user + ": " + value;
                synchronized (historyManager) {
                    historyManager.add(chatMessage);
                }
                broadcast(chatMessage);
                break;
            }
        }
    }

    private synchronized String getUserList() {
        Joiner joiner = Joiner.on("; ").skipNulls();
        return "Users in chat: " + joiner.join(users);
    }

    /**
     * вещаем по всем клиентам в формате
     * {command} {status} {text}
     *
     * @param message транслируемое сообщение
     */
    void broadcast(String message) {
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
        removeUser(user);
        removeSession(session);

        if (user != null) {
            broadcast("User " + user + " has left the chat");
        }
    }

    private synchronized void removeUser(String user) {
        users.remove(user);
    }

    private synchronized void removeSession(IoSession session) {
        sessions.remove(session);
    }


}
