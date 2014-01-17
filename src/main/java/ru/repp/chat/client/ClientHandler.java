package ru.repp.chat.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Response;
import ru.repp.chat.utils.ResponseHandler;

/**
* обработчик сетевого взаимодействия клиента
*
* @author den
* @since 1/12/14
*/
public class ClientHandler extends IoHandlerAdapter {


    ResponseHandler responseHandler;
    public ClientHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
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
        String[] parts = completeMsg.split(" ", 3);
        String command = parts[0];
        String status = parts[1];
        String value = parts.length > 2 ? parts[2] : "";

        Command cmd = Command.formString(command);

        if (Response.OK.toString().equals(status)) {
            // комманда успешно обработана
            responseHandler.messageReceived(session, cmd, value);
        } else {
            responseHandler.error(value);
        }

    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        session.close(true);
    }
}
