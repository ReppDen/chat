package ru.repp.chat.client;

import java.io.FileReader;
import java.io.IOException;

/**
 * Клиентское приложение
 *
 * @author den
 * @since 1/12/14
 */
public class ClientApp {

    public static void main(String[] args) throws IOException, InterruptedException {
        ClientOld c = new ClientOld();
        c.setInReader(new FileReader("src/test/resources/client/simple.txt"));
        c.start();
    }


}
