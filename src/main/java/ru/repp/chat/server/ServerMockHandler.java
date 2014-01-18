package ru.repp.chat.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Response;
import ru.repp.chat.utils.Utils;

/**
 * обработчик сетевого взаимодействия заглушки сервера
 *
 * @author den
 * @since 1/18/14
 */
public class ServerMockHandler extends IoHandlerAdapter {


    Server s;
    public ServerMockHandler(Server s) {
        this.s = s;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String completeMsg = (String) message;
        s.getHistoryManager().add(completeMsg);
        String[] parts = completeMsg.split(" ", 2);
        String command = parts[0];
        String value = parts[1];

        Command cmd = Command.formString(command);
        switch (cmd) {
            case LOGIN: {
                session.setAttribute("user", value);
                session.write(Utils.makeCustomServerCmd(cmd, Response.OK, value));
                break;
            }
            case QUIT: {
                session.write(Utils.makeCustomServerCmd(cmd, Response.OK, value));
                break;
            }
            default: {
                if (session.getAttribute("user") == null) {
                    session.write(Utils.makeCustomServerCmd(cmd, Response.ERROR, "You not authorized"));
                } else {
                    session.write(Utils.makeCustomServerCmd(cmd, Response.OK, value));
                }

            }

        }

    }
}
