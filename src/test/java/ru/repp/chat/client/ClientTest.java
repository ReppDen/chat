package ru.repp.chat.client;

import org.junit.Test;
import ru.repp.chat.server.Server;

import java.io.FileReader;

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
        c.setInReader(new FileReader("in.txt"));
        c.start();


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
}
