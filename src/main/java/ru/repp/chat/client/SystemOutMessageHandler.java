package ru.repp.chat.client;

import org.apache.mina.core.session.IoSession;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.ResponseHandler;

/**
 * Обработчких сообщений с выводом в консоль
 *
 * @author den
 * @since 1/17/14
 */
public class SystemOutMessageHandler implements ResponseHandler {

    public void error(String msg) {
        System.err.println("Server send an error! " + msg);

    }

    public void messageReceived(IoSession session, Command cmd, String value) {
        switch (cmd) {
            case LOGIN: {
                System.out.println("Login successfull!");
                System.out.println("[Hint] Type /help to get command list");
                session.setAttribute("user", value);
                break;
            }
            case QUIT: {
                System.out.println("Session closed. You left the chat.");
                session.close(true);
                break;
            }
            case LIST: case HELP: {
                System.out.println(value);
                break;
            }
            default: {
                break;
            }
        }
    }
}
