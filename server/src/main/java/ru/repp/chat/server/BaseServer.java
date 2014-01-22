package ru.repp.chat.server;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.repp.chat.server.history.HistoryManager;
import ru.repp.chat.server.history.MemoryHistoryManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Сервер чата
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class BaseServer implements Server{

    private final int port;

    IoAcceptor acceptor;
    private HistoryManager historyManager;
    private static final Logger LOG = LoggerFactory.getLogger(BaseServer.class);
    private final IoHandlerAdapter messageHandler;

    public BaseServer(int port) {
        this.port = port;
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        acceptor.getFilterChain().addLast("executor", new ExecutorFilter(1000));
        messageHandler = new ServerMessageHandler(new MemoryHistoryManager());
    }

    public int start() {
        if (acceptor.isActive()) {
            // нельзя натсраивать аццептор, пока он активен
            return 1;
        }
        try {

            acceptor.setHandler(getMessageHandler());
            acceptor.getSessionConfig().setReadBufferSize(2048);
            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
            acceptor.getSessionConfig().setUseReadOperation(true);
            acceptor.bind( new InetSocketAddress(port));
            historyManager = new MemoryHistoryManager();
            LOG.info("Server started. Port " + port);
            return 0;
        } catch (IOException ex) {
            LOG.error("Server startup failed", ex);
            return 1;
        }
    }

    private IoHandlerAdapter getMessageHandler() {
        return messageHandler;
    }

    public void stop() {
        acceptor.unbind();
        LOG.info("Server stopped");
    }

    /**
     * @return индикатор активности сервера
     */

    public boolean isServerActive() {
        return acceptor.isActive();
    }

    /**
     * @return количество подключенных сессий
     */

    public int getSessionsCount() {
        return acceptor.getManagedSessionCount();
    }

    /**
     * @return количество авторизованых клиентов
     */
    public int getAuthorizedClientsCount() {
        int count = 0;
        for (IoSession s :acceptor.getManagedSessions().values()) {
            count += s.getAttribute("user") != null ? 1 : 0;
        }
        return count;
    }


    
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

}
