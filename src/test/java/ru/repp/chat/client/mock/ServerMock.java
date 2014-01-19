package ru.repp.chat.client.mock;

import ru.repp.chat.server.BaseServer;

/**
 * Заглушка сервера
 *
 * @author den
 * @since 1/18/14
 */
public class ServerMock extends BaseServer {

    public ServerMock(int port) {
        super(port);
        setMessageHandler(new ServerMockHandler(ServerMock.this));
    }

}
