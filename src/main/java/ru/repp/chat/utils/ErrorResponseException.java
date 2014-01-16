package ru.repp.chat.utils;

/**
 * Исключение, обозначающее ответ сервера "ошибка"
 *
 * @author den
 * @since 1/16/14
 */
public class ErrorResponseException  extends Exception {

    public ErrorResponseException(String message) {
        super(message);
    }
}
