package ru.repp.chat.client;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import ru.repp.chat.ChatCommand;

/**
* Здесь будет ваша реклама
*
* @author den
* @since 1/12/14
*/
public class ClientSessionHandler extends IoHandlerAdapter {

    public ClientSessionHandler() {
    }

    @Override
    public void sessionOpened(IoSession session) {

    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        String theMessage = (String) message;
        String[] result = theMessage.split(" ", 3);
        String status = result[1];
        String theCommand = result[0];
        ChatCommand command = ChatCommand.valueOf(theCommand);
        if (ChatCommand.OK_STATUS.equals(status)) {
            switch (command.toInt()) {
                case ChatCommand.SEND:
                    if (result.length == 3) {
                        System.out.println(result[2]);
//                        callback.messageReceived(result[2]);
                    }
                    break;
                case ChatCommand.LOGIN:
//                    callback.loggedIn();
                    System.out.println("Login successfull!");
                    System.out.println("[Hint] Type /help to get command list");
                    break;

                case ChatCommand.QUIT:
//                    callback.loggedOut();
                    System.out.println("Session closed. You left the chat.");
                    session.close(true);
            }

        } else {
            System.err.println("Server send an error! " + result[2]);
//            if (result.length == 3) {
//                callback.error(result[2]);
//            }
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        System.out.println("error found!" + cause.toString());
        cause.printStackTrace();
        session.close(true);
    }
}
