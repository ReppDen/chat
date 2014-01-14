package ru.repp.chat.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import ru.repp.chat.ChatCommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Клиент чата
 *
 * @author @Drepp
 * @since 14.01.14
 */
public class Client {
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9123;
    private static final long CONNECT_TIMEOUT = 30*1000L; // 30 seconds


    InputStreamReader inReader;
    IoSession session;

    public Client() {
        this(HOSTNAME, PORT);
    }

    public Client(String host, int port) {
        NioSocketConnector connector = createConnector();

        // создаем сессию
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        session = future.getSession();

        inReader = new InputStreamReader(System.in);
    }

    public void start() throws IOException{
        // инициализируем поток ввода
        BufferedReader buffReader =  new BufferedReader(getInReader());
        System.out.println("Enter your name:");
        String msg = buffReader.readLine();
        session.write(ChatCommand.LOGIN_CMD + " " + msg);
        while (true) {
            try {
                msg = buffReader.readLine();
                if (StringUtils.isNotBlank(msg)) {
                    session.write(msg);
                }
            } catch (RuntimeIoException e) {
                e.printStackTrace();
                break;
            }
        }

        stop();
    }

    public void stop() throws IOException {
        session.close(true);
        System.out.println("Confirm exit");
        inReader.read();
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

    public InputStreamReader getInReader() {
        return inReader;
    }

    public void setInReader(InputStreamReader inReader) {
        this.inReader = inReader;
    }
}
