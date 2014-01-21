//package ru.repp.chat.client;
//
//import org.hamcrest.CoreMatchers;
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import ru.repp.chat.client.mock.ServerMock;
//import ru.repp.chat.server.Server;
//import ru.repp.chat.utils.Command;
//import ru.repp.chat.utils.Constants;
//import ru.repp.chat.utils.Response;
//import ru.repp.chat.utils.Utils;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.PrintStream;
//import java.nio.channels.UnresolvedAddressException;
//
//
///**
// * Тесты для клиента
// * Проверяется корректность клиентских комманд, соединение с сервером
// *
// * @author @Drepp
// * @since 14.01.14
// */
//public class BaseClientTest {
//
//
//    Server server;
//
//    @Before
//    public void startServer() {
//        server = new ServerMock(Constants.PORT);
//        server.start();
//    }
//
//    @After
//    public void stopServer() {
//        this.server.stop();
//    }
//
//    @Test
//    public void testConnect() throws Exception {
//        Client c = new BaseClient();
//        Assert.assertFalse(c.isConnected());
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        Assert.assertTrue(c.isConnected());
//        // cообщений не отправляллось
//        Assert.assertEquals(server.getHistoryManager().getCount(), 0);
//        c.stop();
//        Assert.assertFalse(c.isConnected());
//    }
//
//    @Test
//    public void testStop()  throws Exception {
//        Client c = new BaseClient();
//        Assert.assertFalse(c.isConnected());
//        c.stop();
//        Assert.assertFalse(c.isConnected());
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        Assert.assertTrue(c.isConnected());
//        c.stop();
//        Assert.assertFalse(c.isConnected());
//    }
//
//    @Test
//    public void testLogin() throws Exception {
//        Client c = new BaseClient();
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        String user = "Den";
//        c.login(user);
//        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.LOGIN)));
//        c.stop();
//    }
//
//    @Test
//    public void testConstructor() throws Exception {
//        String fileName = "file.txt";
//        Client c = new BaseClient(new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName))), null); // TODO дописать тест
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        c.login("Den");
//        c.stop();
//        File file = new File(fileName);
//        Assert.assertNotEquals(file.length(), 0);
//        Assert.assertTrue(file.delete());
//
//    }
//
//    @Test
//    public void testConnectFail() throws Exception {
//        Client c = new BaseClient();
//        Assert.assertFalse(c.isConnected());
//
//        Throwable t = null;
//        try {
//            c.connect("non-existing-host", Constants.PORT);
//        } catch (Throwable ex) {
//            t = ex;
//        }
//        Assert.assertNotNull(t);
//        Assert.assertThat(t, CoreMatchers.instanceOf(UnresolvedAddressException.class));
//        Assert.assertFalse(c.isConnected());
//
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        c.stop();
//    }
//
//
//    @Test
//    public void testLogedIn() throws Exception {
//        Client c = new BaseClient();
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        String user = "Den";
//        Assert.assertFalse(c.isLoggedIn());
//        c.login(user);
//        Assert.assertTrue(c.isLoggedIn());
//        c.stop();
//    }
//
//
//
//    @Test
//    public void testQuit() throws Exception {
//        Client c = new BaseClient();
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        Assert.assertTrue(c.isConnected());
//        c.quit();
//        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.QUIT)));
//        c.stop();
//    }
//
//    @Test
//    public void testGetUserName() throws Exception {
//        Client c = new BaseClient();
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        String user = "Den";
//        c.login(user);
//        Assert.assertEquals(c.getUserName(), user);
//        c.stop();
//    }
//
//    @Test
//    public void testHelp() throws Exception {
//        Client c = new BaseClient();
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        c.login("Den");
//        c.help();
//        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.HELP)));
//        c.stop();
//    }
//
//    @Test
//    public void testList() throws Exception {
//        Client c = new BaseClient();
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        c.login("Den");
//        c.list();
//        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.LIST)));
//        c.stop();
//    }
//
//    @Test
//    public void testSend() throws Exception {
//        Client c = new BaseClient();
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//        c.login("Den");
//        c.send("Hello");
//        Assert.assertTrue(server.getHistoryManager().getLast(1).get(0).matches(Utils.getClinetCommandPattern(Command.SEND)));
//        c.stop();
//    }
//
//    @Test
//    public void testLogin2() throws Exception {
//        Client c = new BaseClient();
//
//        c.connect(Constants.HOSTNAME, Constants.PORT);
//
//        String name = "Den";
//        String res = c.login(name);
//
//        Assert.assertEquals(Utils.makeCustomServerCmd(Command.LOGIN, Response.OK, name),res);
//
//        c.stop();
//
//    }
//
//}
