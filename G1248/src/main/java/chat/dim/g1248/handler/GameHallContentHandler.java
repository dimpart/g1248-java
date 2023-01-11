package chat.dim.g1248.handler;

import java.util.List;

import chat.dim.g1248.protocol.GameHallContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;

/**
 *  Handler for Game Hall Content
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  Handler for customized content
 */
public abstract class GameHallContentHandler extends GameCustomizedContentHandler {

    @Override
    public List<Content> handleAction(String act, ID sender, CustomizedContent content, ReliableMessage rMsg) {
        if (act == null) {
            throw new IllegalArgumentException("action name empty: " + content);
        } else if (act.equals(GameHallContent.ACT_SEEK_REQ)) {
            // action "seeking"
            return handleSeekRequest(sender, content, rMsg);
        } else if (act.equals(GameHallContent.ACT_SEEK_RES)) {
            // action "rooms"
            return handleSeekResponse(sender, content, rMsg);
        }
        // TODO: define your actions here
        // ...

        return super.handleAction(act, sender, content, rMsg);
    }

    //
    //  Actions
    //

    protected abstract List<Content> handleSeekRequest(ID sender, CustomizedContent content, ReliableMessage rMsg);

    protected abstract List<Content> handleSeekResponse(ID sender, CustomizedContent content, ReliableMessage rMsg);
}
