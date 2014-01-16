package ru.repp.chat.utils;

/**
 * Набор комманд чата
 *
 * @author den
 * @since 1/16/14
 */
public enum Command {

    SEND("/SEND", 0),
    LOGIN("/LOGIN", 1),
    QUIT("/QUIT", 2),
    HELP("/HELP", 3),
    LIST("/LIST", 4);

    private String cmdText;
    private int code;

    @Override
    public String toString() {
        return cmdText;
    }

    public final int getCode() {
        return code;
    }

    Command(String s, int code) {
        this.cmdText = s;
        this.code = code;
    }

    public static Command formString(String cmd) {
        if (cmd != null) {
            for (Command c : Command.values()) {
                if (cmd.equalsIgnoreCase(c.cmdText)) {
                    return c;
                }
            }
        }
    }
}
