package ru.repp.chat.server;

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
     * Получить последние 100 сообщений
     * @return 100 сообщений
     */
    public List<String> getLast100();

    /**
     * Очистить историю
     */
    public void clear();


}
