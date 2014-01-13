package ru.repp.chat.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Клиент чата
 *
 * @author den
 * @since 1/12/14
 */
public class Client {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9123;
    private static final long CONNECT_TIMEOUT = 30*1000L; // 30 seconds

    public static void main(String[] args) throws Throwable {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);

        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

        connector.getFilterChain().addLast("logger", new LoggingFilter());
        connector.setHandler(new ClientSessionHandler());
        IoSession session;

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
        future.awaitUninterruptibly();
        session = future.getSession();
        System.out.println("Enter your name:");
        String msg = br.readLine();
        session.write("/login " + msg);
        for (;;) {
            try {
                msg = br.readLine();
                if (StringUtils.isNotBlank(msg)) {
                    session.write(msg);
                }
            } catch (RuntimeIoException e) {
                System.err.println("Failed to connect.");
                e.printStackTrace();
                Thread.sleep(5000);
            }
        }
    }
}
