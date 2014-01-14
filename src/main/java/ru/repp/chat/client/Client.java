package ru.repp.chat.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.BufferedReader;
import java.io.IOException;
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

    public static void main(String[] args) throws IOException {
        NioSocketConnector connector = createConnector();

        // создаем сессию
        ConnectFuture future = connector.connect(new InetSocketAddress(HOSTNAME, PORT));
        future.awaitUninterruptibly();
        IoSession session = future.getSession();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your name:");
        String msg = br.readLine();
        session.write("/login " + msg);
        while (true) {
            try {
                msg = br.readLine();
                if (StringUtils.isNotBlank(msg)) {
                    session.write(msg);
                }
            } catch (RuntimeIoException e) {
                e.printStackTrace();
                break;
            }
        }

        System.out.println("Confirm exit");
        br.readLine();
    }

    /**
     * создает и конфигурирует коннектор
     */
    private static NioSocketConnector createConnector() {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.setHandler(new ClientSessionHandler());
        return connector;
    }
}
