package ru.repp.chat.client;

import org.apache.mina.core.session.IoSession;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.ResponseHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author den
 * @since 1/18/14
 */
public class BuffReaderMessageHandler implements ResponseHandler {
    private final OutputStreamWriter outStream;

    public BuffReaderMessageHandler(OutputStreamWriter out) {
        this.outStream = out;
    }

    public void error(String msg) {
        try {
            outStream.write("Server send an error! " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void messageReceived(IoSession session, Command cmd, String value) {
        try {
            switch (cmd) {
                case LOGIN: {
                    outStream.write("Login successfull!");
                    outStream.write("[Hint] Type /help to get command list");
                    session.setAttribute("user", value);
                    break;
                }
                case QUIT: {
                    outStream.write("Session closed. You left the chat.");
                    session.close(true);
                    break;
                }
                case HELP: {
                    outStream.write("Nice try, LOL!");
                    break;
                }
                case LIST: {
                    break;
                }
                case SEND:
                default: {

                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
