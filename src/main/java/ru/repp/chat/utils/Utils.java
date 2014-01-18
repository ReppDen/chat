package ru.repp.chat.utils;

/**
 * Утилиты чата
 *
 * @author den
 * @since 1/12/14
 */
public class Utils {
    /**
     * шаблон клиентоской комманды
     */
    public static String getClinetCommandPattern(Command cmd) {
        return "^\\" + cmd.toString() + "\\s\\w+$";
    }

    /**
     * посылает указанную комманду клиенту
     * @param cmd комманда
     * @param arg аргумент
     */
    public static String makeCustomServerCmd(Command cmd, Response response, Object arg) {
        return cmd.toString() + " " + response.toString() + " " + arg.toString();
    }
}
