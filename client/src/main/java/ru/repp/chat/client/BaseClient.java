package ru.repp.chat.client;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.RecoverableProtocolDecoderException;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import ru.repp.chat.utils.Command;
import ru.repp.chat.utils.Constants;
import ru.repp.chat.utils.Response;
import ru.repp.chat.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

/**
 * базовая имплементация клиента
 *
 * @author @Drepp
 * @since 21.01.14
 */
public class BaseClient extends IoHandlerAdapter implements Client {

    private final BufferedReader inReader;
    private final NioSocketConnector connector;
    private IoSession session;
    private final PrintStream printStream;

    /**
     * Создает клиента чата <br/>
     * по умолчанию выводит все сообщения в System.out
     */
    public BaseClient() {
        this(System.out, new BufferedReader(new InputStreamReader(System.in)));
    }

    /**
     * Создает клиента чата
     *
     * @param printStream поток, в который будут записываться сообщения клиента
     * @param inReader    поток, откуда считываются водные данные
     */
    public BaseClient(PrintStream printStream, BufferedReader inReader) {
        this.printStream = printStream;
        this.inReader = inReader;
        connector = new NioSocketConnector();
        connector.setConnectTimeoutMillis(Constants.CONNECT_TIMEOUT);
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.getFilterChain().addLast("executor", new ExecutorFilter(Executors.newSingleThreadExecutor()));
        connector.setHandler(this);
        printStream.println("Welcome co ChatApp! You need to log in for continue.");
    }


    public int connect(String host, int port) {

        // создаем сессию
        try {
            session = getConnectFuture(host, port).getSession();
            getSession().getConfig().setUseReadOperation(true);
            return 0;
        } catch (RuntimeIoException ex) {
            printStream.println("Can not connect to server " + host + ":" + port);
            stop();
            return 1;
        }
    }

    ConnectFuture getConnectFuture(String host, int port) {
        ConnectFuture future = connector.connect(new InetSocketAddress(host, port));
        future.awaitUninterruptibly();
        return future;
    }


    public boolean isConnected() {
        return getSession() != null && getSession().isConnected();
    }


    public void stop() {
        if (isConnected()) {
            if (printStream != System.out) {
                printStream.flush();
                printStream.close();
            }
            printStream.println("You left the chat.");
            getSession().close(false).awaitUninterruptibly();
        }

    }

    /**
     * посылает указанную комманду серверу
     *
     * @param cmd комманда
     * @param arg аргумент
     */
    String sendCustomCmd(Command cmd, Object arg) throws Exception {
        return sendRawText(Utils.makeCustomClientCmd(cmd, arg));
    }


    public String login(String name) throws Exception {
        return sendCustomCmd(Command.LOGIN, name);
    }


    public String getUserName() {
        return getSession() != null ? (String) getSession().getAttribute("user") : null;
    }


    public String quit() throws Exception {
        return sendCustomCmd(Command.QUIT, null);
    }


    IoSession getSession() {
        return session;
    }

    public boolean isLoggedIn() {
        return getSession() != null && getSession().getAttribute("user") != null;
    }


    public String help() throws Exception {
        return sendCustomCmd(Command.HELP, null);
    }


    public String list() throws Exception {
        return sendCustomCmd(Command.LIST, null);
    }


    public String send(String msg) throws Exception {
        return sendCustomCmd(Command.SEND, msg);
    }

    public String sendRawText(String msg) {
        getSession().write(msg.trim()).awaitUninterruptibly();
        return (String) getSession().read().awaitUninterruptibly().getMessage();
    }

    public void doLogin() throws Exception {
        String msg;
        do {
            printStream.println("Please your nickname (one word, english characters, numbers and \"_\" allowed). Or type /quit to exit application");
            msg = inReader.readLine();
        } while (!msg.matches("[A-Za-z0-9_]+") && !msg.toUpperCase().matches("/QUIT"));
        if (msg.toUpperCase().matches("/QUIT")) {
            this.stop();
        } else {
            this.login(msg);
        }

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        String completeMsg = (String) message;
        String[] parts = completeMsg.split(" ", 3);
        String command = parts[0];
        String status = parts[1];
        String value = parts.length > 2 ? parts[2] : "";

        Command cmd = Command.formString(command);

        if (Response.OK.toString().equals(status)) {
            // комманда успешно обработана
            switch (cmd) {
                case LOGIN: {
                    printStream.println("Login successfull!");
                    printStream.println("[Hint] Type /help to get command list");
                    session.setAttribute("user", value);
                    break;
                }
                case QUIT: {
                    this.stop();
                    break;
                }
                case LIST:
                case HELP: {
                    printStream.println(value);
                    break;
                }
                default: {
                    printStream.println(value);
                    break;
                }
            }
        } else {
            printStream.println("Server send an error! " + value);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        if (cause instanceof IOException) {
            printStream.println("Error! Server connection interrupted");
        } else if (cause instanceof RecoverableProtocolDecoderException) {
            printStream.println("Error! Server send incorrect command");
        }
        session.close(true);
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        printStream.println("Connection lost. Server is not available");
    }
}
