package ru.repp.chat.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.util.Date;

/**
 * Здесь будет ваша реклама
 *
 * @author den
 * @since 1/12/14
 */
public class ChatMessageHandler extends IoHandlerAdapter {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        cause.printStackTrace();
        session.close(true);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String str = message != null ? message.toString() : null;
        if( str.trim().equalsIgnoreCase("quit") ) {
            session.close(true);
            return;
        }
        System.out.println("Recived " + str);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println("idle" + (new Date().toString()));
    }
}
