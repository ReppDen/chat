package ru.repp.chat.client;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.repp.chat.server.Server;
import ru.repp.chat.server.ServerMock;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Utils;

import java.nio.channels.UnresolvedAddressException;

/**
 * Тесты для клиента
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class ClientTest {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9123;

    Server server;

    @Before
    public void startServer() {
        server = new ServerMock(PORT);
        server.start();
    }

    @After
    public void stopServer() {
        this.server.stop();
    }

    @Test
    public void testConnect() {
        Client c = new Client();
        Assert.assertFalse(c.isConnected());
        c.connect(HOSTNAME, PORT);
        Assert.assertTrue(c.isConnected());
        // cообщений не отправляллось
        Assert.assertEquals(server.getHistoryManager().getCount(), 0);
        c.stop();
        Assert.assertFalse(c.isConnected());
    }

    @Test
    public void testStop() {
        Client c = new Client();
        Assert.assertFalse(c.isConnected());
        c.stop();
        Assert.assertFalse(c.isConnected());
        c.connect(HOSTNAME, PORT);
        Assert.assertTrue(c.isConnected());
        c.stop();
        Assert.assertFalse(c.isConnected());
    }

    @Test
    public void testConnectFail() {
        Client c = new Client();
        Assert.assertFalse(c.isConnected());

        Throwable t = null;
        try {
            c.connect("non-existing-host", PORT);
        } catch (Throwable ex) {
            t = ex;
        }
        Assert.assertNotNull(t);
        Assert.assertThat(t, CoreMatchers.instanceOf(UnresolvedAddressException.class));
        Assert.assertFalse(c.isConnected());

        c.connect(HOSTNAME, PORT);
        c.stop();
    }

    @Test
    public void testLogin() {
        Client c = new Client();
        c.connect(HOSTNAME, PORT);
        String user = "Den";
        c.login(user);
        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.LOGIN)));
        c.stop();
    }

    @Test
    public void testLogedIn() {
        Client c = new Client();
        c.connect(HOSTNAME, PORT);
        String user = "Den";
        Assert.assertFalse(c.isLoggedIn());
        c.login(user);
        Assert.assertTrue(c.isLoggedIn());
        c.stop();
    }



    @Test
    public void testQuit() throws Exception {
        Client c = new Client();
        c.connect(HOSTNAME, PORT);
        Assert.assertTrue(c.isConnected());
        c.quit();
        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.QUIT)));
        c.stop();
    }

    @Test
    public void testGetUserName() {
        Client c = new Client();
        c.connect(HOSTNAME, PORT);
        String user = "Den";
        c.login(user);
        Assert.assertEquals(c.getUserName(), user);
        c.stop();
    }

    @Test
    public void testFoo() throws Exception {
        // TODO Remove!
    }

    @Test
    public void testHelp() throws Exception {
        Client c = new Client();
        c.connect(HOSTNAME, PORT);
        c.login("Den");
        c.help();
        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.HELP)));
        c.stop();
    }

    @Test
    public void testList() throws Exception {
        Client c = new Client();
        c.connect(HOSTNAME, PORT);
        c.login("Den");
        c.list();
        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.LIST)));
        c.stop();
    }

    @Test
    public void testSend() throws Exception {
        Client c = new Client();
        c.connect(HOSTNAME, PORT);
        c.login("Den");
        c.send("Hello");
        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.SEND)));
        c.stop();
    }
}
