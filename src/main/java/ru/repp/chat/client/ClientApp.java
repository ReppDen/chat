package ru.repp.chat.client;

import org.apache.commons.lang3.StringUtils;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        String msg = null;
        c.doLogin();
        boolean keep = true;
        while (keep) {
            msg = inReader.readLine();
            if (msg.toUpperCase().startsWith(Command.QUIT.toString())) {
                c.quit();
                keep = false;
            }
            if (!StringUtils.isBlank(msg)) {
                c.send(msg);
            }
        }
        c.stop();
        System.exit(0);
    }


}
