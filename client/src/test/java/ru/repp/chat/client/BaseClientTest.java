package ru.repp.chat.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Constants;
import ru.repp.chat.utils.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;


/**
 * Клиентские тесты
 *
 * @author den
 * @since 1/22/14
 */
public class BaseClientTest {
    @Test
    public void testConnect() throws Exception {
        final IoSession s = Mockito.mock(IoSession.class);
        final IoSessionConfig conf = Mockito.mock(IoSessionConfig.class);
        final ConnectFuture f = Mockito.mock(ConnectFuture.class);
        BaseClient c = Mockito.spy(new BaseClient());

        Mockito.doReturn(f).when(c).getConnectFuture(Mockito.anyString(), Mockito.anyInt());


        Mockito.when(f.getSession()).thenReturn(s);
        Mockito.when(s.getConfig()).thenReturn(conf);
        Assert.assertEquals(c.connect(Constants.HOSTNAME, Constants.PORT), 0);
    }

    @Test
    public void testConnectFail() throws Exception {
        final ConnectFuture f = Mockito.mock(ConnectFuture.class);
        BaseClient c = Mockito.spy(new BaseClient());

        Mockito.doReturn(f).when(c).getConnectFuture(Mockito.anyString(), Mockito.anyInt());


        Mockito.when(f.getSession()).thenThrow(RuntimeIoException.class);
        Assert.assertEquals(c.connect(Constants.HOSTNAME, Constants.PORT), 1);
    }

    @Test
    public void testIsConnected() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        BaseClient c = Mockito.spy(new BaseClient());

        Mockito.doReturn(sessionMock).when(c).getSession();

        Mockito.when(sessionMock.isConnected()).thenReturn(true);
        Assert.assertTrue(c.isConnected());
    }

    @Test
    public void testIsConnectedSessionNull() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        BaseClient c = Mockito.spy(new BaseClient());

        Mockito.doReturn(sessionMock).when(c).getSession();
        Assert.assertFalse(c.isConnected());
    }

    @Test
    public void testIsConnectedNotConnected() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        BaseClient c = Mockito.spy(new BaseClient());

        Mockito.doReturn(sessionMock).when(c).getSession();

        Mockito.when(sessionMock.isConnected()).thenReturn(false);
        Assert.assertFalse(c.isConnected());
    }

    @Test
    public void testStopClinetConnected() throws Exception {
        PrintStream out = Mockito.mock(PrintStream.class);
        BufferedReader in = Mockito.mock(BufferedReader.class);

        final IoSession sessionMock = Mockito.mock(IoSession.class);
        BaseClient c = Mockito.spy(new BaseClient(out,in));

        Mockito.doReturn(sessionMock).when(c).getSession();

        Mockito.when(sessionMock.isConnected()).thenReturn(true);
        Mockito.when(sessionMock.close(false)).thenReturn(Mockito.mock(CloseFuture.class));
        Mockito.when(sessionMock.close(false).awaitUninterruptibly()).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Mockito.when(sessionMock.isConnected()).thenReturn(false);
                return null;
            }
        });

        Assert.assertTrue(c.isConnected());
        c.stop();
        Assert.assertFalse(c.isConnected());
    }

    @Test
    public void testStopClinetNotConnected() throws Exception {
        PrintStream out = Mockito.mock(PrintStream.class);
        BufferedReader in = Mockito.mock(BufferedReader.class);

        final IoSession sessionMock = Mockito.mock(IoSession.class);
        BaseClient c = Mockito.spy(new BaseClient(out,in));

        Mockito.doReturn(sessionMock).when(c).getSession();
        Mockito.when(sessionMock.isConnected()).thenReturn(false);
        Mockito.when(sessionMock.close(false)).thenReturn(Mockito.mock(CloseFuture.class));
        Mockito.when(sessionMock.close(false).awaitUninterruptibly()).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Mockito.when(sessionMock.isConnected()).thenReturn(false);
                return null;
            }
        });

        Assert.assertFalse(c.isConnected());
        c.stop();
        Assert.assertFalse(c.isConnected());
    }

    @Test
    public void testLogin() throws Exception {
        Client c = Mockito.spy(new BaseClient());

        final StringBuilder stringBuilder = new StringBuilder();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                stringBuilder.append(invocation.getArguments()[0]);
                return null;
            }
        }).when(c).sendRawText(Mockito.anyString());

        c.login("Den");

        // соответствует шаблону
        Assert.assertTrue(stringBuilder.toString().matches(Utils.getClinetCommandPattern(Command.LOGIN)));

    }

    @Test
    public void testGetUserName() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        BaseClient c = Mockito.spy(new BaseClient());

        Mockito.doReturn(sessionMock).when(c).getSession();

        String name = "den";
        Mockito.when(sessionMock.getAttribute("user")).thenReturn(name);

        Assert.assertEquals(c.getUserName(), name);
        Mockito.when(sessionMock.getAttribute("user")).thenReturn(StringUtils.reverse(name));
        Assert.assertNotEquals(c.getUserName(), name);
    }

    @Test
    public void testGetUserNameIsNull() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        Client c = new BaseClient() {
            public IoSession getSession() {
                return sessionMock;
            }
        };
        Mockito.when(sessionMock.getAttribute("user")).thenReturn(null);

        Assert.assertNull(c.getUserName());
    }

    @Test
    public void testQuit() throws Exception {
        Client c = Mockito.spy(new BaseClient());

        final StringBuilder stringBuilder = new StringBuilder();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                stringBuilder.append(invocation.getArguments()[0]);
                return null;
            }
        }).when(c).sendRawText(Mockito.anyString());

        c.quit();

        // соответствует шаблону
        Assert.assertTrue(stringBuilder.toString().matches(Utils.getClinetCommandPattern(Command.QUIT)));
    }


    @Test
    public void testHelp() throws Exception {
        Client c = Mockito.spy(new BaseClient());

        final StringBuilder stringBuilder = new StringBuilder();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                stringBuilder.append(invocation.getArguments()[0]);
                return null;
            }
        }).when(c).sendRawText(Mockito.anyString());

        c.help();

        // соответствует шаблону
        Assert.assertTrue(stringBuilder.toString().matches(Utils.getClinetCommandPattern(Command.HELP)));
    }

    @Test
    public void testList() throws Exception {
        Client c = Mockito.spy(new BaseClient());

        final StringBuilder stringBuilder = new StringBuilder();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                stringBuilder.append(invocation.getArguments()[0]);
                return null;
            }
        }).when(c).sendRawText(Mockito.anyString());

        c.list();

        // соответствует шаблону
        Assert.assertTrue(stringBuilder.toString().matches(Utils.getClinetCommandPattern(Command.LIST)));
    }

    @Test
    public void testSend() throws Exception {
        Client c = Mockito.spy(new BaseClient());

        final StringBuilder stringBuilder = new StringBuilder();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                stringBuilder.append(invocation.getArguments()[0]);
                return null;
            }
        }).when(c).sendRawText(Mockito.anyString());

        String cmd = "test";
        c.send(cmd);

        // соответствует шаблону
        Assert.assertTrue(stringBuilder.toString().matches(Utils.getClinetCommandPattern(Command.SEND)));
    }

    @Test
    public void testSendRawText() throws Exception {
        Client c = Mockito.spy(new BaseClient());

        final StringBuilder stringBuilder = new StringBuilder();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                stringBuilder.append(invocation.getArguments()[0]);
                return null;
            }
        }).when(c).sendRawText(Mockito.anyString());

        String cmd = "/SEND test";
        c.sendRawText(cmd);

        // комманда ушла кооректно
        Assert.assertEquals(cmd, stringBuilder.toString());
        // соответствует шаблону
        Assert.assertTrue(cmd.matches(Utils.getClinetCommandPattern(Command.SEND)));
    }

    @Test
    public void testSendRawTextIncorrectCommand() throws Exception {
        Client c = Mockito.spy(new BaseClient());

        final StringBuilder stringBuilder = new StringBuilder();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                stringBuilder.append(invocation.getArguments()[0]);
                return null;
            }
        }).when(c).sendRawText(Mockito.anyString());

        final String cmdText = "/SOMETHING test";
        c.sendRawText(cmdText);
        // комманда ушла кооректно
        Assert.assertEquals(cmdText, stringBuilder.toString());

        // не соответсвует ни одной комманде
        boolean anyMatch = false;
        for (Command command : Command.values()) {
            anyMatch |= stringBuilder.toString().matches(Utils.getClinetCommandPattern(command));
        }
        Assert.assertFalse(anyMatch);
    }

    @Test
    public void testDoLogin() throws Exception {
        PrintStream out = Mockito.mock(PrintStream.class);
        BufferedReader in = Mockito.spy(new BufferedReader(new FileReader("src/test/resources/testDoLogin.txt")));
        Client c = Mockito.spy(new BaseClient(out, in));


        Mockito.doReturn(null).when(c).login(Mockito.anyString());
        Mockito.doNothing().when(c).stop();

        c.doLogin();
        Mockito.verify(c, Mockito.times(1)).login(Mockito.anyString());
        Mockito.verify(in, Mockito.times(1)).readLine();
        Mockito.verify(c, Mockito.times(0)).stop();
        in.close();
    }

    @Test
    public void testDoLoginWrongNickNames() throws Exception {
        PrintStream out = Mockito.mock(PrintStream.class);
        BufferedReader in = Mockito.spy(new BufferedReader(new FileReader("src/test/resources/testDoLoginWrongNickNames.txt")));
        Client c = Mockito.spy(new BaseClient(out, in));

        Mockito.doReturn(null).when(c).login(Mockito.anyString());
        Mockito.doNothing().when(c).stop();

        c.doLogin();
        Mockito.verify(c, Mockito.times(1)).login(Mockito.anyString());
        Mockito.verify(in, Mockito.times(5)).readLine();
        Mockito.verify(c, Mockito.times(0)).stop();
        in.close();
    }


    @Test
    public void testDoLoginQuit() throws Exception {
        PrintStream out = Mockito.mock(PrintStream.class);
        BufferedReader in = Mockito.spy(new BufferedReader(new FileReader("src/test/resources/testDoLoginQuit.txt")));
        Client c = Mockito.spy(new BaseClient(out, in));

        Mockito.doReturn(null).when(c).login(Mockito.anyString());
        Mockito.doNothing().when(c).stop();

        c.doLogin();
        Mockito.verify(c, Mockito.times(0)).login(Mockito.anyString());
        Mockito.verify(in, Mockito.times(6)).readLine();
        Mockito.verify(c, Mockito.times(1)).stop();
        in.close();
    }


    @Test
    public void testIsLoggedIn() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        Client c = new BaseClient() {
            public IoSession getSession() {
                return sessionMock;
            }
        };

        String user = "Den";
        Mockito.when(sessionMock.getAttribute("user")).thenReturn(user);
        Assert.assertTrue(c.isLoggedIn());
    }

    @Test
    public void testIsLoggedInNoUser() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        Client c = new BaseClient() {
            public IoSession getSession() {
                return sessionMock;
            }
        };

        Mockito.when(sessionMock.getAttribute("user")).thenReturn(null);
        Assert.assertFalse(c.isLoggedIn());
    }

    @Test
    public void testIsLoggedInNoUserNoSession() throws Exception {
        Client c = new BaseClient() {
            public IoSession getSession() {
                return null;
            }
        };
        Assert.assertFalse(c.isLoggedIn());
    }
}
