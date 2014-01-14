package ru.repp.chat.server;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
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
public class Server {

    private static final int defaultPort = 9123;
    private int port;

    private final static Logger LOG = LoggerFactory.getLogger((ServerApp.class));

    IoAcceptor acceptor;

    public Server() {
        this(defaultPort);
    }

    public Server(int port) {
        this.port = port;
        acceptor = new NioSocketAcceptor();
    }

    public void start() {
        try {
            acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName("UTF-8"))));
            acceptor.setHandler(  new ServerChatMessageHandler() );
            acceptor.getSessionConfig().setReadBufferSize( 2048 );
            acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
            acceptor.bind( new InetSocketAddress(port));
            LOG.info("Server started");
        } catch (IOException ex) {
            LOG.error("Server startup failed", ex);
        }
    }

    public void stop() {
        acceptor.unbind();
    }

    public boolean isServerActive() {
        return acceptor.isActive();
    }
}
