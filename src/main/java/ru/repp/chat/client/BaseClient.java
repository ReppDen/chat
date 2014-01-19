package ru.repp.chat.client;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import ru.repp.chat.utils.Command;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

/**
 * Класс клиент для чата
 *
 * @author den
 * @since 1/16/14
 */
public class BaseClient implements Client {

    private static final long CONNECT_TIMEOUT = 30*1000L; // 30 seconds


    private NioSocketConnector connector;
    private IoSession session;
    private PrintStream printStream;
    /**
     * Создает клиента чата <br/>
     * по умолчанию выводит все сообщения в System.out
     */
    public BaseClient() {
        this(System.out);
    }

    /**
     * Создает клиента чата
     * @param printStream поток, в который будут записываться сообщения клиента
     */
    public BaseClient(PrintStream printStream) {
        this.printStream = printStream;
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(CONNECT_TIMEOUT);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.getFilterChain().addLast("executor", new ExecutorFilter(Executors.newSingleThreadExecutor()));
        connector.setHandler(new ClientMessageHandler(new PrintStreamResponseHandler(printStream)));
    }

    @Override
    public void connect(String host, int port) {

        // создаем сессию
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        session = future.getSession();
        session.getConfig().setUseReadOperation(true);
    }

    @Override
    public boolean isConnected() {
        return session != null && session.isConnected();
    }

    @Override
    public void stop() {
        if (session != null) {
            session.getConfig().setUseReadOperation(false);
            session.close(false).awaitUninterruptibly();
        }
        if (printStream != null && printStream != System.out) {
            printStream.flush();
            printStream.close();
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

    @Override
    public void login(String name) {
        sendCustomCmd(Command.LOGIN, name);
    }

    @Override
    public String getUserName() {
        return session != null ? (String) session.getAttribute("user") : null;
    }

    @Override
    public void quit() {
        sendCustomCmd(Command.QUIT, null);
    }

    @Override
    public IoSession getSession() {
        return session;
    }
    @Override
    public boolean isLoggedIn() {
        return session != null && session.getAttribute("user") != null;
    }

    @Override
    public void help() {
        sendCustomCmd(Command.HELP, null);
    }

    @Override
    public void list() {
        sendCustomCmd(Command.LIST, null);
    }

    @Override
    public void send(String msg) {
        sendCustomCmd(Command.SEND, msg);
    }
}
