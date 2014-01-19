package ru.repp.chat.client;

import org.apache.mina.core.session.IoSession;

import java.io.IOException;

/**
 * Здесь будет ваша реклама
 *
 * @author den
 * @since 1/19/14
 */
public interface Client {
    void connect(String host, int port) throws Exception;

    boolean isConnected() throws Exception;

    /**
     * Останавливает клиента
     */
    void stop() throws Exception;

    /**
     * отправка комманды авторизации
     * @param name имя пользователя
     */
    void login(String name) throws Exception;

    /**
     * @return Возворащает имя авторизованног опользователя, null если не авторизован
     */
    String getUserName() throws Exception;

    /**
     * завершение работы клиента
     */
    void quit() throws Exception;

    IoSession getSession() throws Exception;

    boolean isLoggedIn() throws Exception;

    void help() throws Exception;

    void list() throws Exception;

    void send(String msg) throws Exception;

    /**
     * запустить интерактивную процедуру логина
     * @throws IOException
     */
    void doLogin() throws IOException;
}
