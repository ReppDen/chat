package ru.repp.chat.server;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Сервер чата
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class ServerImpl implements Server {

    private static final int defaultPort = 9123;
    private int port;

    private final static Logger LOG = LoggerFactory.getLogger((ServerApp.class));

    IoAcceptor acceptor;

    public ServerImpl() {
        this(defaultPort);
    }

    public ServerImpl(int port) {
        this.port = port;
        acceptor = new NioSocketAcceptor();
    }

    @Override
    public void start() {
        try {
            acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName("UTF-8"))));
            acceptor.setHandler( new ServerHandler() );
            acceptor.getSessionConfig().setReadBufferSize( 2048 );
            acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
            acceptor.bind( new InetSocketAddress(port));
            LOG.info("ServerImpl started");
        } catch (IOException ex) {
            LOG.error("ServerImpl startup failed", ex);
        }
    }

    @Override
    public void stop() {
        acceptor.unbind();
    }

    /**
     * @return индикатор активности сервера
     */
    @Override
    public boolean isServerActive() {
        return acceptor.isActive();
    }

    /**
     * @return количество подключенных сессий
     */
    @Override
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


    @Override
    public void setHistoryManager(HistoryManager manager) {

    }

    @Override
    public HistoryManager getHistoryManager() {
        return null;
    }
}
