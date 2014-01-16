package ru.repp.chat.client;

import ru.repp.chat.utils.Command;

/**
 * @author den
 * @since 1/17/14
 */
public interface MessageHandler {

    public void error(String status);

    public void messageReceived(Command cmd, String status);
}
