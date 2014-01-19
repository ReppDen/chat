package ru.repp.chat.client;

/**
 * Клиентское приложение
 *
 * @author den
 * @since 1/12/14
 */
public class ClientApp {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9123;

    public static void main(String[] args) throws Exception {
        Client c = new BaseClient();
        c.connect(HOSTNAME, PORT);
        c.login("Den");
    }


}
