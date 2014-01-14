package ru.repp.chat;

/**
 * @author @Drepp
 * @since 13.01.14
 */
public class ChatCommand {
    public static final int SEND = 0;
    public static final String SEND_CMD = "/SEND";

    public static final int LOGIN = 1;
    public static final String LOGIN_CMD = "/LOGIN";

    public static final int QUIT = 2;
    public static final String QUIT_CMD = "/QUIT";

    public static final int HELP = 3;
    public static final String HELP_CMD = "/HELP";

    public static final int LIST = 4;
    public static final String LIST_CMD = "/LIST";

    public static final String OK_STATUS = "OK";

    private final int num;

    private ChatCommand(int num) {
        this.num = num;
    }

    public int toInt() {
        return num;
    }

    public static ChatCommand valueOf(String s) {
        s = s.toUpperCase();
        if (LOGIN_CMD.equals(s)) {
            return new ChatCommand(LOGIN);
        } else
        if (SEND_CMD.equals(s)) {
            return new ChatCommand(SEND);
        }
        if (HELP_CMD.equals(s)) {
            return new ChatCommand(HELP);
        }
        if (LIST_CMD.equals(s)) {
            return new ChatCommand(LIST);
        }
        if (QUIT_CMD.equals(s)) {
            return new ChatCommand(QUIT);
        } else {
            return new ChatCommand(SEND);
        }
    }
}
