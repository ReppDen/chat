package ru.repp.chat.server;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.repp.chat.server.history.HistoryManager;
import ru.repp.chat.server.history.MemoryHistoryManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Сервер чата
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class BaseServer implements Server{

    private int port;
    IoAcceptor acceptor;
    HistoryManager historyManager;
    private static final Logger LOG = LoggerFactory.getLogger(BaseServer.class);
    private IoHandlerAdapter messageHandler;

    public BaseServer(int port) {
        this.port = port;
        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        messageHandler = new ServerMessageHandler(new MemoryHistoryManager());
    }

    public int start() {
        try {

            acceptor.setHandler(getMessageHandler());
            acceptor.getSessionConfig().setReadBufferSize(2048);
            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
            acceptor.getSessionConfig().setUseReadOperation(true);
            acceptor.bind( new InetSocketAddress(port));
            setHistoryManager(new MemoryHistoryManager());
            getLogger().info("Server started. Port " + port);
            return 0;
        } catch (IOException ex) {
            getLogger().error("Server startup failed", ex);
            return 1;
        }
    }

    protected Logger getLogger() {
        return LOG;
    }

    
    public IoHandlerAdapter getMessageHandler() {
        return messageHandler;
    }

    
    public void setMessageHandler(IoHandlerAdapter messageHandler) {
        this.messageHandler = messageHandler;
    }

    public void stop() {
        acceptor.unbind();
        getLogger().info("Server stopped");
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

    
    public void setHistoryManager(HistoryManager manager) {
        historyManager = manager;
    }

    
    public Map<Long, IoSession> getSessions() {
        return acceptor.getManagedSessions();
    }
}
