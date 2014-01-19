package ru.repp.chat.server;

import org.apache.mina.core.session.IoSession;
import org.junit.Assert;
import org.junit.Test;
import ru.repp.chat.client.BaseClient;
import ru.repp.chat.client.Client;
import ru.repp.chat.server.mock.ClientMock;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Utils;

/**
 * Тесты для сервера
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class BaseServerTest {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9123;

    @Test
    public void testStart() {
        Server s = new BaseServer(PORT);
        s.start();
        s.stop();
    }

    @Test
    public void testStop() throws Exception {
        Server s = new BaseServer(PORT);
        s.stop();

        s.start();
        s.stop();
    }

    @Test
    public void testIsActive() throws Exception {
        Server s = new BaseServer(PORT);
        Assert.assertFalse(s.isServerActive());

        s.start();
        Assert.assertTrue(s.isServerActive());

        s.stop();
        Assert.assertFalse(s.isServerActive());

    }

    @Test
    public void testGetSessionsCount() throws Exception {
        Server s = new BaseServer(PORT);
        s.start();

        Client c = new ClientMock();
        Client c2 = new ClientMock();

        c.connect(HOSTNAME, PORT);
        c2.connect(HOSTNAME, PORT);

        Assert.assertEquals(s.getSessionsCount(), 2);

        c.stop();
        c2.stop();

        s.stop();
    }

    @Test
    public void testGetAuthorizedClientsCount() throws Exception {
        Server s = new BaseServer(PORT);
        s.start();

        Client[] clients = new Client[3];

        for (int i = 0; i < clients.length; i++) {
            clients[i] = new BaseClient();
            clients[i].connect(HOSTNAME, PORT);
        }

        int index = 0;
        for (IoSession session :s.getSessions().values()) {
            s.getMessageHandler().messageReceived(session, Utils.makeCustomClientCmd(Command.LOGIN, "Den" + index++));
        }

        Assert.assertEquals(s.getAuthorizedClientsCount(), clients.length);

        for (int i = 0; i < clients.length; i++) {
            clients[i].stop();
        }

        s.stop();
    }
}
