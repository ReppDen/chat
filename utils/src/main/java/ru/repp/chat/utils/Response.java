package ru.repp.chat.utils;

/**
 * Множество ответов сервера
 *
 * @author den
 * @since 1/16/14
 */
public enum Response {

    OK("OK"),
    ERROR("ERROR");

    private final String responseText;

    @Override
    public String toString() {
        return responseText;
    }

    Response(String text) {
        responseText = text;
    }

}
