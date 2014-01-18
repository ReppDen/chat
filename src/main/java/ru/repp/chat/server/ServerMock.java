package ru.repp.chat.server;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Заглушка сервера
 *
 * @author den
 * @since 1/18/14
 */
public class ServerMock implements Server {
    private NioSocketAcceptor acceptor;
    private int port = 0;
    private HistoryManager historyManager;

    public ServerMock(int port) {
        this.acceptor = new NioSocketAcceptor();
        this.port = port;
        setHistoryManager(new MemoryHistoryManager());
    }

    @Override
    public void start() {
        try {
            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
            acceptor.setHandler(new ServerMockHandler(this));
            acceptor.getSessionConfig().setReadBufferSize(2048);
            acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
            acceptor.bind(new InetSocketAddress(port));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void stop() {
        acceptor.unbind();
    }

    @Override
    public boolean isServerActive() {
        return acceptor.isActive();
    }

    @Override
    public int getSessionsCount() {
        return 0;
    }

    @Override
    public int getAuthorizedClientsCount() {
        return 0;
    }

    @Override
    public void setHistoryManager(HistoryManager manager) {
        historyManager = manager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

//    @Override
    public void broadcast(String msg) {
        for (IoSession session :acceptor.getManagedSessions().values()) {
            session.write(msg).awaitUninterruptibly();
        }
    }
}
