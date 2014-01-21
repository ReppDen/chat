package ru.repp.chat.server;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import ru.repp.chat.server.history.HistoryManager;

import java.util.Map;

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
    int start();

    IoHandlerAdapter getMessageHandler();

    void setMessageHandler(IoHandlerAdapter messageHandler);

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

    void setHistoryManager(HistoryManager manager);

    /**
     * @return менеджер истории
     */
    HistoryManager getHistoryManager();


    Map<Long, IoSession> getSessions();
}
