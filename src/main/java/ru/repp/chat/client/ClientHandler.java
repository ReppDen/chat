package ru.repp.chat.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Response;

/**
* обработчик сетевого взаимодействия клиента
*
* @author den
* @since 1/12/14
*/
public class ClientHandler extends IoHandlerAdapter {


    MessageHandler messageHandler;
    public ClientHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
    @Override
    public void sessionOpened(IoSession session) {

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        String completeMsg = (String) message;
        String[] parts = completeMsg.split(" ", 2);
        String command = parts[0];
        String status = parts[1];

        Command cmd = Command.formString(command);

        if (Response.OK.toString().equals(status)) {
            // комманда успешно обработана
//            switch (cmd) {
//                case SEND: {
//                    break;
//                }
//                case LOGIN: {
//                    break;
//                }
//                case QUIT: {
//                    break;
//                }
//                case HELP: {
//                    break;
//                }
//                case LIST: {
//                    break;
//                }
//            }
            messageHandler.messageReceived(cmd, status);
        } else {
            messageHandler.error(status);
        }

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        session.close(true);
    }
}
