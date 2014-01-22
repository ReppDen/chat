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
    int connect(String host, int port) throws Exception;

    boolean isConnected() throws Exception;

    /**
     * Останавливает клиента
     */
    void stop() throws Exception;

    /**
     * отправка комманды авторизации
     * @param name имя пользователя
     */
    String login(String name) throws Exception;

    /**
     * @return Возворащает имя авторизованног опользователя, null если не авторизован
     */
    String getUserName() throws Exception;

    /**
     * завершение работы клиента
     */
    String quit() throws Exception;

    boolean isLoggedIn() throws Exception;

    String help() throws Exception;

    String list() throws Exception;

    String send(String msg) throws Exception;

    String sendRawText(String msg) throws Exception;

    /**
     * запустить интерактивную процедуру логина
     * @throws IOException
     */
    void doLogin() throws Exception;
}
