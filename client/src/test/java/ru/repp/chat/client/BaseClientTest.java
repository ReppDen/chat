package ru.repp.chat.client;

import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
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
import java.io.PrintStream;

/**
 * Клиентские тесты
 *
 * @author den
 * @since 1/22/14
 */
public class BaseClientTest extends TestCase {
    @Test
    public void testConnect() throws Exception {
        final ConnectFuture f = Mockito.mock(ConnectFuture.class);
        final IoSession s = Mockito.mock(IoSession.class);
        final IoSessionConfig conf = Mockito.mock(IoSessionConfig.class);
        Client c = new BaseClient() {
            @Override
            protected ConnectFuture getConnectFuture(String host, int port) {
                return f;
            }
        };

        Mockito.when(f.getSession()).thenReturn(s);
        Mockito.when(s.getConfig()).thenReturn(conf);
        Assert.assertEquals(c.connect(Constants.HOSTNAME, Constants.PORT), 0);
    }

    @Test
    public void testConnectFail() throws Exception {
        final ConnectFuture f = Mockito.mock(ConnectFuture.class);
        Client c = new BaseClient() {
            @Override
            protected ConnectFuture getConnectFuture(String host, int port) {
                return f;
            }
        };

        Mockito.when(f.getSession()).thenThrow(RuntimeIoException.class);
        Assert.assertEquals(c.connect(Constants.HOSTNAME, Constants.PORT), 1);
    }

    @Test
    public void testIsConnected() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        Client c = new BaseClient() {
            public IoSession getSession() {
                return sessionMock;
            }
        };

        Mockito.when(sessionMock.isConnected()).thenReturn(true);
        Assert.assertTrue(c.isConnected());
    }

    @Test
    public void testIsConnectedSessionNull() throws Exception {
        Client c = new BaseClient() {
            public IoSession getSession() {
                return null;
            }
        };
        Assert.assertFalse(c.isConnected());
    }

    @Test
    public void testIsConnectedNotConnected() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        Client c = new BaseClient() {
            public IoSession getSession() {
                return sessionMock;
            }
        };

        Mockito.when(sessionMock.isConnected()).thenReturn(false);
        Assert.assertFalse(c.isConnected());
    }

    @Test
    public void testStopClinetConnected() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        PrintStream out = Mockito.mock(PrintStream.class);
        BufferedReader in = Mockito.mock(BufferedReader.class);

        Client c = new BaseClient(out, in) {
            public IoSession getSession() {
                return sessionMock;
            }
        };
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
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        PrintStream out = Mockito.mock(PrintStream.class);
        BufferedReader in = Mockito.mock(BufferedReader.class);

        Client c = new BaseClient(out, in) {
            public IoSession getSession() {
                return sessionMock;
            }
        };
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
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        Client c = new BaseClient() {
            public IoSession getSession() {
                return sessionMock;
            }
        };

        String name = "Den";
        final StringBuilder stringBuilder = new StringBuilder();
        Mockito.when(sessionMock.write(Mockito.anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                stringBuilder.append(invocation.getArguments()[0]).toString();
                return Mockito.mock(WriteFuture.class);
            }
        });
        Mockito.when(sessionMock.write(Mockito.anyString()).awaitUninterruptibly()).thenReturn(null);
        Mockito.when(sessionMock.read()).thenReturn(Mockito.mock(ReadFuture.class));
        Mockito.when(sessionMock.read().awaitUninterruptibly()).thenReturn(Mockito.mock(ReadFuture.class));
        Mockito.when(sessionMock.read().awaitUninterruptibly().getMessage()).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return stringBuilder.toString();
            }
        });

        String loginRes = c.login(name);
        Assert.assertEquals(loginRes, stringBuilder.toString());
        Assert.assertTrue(loginRes.matches(Utils.getClinetCommandPattern(Command.LOGIN)));

    }

    @Test
    public void testGetUserName() throws Exception {
        final IoSession sessionMock = Mockito.mock(IoSession.class);
        Client c = new BaseClient() {
            public IoSession getSession() {
                return sessionMock;
            }
        };
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

    }

    @Test
    public void testGetSession() throws Exception {

    }

    @Test
    public void testIsLoggedIn() throws Exception {

    }

    @Test
    public void testHelp() throws Exception {

    }

    @Test
    public void testList() throws Exception {

    }

    @Test
    public void testSend() throws Exception {

    }

    @Test
    public void testSendRawText() throws Exception {

    }

    @Test
    public void testDoLogin() throws Exception {

    }
}
