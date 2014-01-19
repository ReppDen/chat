package ru.repp.chat.client;

import org.apache.mina.core.session.IoSession;
import ru.repp.chat.utils.Command;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Обработчких сообщений с выводом в
 *
 * @author den
 * @since 1/17/14
 */
public  class PrintStreamResponseHandler implements ResponseHandler {


    private final PrintStream printStream;

    public PrintStreamResponseHandler(PrintStream printStream) {
        this.printStream = printStream;
    }

    public void error(String msg) throws IOException {
        printStream.println("Server send an error! " + msg);

    }

    public void messageReceived(IoSession session, Command cmd, String value) throws IOException {
        switch (cmd) {
            case LOGIN: {
                printStream.println("Login successfull!");
                printStream.println("[Hint] Type /help to get command list");
                session.setAttribute("user", value);
                break;
            }
            case QUIT: {
                printStream.println("Session closed. You left the chat.");
                session.close(true);
                break;
            }
            case LIST: case HELP: {
                printStream.println(value);
                break;
            }
            default: {
                break;
            }
        }
    }
}
