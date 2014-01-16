package ru.repp.chat.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import ru.repp.chat.utils.ChatCommand;

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
@Deprecated
public class ClientOld {

    private class ClientThread implements Runnable {

        public void run() {

            try {
                // инициализируем поток ввода
                BufferedReader buffReader =  new BufferedReader(getInReader());
                System.out.println("Enter your name:");
                String msg = buffReader.readLine();
                session.write(ChatCommand.LOGIN_CMD + " " + msg);
                while (isConnected()) {
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
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9123;
    private static final long CONNECT_TIMEOUT = 30*1000L; // 30 seconds


    InputStreamReader inReader;
    IoSession session;
    Thread chat;


    public ClientOld() {
        init();
    }

    public void start() throws InterruptedException {
        chat = new Thread(new ClientThread());
        chat.join();

    }

    public void stop() throws IOException {
        if (chat.isAlive()) {
            chat.stop();
        }
        session.close(true);
        System.out.println("Confirm exit");
        inReader.read();
        inReader.close();
    }

    private void init() {
        init(HOSTNAME, PORT, new InputStreamReader(System.in));
    }
    /**
     * создает и конфигурирует коннектор
     */
    private void init(String host, int port, InputStreamReader reader) {
        NioSocketConnector connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.setHandler(new ClientSessionHandler());
        // создаем сессию
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        session = future.getSession();

        inReader = reader;

    }

    public InputStreamReader getInReader() {
        return inReader;
    }

    public void setInReader(InputStreamReader inReader) {
        this.inReader = inReader;
    }

    public boolean isConnected() {
        return session.isConnected();
    }
}
