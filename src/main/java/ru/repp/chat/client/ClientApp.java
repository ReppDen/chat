package ru.repp.chat.client;

import ru.repp.chat.utils.Constants;

/**
 * Клиентское приложение
 *
 * @author den
 * @since 1/12/14
 */
public class ClientApp {
    public static void main(String[] args) throws Exception {
        Client c = new BaseClient();
        c.connect(Constants.HOSTNAME, Constants.PORT);
    }


}
