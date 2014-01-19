package ru.repp.chat.server;

import java.io.IOException;

/**
 * Серверное приложение
 *
 * @author den
 * @since  1/12/14
 */
public class ServerApp {

    private static final int PORT = 9123;

    public static void main(String[] args) throws IOException {
        new BaseServer(PORT).start();
    }
}
