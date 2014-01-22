package ru.repp.chat.server;

import ru.repp.chat.client.BaseClient;
import ru.repp.chat.client.Client;
import ru.repp.chat.utils.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author @Drepp
 * @since 22.01.14
 */
public class StressTest {

    public static void main(String[] args) throws Exception {

        int n = 1000;
        Client[] clients = new Client[n];

        for (int i = 0; i < n; i++) {
            clients[i] = new BaseClient();
            clients[i] .connect(Constants.HOSTNAME, Constants.PORT);
        }

        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        r.readLine();
        int index = 0;
        for (Client c : clients) {
            c.login("User" + index++);
        }

        while (r.readLine().trim() != null) {
            index = 0;
            for (Client c : clients) {
                c.send("Hi from user" + index++);
            }
        }




        for (Client c : clients) {
            c.stop();
        }
    }
}
