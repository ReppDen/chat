package ru.repp.chat.client;

import ru.repp.chat.utils.Command;

/**
 * Обработчких сообщений с выводом в консоль
 *
 * @author den
 * @since 1/17/14
 */
public class SystemOutMessageHandler implements MessageHandler {

    public void error(String msg) {
        System.err.println("Server send an error! " + msg);

    }

    public void messageReceived(Command cmd, String status) {
        switch (cmd) {
            case LOGIN: {
                System.out.println("Login successfull!");
                System.out.println("[Hint] Type /help to get command list");
                break;
            }
            case QUIT: {
                System.out.println("Session closed. You left the chat.");
                break;
            }
            case HELP: {
                System.out.println("Nice try, LOL!");
                break;
            }
            case LIST: {
                break;
            }
            case SEND: default: {

                break;
            }
        }
    }
}
