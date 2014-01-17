package ru.repp.chat.client;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.ResponseHandler;

import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Класс клиент для чата
 *
 * @author den
 * @since 1/16/14
 */
public class Client {

    private static final String HOSTNAME = "localhost";
    private static final int PORT = 9123;
    private static final long CONNECT_TIMEOUT = 30*1000L; // 30 seconds


    private NioSocketConnector connector;
    private IoSession session;
    private BufferedReader buffReader;
    ResponseHandler messageHandler;
    public Client() {
        messageHandler = new SystemOutMessageHandler(); //todo DI
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.setHandler(new ClientHandler(messageHandler));
    }

    public void connect() {
        connect(HOSTNAME, PORT);
    }

    public void connect(String host, int port) {

        // создаем сессию
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        session = future.getSession();
        session.getConfig().setUseReadOperation(true);
    }

    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    /**
     * Останавливает клиента
     */
    public void stop() {
        session.getConfig().setUseReadOperation(false);
        session.close(true).awaitUninterruptibly();
    }

    /**
     * посылает указанную комманду серверу
     * @param cmd комманда
     * @param arg аргумент
     */
    public void sendCustomCmd(Command cmd, Object arg) {
        session.write(cmd.toString() + " " + arg.toString()).awaitUninterruptibly();
        session.read().awaitUninterruptibly();
    }

    /**
     * отправка комманды авторизации
     * @param name имя пользователя
     */
    public void login(String name) {
        sendCustomCmd(Command.LOGIN, name);
    }

    /**
     * @return Возворащает имя авторизованног опользователя, null если не авторизован
     */
    public String getUserName() {
        return session != null ? (String) session.getAttribute("user") : null;
    }

    /**
     * завершение работы клиента
     */
    public void quit() {
        sendCustomCmd(Command.QUIT, null);
    }
}
