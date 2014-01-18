package ru.repp.chat.client;

import org.junit.Assert;
import org.junit.Test;
import ru.repp.chat.server.Server;

import java.io.OutputStreamWriter;

/**
 * Тесты для клиента
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class ClientTest {

    private static final int TIMEOUT = 3000;
    /**
     * тест тоединения с сервером
     */
    @Test
    public void testClientConnection() {
        Server s = startServer();

        Client c = new Client();
        Assert.assertFalse(c.isConnected());
        c.connect();

        Assert.assertTrue(c.isConnected());

        c.stop();
        stopServer(s);
    }

    /**
     * тест переподключения к серверу одним клиентом
     */
    @Test
    public void testReconnect() {
        Server s = startServer();

        Client c = new Client();
        Assert.assertFalse(c.isConnected());
        c.connect();

        Assert.assertTrue(c.isConnected());

        c.stop();
        Assert.assertFalse(c.isConnected());

        c.connect();
        Assert.assertTrue(c.isConnected());

        c.stop();
        Assert.assertFalse(c.isConnected());

        stopServer(s);
    }

    /**
     * подключение нескольких клиентов
     * @throws Exception
     */
    @Test
    public void testFewClients() throws Exception {
        Server s = startServer();
        int n = 3;
        Client[] clients = new Client[n];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = new Client();
            clients[i].connect();
        }

        Assert.assertEquals(s.getSessionsCount(),3);

        for (Client c : clients) {
            c.stop();
        }

        sleep();
        Assert.assertEquals(s.getSessionsCount(),0);

        s.stop();

    }

    @Test
    public void testLogin() throws Exception {
        Server s = startServer();

        Client c = new Client();
        OutputStreamWriter writer = new OutputStreamWriter(System.out);
//        c.setResponseHandler(new BuffReaderMessageHandler(writer));
        c.connect();

        String user = "Den";
        String resp = c.login(user);

        Assert.assertEquals(s.getAuthorizedClientsCount(), 1);

        Assert.assertEquals(c.getUserName(), user);

        c.stop();

        stopServer(s);
    }

    @Test
    public void testDoubleLogin() throws Exception {
        Server s = startServer();

        Client c = new Client();
        c.connect();

        String[] users = new String [] {"Den", "Vasya"};

        Assert.assertEquals(s.getAuthorizedClientsCount(), 0);
        for (String user : users) {
            c.login(user);
        }
        Assert.assertEquals(s.getAuthorizedClientsCount(), 1);

        Assert.assertEquals(c.getUserName(), users[0]);

        sleep();
        c.stop();
        Assert.assertEquals(s.getAuthorizedClientsCount(), 0);

        stopServer(s);
    }

    private void sleep() {
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFewClientsLogin() throws Exception {
        Server s = startServer();

        int n = 3;
        String baseName = "Den";
        Client[] clients = new Client[n];


        Assert.assertEquals(s.getSessionsCount(), 0);
        Assert.assertEquals(s.getAuthorizedClientsCount(), 0);
        for (int i = 0; i < clients.length; i++) {
            clients[i] = new Client();
            clients[i].connect();
            clients[i].login(baseName + i);
        }

        Assert.assertEquals(s.getSessionsCount(), n);
        Assert.assertEquals(s.getAuthorizedClientsCount(), n);

        for (int i = 0; i < clients.length; i++) {
            Assert.assertEquals(clients[i].getUserName(), baseName + i);
            clients[i].stop();
        }

        stopServer(s);
    }

    @Test
    public void testSameNameClientsLogin() throws Exception {
        Server s = startServer();

        Client c = new Client();
        c.connect();

        Assert.assertEquals(s.getAuthorizedClientsCount(), 0);
        String user = "Den";
        c.login(user);

        Assert.assertEquals(s.getAuthorizedClientsCount(), 1);

        Client c2 = new Client();
        c.login(user);

        Assert.assertEquals(s.getAuthorizedClientsCount(), 1);

        Assert.assertEquals(c.getUserName(), user);
        Assert.assertNull(c2.getUserName());

        c.stop();

        stopServer(s);
    }

    @Test
    public void testQuitCmd() throws Exception {
        Server s = startServer();

        Client c = new Client();
        c.connect();
        c.login("Den");
        Assert.assertEquals(s.getSessionsCount(), 1);

        c.quit();
//
        Assert.assertEquals(s.getSessionsCount(), 0);

//        Assert.assertFalse(c.isConnected());
        c.stop();
        stopServer(s);

    }

    /**
     * иницилизирует новый сервер
     * @return
     */
    private Server startServer() {
       Server s = new Server();
        s.start();
        return s;
    }

    private void stopServer(Server s) {
        s.stop();
    }
}
