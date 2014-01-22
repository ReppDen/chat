package ru.repp.chat.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author @Drepp
 * @since 22.01.14
 */
public class UtilsTest {

    @Test
    public void testMakeCustomServerCmd() throws Exception {
        Command cmd = Command.HELP;
        Response resp = Response.ERROR;
        String text = "text";
        String s = Utils.makeCustomServerCmd(cmd, resp, text);
        String[] parts = s.split("\\s");
        Assert.assertEquals(parts.length, 3);
        Assert.assertEquals(parts[0], cmd.toString());
        Assert.assertEquals(parts[1], resp.toString());
        Assert.assertEquals(parts[2], text);

    }

    @Test
    public void testMakeCustomClientCmd() throws Exception {
        Command cmd = Command.HELP;
        String text = "text";
        String s = Utils.makeCustomClientCmd(cmd, text);
        String[] parts = s.split("\\s");
        Assert.assertEquals(parts.length, 2);
        Assert.assertEquals(parts[0], cmd.toString());
        Assert.assertEquals(parts[1], text);
    }

    @Test
    public void testMatchesClinetCommonCommandPattern() throws Exception {
        String match = Utils.makeCustomClientCmd(Command.LIST, "text");
        String notMatch = "something wrond";
        Assert.assertTrue(Utils.matchesClinetCommonCommandPattern(match));
        Assert.assertFalse(Utils.matchesClinetCommonCommandPattern(notMatch));
    }
}
