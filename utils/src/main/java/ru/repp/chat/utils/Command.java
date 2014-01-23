package ru.repp.chat.utils;

/**
 * Набор комманд чата
 *
 * @author den
 * @since 1/16/14
 */
public enum Command {

    SEND("/SEND"),
    LOGIN("/LOGIN"),
    QUIT("/QUIT"),
    HELP("/HELP"),
    LIST("/LIST");

    private final String cmdText;

    @Override
    public String toString() {
        return cmdText;
    }

    Command(String s) {
        this.cmdText = s;
    }

    public static Command formString(String cmd) {
        if (cmd != null) {
            for (Command c : Command.values()) {
                if (cmd.equalsIgnoreCase(c.cmdText)) {
                    return c;
                }
            }
        }
        return null;
    }
}
