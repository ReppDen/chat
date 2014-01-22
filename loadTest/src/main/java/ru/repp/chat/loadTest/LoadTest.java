package ru.repp.chat.loadTest;

import org.mockito.Mockito;
import ru.repp.chat.client.BaseClient;
import ru.repp.chat.client.Client;
import ru.repp.chat.utils.Constants;

import java.io.*;
import java.util.UUID;

/**
 * Класс для запуска ручного нагрузончного тестирования
 * Соединяет, логинит 1000 клиентов, при вводу в консоль отправляет сообщение
 *
 * @author @Drepp
 * @since 22.01.14
 */
public class LoadTest {
    private static final int n = 1000;

    private boolean keepWork;

    public static void main(String[] args) throws Exception {
        System.out.println("Starting " + n + " clients. Login is automatic.");
        System.out.println("Type somesinng in console and one client will send to server or type \"stop\" to ");
        LoadTest st = new LoadTest();
        st.start();
        BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(System.in)));
        String msg;
        do {
            msg = br.readLine();
        } while (!msg.trim().equals("stop"));
        System.out.println("Stopped");
        System.exit(0);
    }

    void start() throws InterruptedException {
        keepWork = true;
        for (int i = 0; i < n; i++) {
            Thread t = new Thread(new ClientThread());
            t.start();
        }

    }

    public synchronized boolean keepWork() {
        return keepWork;
    }

    private class ClientThread implements Runnable {

        @Override
        public void run() {
            Client c = null;
            try {
                PrintStream ws = Mockito.mock(PrintStream.class);//new PrintStream(new FileOutputStream(fileName));
                Mockito.doNothing().when(ws).println(Mockito.any());
//                ws = new PrintStream(new FileOutputStream(fileName));
                BufferedReader br = Mockito.mock(BufferedReader.class);
//                BufferedReader br = new BufferedReader(new BufferedReader(new InputStreamReader(System.in)));
                c = new BaseClient(ws, br);
                c.connect(Constants.HOSTNAME, Constants.PORT);

                String username = "user_" + UUID.randomUUID();
                c.login(username);

                while (keepWork()) {
                    String msg = "Hi everybody!";
                    Thread.sleep(5000);
                    c.send(msg);
                }
                c.stop();
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (c != null) {
                    try {
                        c.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
