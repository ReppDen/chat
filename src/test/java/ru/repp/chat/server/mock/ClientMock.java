package ru.repp.chat.server.mock;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import ru.repp.chat.client.Client;
import ru.repp.chat.utils.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

/**
 * Заглушка для тестов, нужен только для создания сессии
 *
 * @author den
 * @since 1/19/14
 */
public class ClientMock implements Client {



    private NioSocketConnector connector;
    private IoSession session;

    public ClientMock() {
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(Constants.CONNECT_TIMEOUT);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.getFilterChain().addLast("executor", new ExecutorFilter(Executors.newSingleThreadExecutor()));
        connector.setHandler(new IoHandlerAdapter());
    }


    @Override
    public void connect(String host, int port) throws Exception {
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        session = future.getSession();
        session.getConfig().setUseReadOperation(true);
    }

    @Override
    public boolean isConnected() throws Exception {
        return session.isConnected();
    }

    @Override
    public void stop() throws Exception {
        if (session != null) {
            session.getConfig().setUseReadOperation(false);
            session.close(false).awaitUninterruptibly();
        };
    }

    @Override
    public void login(String name) throws Exception {

    }

    @Override
    public String getUserName() throws Exception {
        return null;
    }

    @Override
    public void quit() throws Exception {

    }

    @Override
    public IoSession getSession() throws Exception {
        return null;
    }

    @Override
    public boolean isLoggedIn() throws Exception {
        return false;
    }

    @Override
    public void help() {

    }

    @Override
    public void list() {

    }

    @Override
    public void send(String msg) {

    }

    @Override
    public void doLogin() throws IOException {

    }

}
