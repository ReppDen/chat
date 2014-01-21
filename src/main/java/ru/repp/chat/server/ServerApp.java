package ru.repp.chat.server;

import ru.repp.chat.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Серверное приложение
 *
 * @author den
 * @since  1/12/14
 */
public class ServerApp {


    public static void main(String[] args) throws IOException {
        Server s = new BaseServer(Constants.PORT);
        s.start();
        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("User commands \"start\", \"stop\" and \"quit\" to rule the server");
        boolean stop = false;
        String cmd;
        while (!stop) {
            cmd = inReader.readLine().toUpperCase();
            if (cmd.matches("START")) {
                if (!s.isServerActive()) {
                    System.out.println("Starting server");
                    s.start();
                } else {
                    System.out.println("Server already started");
                }
            }
            if (cmd.matches("STOP")) {
                System.out.println("Stopping server");
                s.stop();
            }

            if (cmd.matches("QUIT")) {
                stop = true;
            }
        }
        s.stop();
    }
}
