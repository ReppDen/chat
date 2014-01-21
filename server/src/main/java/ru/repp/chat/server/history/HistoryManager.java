package ru.repp.chat.server.history;

import java.util.List;

/**
 * Менеджер истории
 *
 * @author @Drepp
 * @since 14.01.14
 */
public interface HistoryManager {

    /**
     * Добавить сообщение в историю
     * @param msg сообщение
     */
    public void add(String msg);

    /**
     * Получить последние N сообщений
     * @return количество сообещний
     */
    public List<String> getLast(int n);

    /**
     * Очистить историю
     */
    public void clear();

    /**
     * @return количество сообщений
     */
    public int getCount();


}
