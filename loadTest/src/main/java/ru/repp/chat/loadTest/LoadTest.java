package ru.repp.chat.loadTest;

import org.mockito.Mockito;
import ru.repp.chat.client.BaseClient;
import ru.repp.chat.client.Client;
import ru.repp.chat.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Random;
import java.util.UUID;

/**
 * Класс для запуска ручного нагрузончного тестирования
 * Соединяет, логинит 1000 клиентов, при вводу в консоль отправляет сообщение
 *
 * @author @Drepp
 * @since 22.01.14
 */
public class LoadTest {
    private boolean keepWork;

    public static void main(String[] args) throws Exception {

        LoadTest st = new LoadTest();
        System.exit(0);

    }

    public LoadTest() throws InterruptedException, IOException {
        System.out.println("Starting load test");
        BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(System.in)));
        String msg;
        do {
            System.out.println("Enter number of clients (more than zero)");
            msg = br.readLine() ;

        } while (!msg.matches("\\d+") || Integer.valueOf(msg) <= 0);
        Thread[] threads =  start(Integer.valueOf(msg));
        System.out.println("Type \"stop\" to stop clients");

        do {
            msg = br.readLine();
        } while (!msg.trim().equals("stop"));
        keepWork = false;
        System.out.println("waiting for threads");
        for (Thread t : threads) {
            t.join();
        }
        System.out.println("Stopped");
    }

    Thread[] start(Integer count) throws InterruptedException {
        keepWork = true;
        Thread[] threads = new Thread[count];
        for (int i = 0; i < count; i++) {
            threads[i] = new Thread(new ClientThread());
            threads[i].start();
        }
        return threads;
    }

    public synchronized boolean keepWork() {
        return keepWork;
    }

    private class ClientThread implements Runnable {

        @Override
        public void run() {
            Client c = null;
            try {
                PrintStream ws = Mockito.mock(PrintStream.class);
                Mockito.doNothing().when(ws).println(Mockito.any());
                BufferedReader br = Mockito.mock(BufferedReader.class);
                c = new BaseClient(ws, br);
                c.connect(Constants.HOSTNAME, Constants.PORT);

                String username = "user_" + UUID.randomUUID();
                c.login(username);

                while (keepWork()) {
                    String msg = "Hi everybody!";
                    Thread.sleep((Math.abs(new Random().nextInt()) % 20) *1000);
                    c.send(msg);
                }
                c.stop();
            } catch (Throwable t) {
            } finally {
                if (c != null) {
                    try {
                        c.stop();
                    } catch (Exception e) {
                    }
                }

            }
        }
    }
}
