package ru.repp.chat.server;

import org.junit.Assert;
import org.junit.Test;

/**
 * Тесты для сервера
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class ServerImplTest {

    /**
     * тест запуска сервера
     */
    @Test
    public void serverStartupTest() {
        Server s = new ServerImpl();
        Assert.assertFalse(s.isServerActive());

        s.start();
        Assert.assertTrue(s.isServerActive());

        s.stop();
        Assert.assertFalse(s.isServerActive());
    }

}
