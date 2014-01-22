package ru.repp.chat.client;

import org.apache.commons.lang3.StringUtils;
import ru.repp.chat.utils.Constants;
import ru.repp.chat.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Клиентское приложение
 *
 * @author den
 * @since 1/12/14
 */
class ClientApp {
    public static void main(String[] args) throws Exception {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));

        Client c = new BaseClient();
        int connected = c.connect(Constants.HOSTNAME, Constants.PORT);

        if (connected != 0) {
            exitApp();
        }

        String msg;
        do {
            c.doLogin();
        } while (c.isConnected() && !c.isLoggedIn());

        while (c.isConnected()) {
            msg = inReader.readLine();
            if (!StringUtils.isBlank(msg)) {
                if (Utils.matchesClinetCommonCommandPattern(msg)) {
                    // сообщение содержит комманду
                    c.sendRawText(msg);
                } else {
                    c.send(msg);
                }

            }
        }
        c.stop();
        exitApp();
    }

    private static void exitApp() throws IOException {
        System.out.println("Press any key to exit");
        System.in.read();
        System.exit(0);
    }


}
