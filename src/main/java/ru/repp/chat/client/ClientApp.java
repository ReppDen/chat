package ru.repp.chat.client;

import java.io.IOException;

/**
 * Клиентское приложение
 *
 * @author den
 * @since 1/12/14
 */
public class ClientApp {

    public static void main(String[] args) throws IOException {
        Client c = new Client();
        c.start();
    }


}
