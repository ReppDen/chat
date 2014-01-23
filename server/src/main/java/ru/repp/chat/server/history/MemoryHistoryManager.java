package ru.repp.chat.server.history;

import java.util.LinkedList;
import java.util.List;

/**
 * Менеджер истории, хранит все в памяти
 * @author @Drepp
 * @since 14.01.14
 */
public class MemoryHistoryManager implements HistoryManager{

    private List<String> store;

    public MemoryHistoryManager() {
        store = new LinkedList<String>();
    }

    public void add(String msg) {
        store.add(msg);
    }

    public List<String> getLast(int n) {
        if (n <= 0) {
            throw  new IllegalArgumentException("Count must be bigger than zero");
        }
        int start = store.size() - n >= 0 ? store.size() - n : 0;
        store = store.subList(start,store.size());
        return store;
    }

    public void clear() {
        store.clear();

    }

    public int getCount() {
        return store.size();
    }
}
