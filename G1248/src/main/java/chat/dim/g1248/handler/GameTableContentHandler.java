package chat.dim.g1248.handler;

import java.util.List;

import chat.dim.g1248.protocol.GameTableContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;

/**
 *  Handler for Game Table Content
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  Handler for customized content
 */
public abstract class GameTableContentHandler extends GameCustomizedContentHandler {

    @Override
    public List<Content> handleAction(String act, ID sender, CustomizedContent content, ReliableMessage rMsg) {
        if (act == null) {
            throw new IllegalArgumentException("action name empty: " + content);
        } else if (act.equals(GameTableContent.ACT_WATCH_REQ)) {
            // action "watching"
            return handleWatchRequest(sender, content, rMsg);
        } else if (act.equals(GameTableContent.ACT_WATCH_RES)) {
            // action "boards"
            return handleWatchResponse(sender, content, rMsg);
        } else if (act.equals(GameTableContent.ACT_PLAY_REQ)) {
            // action "playing"
            return handlePlayRequest(sender, content, rMsg);
        } else if (act.equals(GameTableContent.ACT_PLAY_RES)) {
            // action "played"
            return handlePlayResponse(sender, content, rMsg);
        }
        // TODO: define your actions here
        // ...

        return super.handleAction(act, sender, content, rMsg);
    }

    //
    //  Actions
    //

    protected abstract List<Content> handleWatchRequest(ID sender, CustomizedContent content, ReliableMessage rMsg);

    protected abstract List<Content> handleWatchResponse(ID sender, CustomizedContent content, ReliableMessage rMsg);

    protected abstract List<Content> handlePlayRequest(ID sender, CustomizedContent content, ReliableMessage rMsg);

    protected abstract List<Content> handlePlayResponse(ID sender, CustomizedContent content, ReliableMessage rMsg);
}
