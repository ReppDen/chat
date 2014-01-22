package ru.repp.chat.server;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.repp.chat.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author @Drepp
 * @since 22.01.14
 */
public class BaseServerTest {
    @Test
    public void testStart() throws Exception {
        Server s = new BaseServer(Constants.PORT);


        Assert.assertEquals(s.start(),0);

        // повторно не получится - порт занят
        Assert.assertEquals(s.start(), 1);
        s.stop();
    }

    @Test
    public void testStop() throws Exception {
        Server s = new BaseServer(Constants.PORT);
        s.stop();
    }

    @Test
    public void testIsServerActive() throws Exception {
        Server s = new BaseServer(Constants.PORT);
        Assert.assertFalse(s.isServerActive());
        s.start();
        Assert.assertTrue(s.isServerActive());
        s.stop();
        Assert.assertFalse(s.isServerActive());
    }

    @Test
    public void testGetSessionsCount() throws Exception {
        BaseServer s = Mockito.spy(new BaseServer(Constants.PORT));
        IoAcceptor acc = Mockito.mock(IoAcceptor.class);
        int count = 5;
        s.acceptor = acc;
        Mockito.when(acc.getManagedSessionCount()).thenReturn(count);
        int res = s.getSessionsCount();
        Assert.assertEquals(res, count);

    }

    @Test
    public void testGetAuthorizedClientsCount() throws Exception {
        BaseServer s = Mockito.spy(new BaseServer(Constants.PORT));
        IoAcceptor acc = Mockito.mock(IoAcceptor.class);
        s.acceptor = acc;
        Map<Long, IoSession> list = new HashMap<Long, IoSession>();
        IoSession session = Mockito.mock(IoSession.class);
        Mockito.doReturn("Vasya").when(session).getAttribute("user");
        list.put(1L, session);
        list.put(2L, session);
        Mockito.when(acc.getManagedSessions()).thenReturn(list);
        int res = s.getAuthorizedClientsCount();
        Assert.assertEquals(res, list.size());
    }

}
