package chat.dim.g1248.handler;

import java.util.ArrayList;
import java.util.List;

import chat.dim.cpu.CustomizedContentHandler;
import chat.dim.cpu.CustomizedContentProcessor;
import chat.dim.dkd.BaseTextContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;

/**
 *  Handler for App Customized Content
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
public class GameCustomizedContentHandler implements CustomizedContentHandler {

    public static String FMT_ACT_NOT_SUPPORT = CustomizedContentProcessor.FMT_ACT_NOT_SUPPORT;
    //public static String FMT_ACT_NOT_SUPPORT = "Customized Content (app: %s, mod: %s, act: %s) not support yet!";

    @Override
    public List<Content> handleAction(String act, ID sender, CustomizedContent content, ReliableMessage rMsg) {
        String app = content.getApplication();
        String mod = content.getModule();
        String text = String.format(FMT_ACT_NOT_SUPPORT, app, mod, act);
        return respondText(text, content.getGroup());
    }

    protected List<Content> respondText(String text, ID group) {
        Content res = new BaseTextContent(text);
        if (group != null) {
            res.setGroup(group);
        }
        List<Content> responses = new ArrayList<>();
        responses.add(res);
        return responses;
    }
}
