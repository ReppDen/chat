package ru.repp.chat.server;

import ru.repp.chat.server.history.HistoryManager;

/**
 * Интерфейс сервера
 *
 * @author den
 * @since 1/18/14
 */
public interface Server {

    /**
     * старт сервера
     */
    int start();

    /**
     * остановка сервера
     */
    void stop();

    /**
     * @return индикатор активности севрера
     */
    boolean isServerActive();

    /**
     * @return количество сессий
     */
    int getSessionsCount();

    /**
     * @return количество авторизованных клиентов
     */
    int getAuthorizedClientsCount();

    /**
     * @return менеджер истории
     */
    HistoryManager getHistoryManager();
}
