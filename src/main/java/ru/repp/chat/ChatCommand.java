package ru.repp.chat;

/**
 * @author @Drepp
 * @since 13.01.14
 */
public class ChatCommand {
    public static final int LOGIN = 0;

    public static final int QUIT = 1;

    public static final int SEND = 2;

    private final int num;

    private ChatCommand(int num) {
        this.num = num;
    }

    public int toInt() {
        return num;
    }

    public static ChatCommand valueOf(String s) {
        s = s.toUpperCase();
        if ("/LOGIN".equals(s)) {
            return new ChatCommand(LOGIN);
        } else
        if ("/QUIT".equals(s)) {
            return new ChatCommand(QUIT);
        } else {
            return new ChatCommand(SEND);
        }
    }
}
