package ru.repp.chat.server;

/**
 * Интерфейс взаимодействия с сервером
 *
 * @author den
 * @since 1/18/14
 */
public interface Server {

    /**
     * старт сервера
     */
    void start();

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
     * задает менеджера сообщений
     * @param manager
     */
    void setHistoryManager(HistoryManager manager);

    /**
     * @return менеджер истории
     */
    HistoryManager getHistoryManager();

    /**
     * Метод для ручного вещаения на всех клиентов
     * @param msg сообщение
     */
//    void broadcast(String msg);
}
