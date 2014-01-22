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
     * формирует ответ сервера
     * @param cmd комманда
     * @param response ответа
     * @param arg аргумент
     */
    public static String makeCustomServerCmd(Command cmd, Response response, Object arg) {
        return cmd.toString() + " " + response.toString() + " " + arg.toString();
    }

    /**
     * формирует клиентскую комманду
     * @param cmd комманда
     * @param arg аргумент
     */
    public static String makeCustomClientCmd(Command cmd, Object arg) {
        return cmd.toString() + " " + arg;
    }

    public static boolean matcheslinetCommonCommandPattern(String msg) {
        return msg.matches("^/\\w+(\\s\\w+)*$");
    }
}
