package ru.repp.chat.client;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.ResponseHandler;

import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

/**
 * Класс клиент для чата
 *
 * @author den
 * @since 1/16/14
 */
public class Client {

    private static final long CONNECT_TIMEOUT = 30*1000L; // 30 seconds


    private NioSocketConnector connector;
    private IoSession session;
    private BufferedReader buffReader;
    ResponseHandler responeHandler;
    public Client() {
        responeHandler = new SystemOutMessageHandler(); //todo DI
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.getFilterChain().addLast("executor", new ExecutorFilter(Executors.newSingleThreadExecutor()));
        connector.setHandler(new ClientHandler(responeHandler));
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
        if (session != null) {
            session.getConfig().setUseReadOperation(false);
            session.close(false).awaitUninterruptibly();
        }
    }

    /**
     * посылает указанную комманду серверу
     * @param cmd комманда
     * @param arg аргумент
     */
    private void sendCustomCmd(Command cmd, Object arg) {
        session.write(cmd.toString() + " " + arg).awaitUninterruptibly();
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

    public IoSession getSession() {
        return session;
    }
    public boolean isLoggedIn() {
        return session != null && session.getAttribute("user") != null;
    }

    public void help() {
        sendCustomCmd(Command.HELP, null);
    }

    public void list() {
        sendCustomCmd(Command.LIST, null);
    }

    public void send(String msg) {
        sendCustomCmd(Command.SEND, msg);
    }
}
