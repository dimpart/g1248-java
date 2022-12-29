package chat.dim.g1248.cpu;

import java.util.List;

import chat.dim.Facebook;
import chat.dim.Messenger;
import chat.dim.cpu.CustomizedContentHandler;
import chat.dim.cpu.CustomizedContentProcessor;
import chat.dim.g1248.GlobalVariable;
import chat.dim.g1248.protocol.GameCustomizedContent;
import chat.dim.g1248.protocol.GameHallContent;
import chat.dim.g1248.protocol.GameHistoryContent;
import chat.dim.g1248.protocol.GameTableContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ReliableMessage;

/**
 *  Application Customized Content Processor
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  Process customized contents for this application only
 */
public class GameCustomizedContentProcessor extends CustomizedContentProcessor {

    public GameCustomizedContentProcessor(Facebook facebook, Messenger messenger) {
        super(facebook, messenger);
    }

    @Override
    protected List<Content> filter(String app, CustomizedContent content, ReliableMessage rMsg) {
        if (app != null && app.equals(GameCustomizedContent.APP_ID)) {
            // App ID match
            // return null to fetch module handler
            return null;
        }
        return super.filter(app, content, rMsg);
    }

    @Override
    protected CustomizedContentHandler fetch(String mod, CustomizedContent content, ReliableMessage rMsg) {
        GlobalVariable shared = GlobalVariable.getInstance();
        if (mod == null) {
            throw new IllegalArgumentException("module name empty: " + content);
        } else if (mod.equals(GameHallContent.MOD_NAME)) {
            // customized module: "hall"
            return shared.gameHallContentHandler;
        } else if (mod.equals(GameTableContent.MOD_NAME)) {
            // customized module: "table"
            return shared.gameTableContentHandler;
        } else if (mod.equals(GameHistoryContent.MOD_NAME)) {
            // customized module: "history"
            return shared.gameHistoryContentHandler;
        }
        // TODO: define your modules here
        // ...

        return super.fetch(mod, content, rMsg);
    }
}
