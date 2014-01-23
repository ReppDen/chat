package ru.repp.chat.server;

import ru.repp.chat.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Серверное приложение
 *
 * @author den
 * @since 1/12/14
 */
class ServerApp {


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
                start(s);
            }
            if (cmd.matches("STOP")) {
                stop(s);
            }

            if (cmd.matches("QUIT")) {
                stop = true;
            }
        }
        stop(s);
        System.exit(0);
    }

    private static void start(Server s) {
        if (!s.isServerActive()) {
            System.out.println("Starting server");
            s.start();
        } else {
            System.out.println("Server already started");
        }
    }

    private static void stop(Server s) {
        if (s.isServerActive()) {
            System.out.println("Stopping server");
            s.stop();
        } else {
            System.out.println("Server already stopped");
        }
    }
}
