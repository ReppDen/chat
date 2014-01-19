package ru.repp.chat.server;

import ru.repp.chat.utils.Constants;

import java.io.IOException;

/**
 * Серверное приложение
 *
 * @author den
 * @since  1/12/14
 */
public class ServerApp {


    public static void main(String[] args) throws IOException {
        new BaseServer(Constants.PORT).start();
    }
}
