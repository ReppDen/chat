package ru.repp.chat.utils;

import org.apache.mina.core.session.IoSession;

/**
 * @author den
 * @since 1/17/14
 */
public interface ResponseHandler {

    public void error(String msg) ;

    public void messageReceived(IoSession session, Command cmd, String value) ;
}
