package ru.repp.chat.client;

import org.apache.mina.core.session.IoSession;
import ru.repp.chat.utils.Command;

import java.io.IOException;

/**
 * @author den
 * @since 1/17/14
 */
public interface ResponseHandler {

    public void error(Command cmd, String msg) throws IOException;

    public void messageReceived(IoSession session, Command cmd, String value) throws IOException;
}
