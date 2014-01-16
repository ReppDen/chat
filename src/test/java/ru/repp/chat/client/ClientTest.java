package ru.repp.chat.client;

import org.junit.Assert;
import org.junit.Test;
import ru.repp.chat.server.Server;

import java.io.FileReader;
import java.io.IOException;

/**
 * Тесты для клиента
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class ClientTest {

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

        Assert.assertEquals(s.getSessionsCount(),0);

        s.stop();

    }

    @Test
    public void testLogin() throws Exception {
        Server s = startServer();

        Client c = new Client();
        c.connect();

        c.login("Den");

        Assert.assertEquals(s.getAuthorizedClientsCount(), 1);

        c.stop();

        stopServer(s);
    }

    @Test
    public void testSendCmd() throws Exception {
        Server s = startServer();

        Client c = new Client();
//        FileReader f = new FileReader("src/test/resources/client/simple.txt");

        c.connect();

//        c.sendCmd(Command.SEND, "hi");

        // TODO

        stopServer(s);

    }


    @Test
    public void test100Client() throws IOException, InterruptedException {
        Server s = startServer();
        ClientOld[] cl = new ClientOld[100];
        for (int i = 0; i < cl.length; i++) {
            cl[i] = new ClientOld();
            cl[i].setInReader(new FileReader("src/test/resources/client/simple2.txt"));
            Assert.assertTrue(cl[i].isConnected());
            cl[i].start();
            Assert.assertFalse(cl[i].isConnected());
        }

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
