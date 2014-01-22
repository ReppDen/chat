package ru.repp.chat.client;

import org.apache.mina.core.service.IoHandler;

import java.io.IOException;

/**
 * Здесь будет ваша реклама
 *
 * @author den
 * @since 1/19/14
 */
public interface Client extends IoHandler {
    int connect(String host, int port);

    boolean isConnected();

    /**
     * Останавливает клиента
     */
    void stop();

    /**
     * отправка комманды авторизации
     * @param name имя пользователя
     */
    String login(String name) throws Exception;

    /**
     * @return Возворащает имя авторизованног опользователя, null если не авторизован
     */
    String getUserName();

    /**
     * завершение работы клиента
     */
    String quit() throws Exception;

    boolean isLoggedIn();

    String help() throws Exception;

    String list() throws Exception;

    String send(String msg) throws Exception;

    String sendRawText(String msg);

    /**
     * запустить интерактивную процедуру логина
     * @throws IOException
     */
    void doLogin() throws Exception;
}
