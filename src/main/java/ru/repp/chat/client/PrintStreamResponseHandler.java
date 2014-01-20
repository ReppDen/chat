package ru.repp.chat.client;

import org.apache.mina.core.session.IoSession;
import ru.repp.chat.utils.Command;

import java.io.IOException;

/**
 * Обработчких сообщений с выводом в
 *
 * @author den
 * @since 1/17/14
 */
@Deprecated
public  class PrintStreamResponseHandler implements ResponseHandler {



    public void error(Command cmd, String msg) throws Exception {


    }

    public void messageReceived(IoSession session, Command cmd, String value) throws IOException {

    }
}
