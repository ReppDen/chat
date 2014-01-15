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

    @Test
    public void testClientConnection() throws Exception {
        Server s = startServer();

        Client c = new Client();
        c.setInReader(new FileReader("src/test/resources/client/simple.txt"));


        Assert.assertTrue(c.isConnected());
        c.start();
        Assert.assertFalse(c.isConnected());
//        c.stop();

        stopServer(s);
    }

    @Test
    public void testFewClientConnections() throws Exception {
        Server s = startServer();

        Client c = new Client();
        c.setInReader(new FileReader("src/test/resources/client/simple.txt"));

        Client c2 = new Client();
        c2.setInReader(new FileReader("src/test/resources/client/simple2.txt"));


        Assert.assertTrue(c.isConnected());
        Assert.assertTrue(c2.isConnected());
        c.start();
        c2.start();
        Assert.assertFalse(c.isConnected());
        Assert.assertFalse(c2.isConnected());


        stopServer(s);

    }


    @Test
    public void test100Client() throws IOException {
        Server s = startServer();
        Client[] cl = new Client[100];
        for (int i = 0; i < cl.length; i++) {
            cl[i] = new Client();
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
