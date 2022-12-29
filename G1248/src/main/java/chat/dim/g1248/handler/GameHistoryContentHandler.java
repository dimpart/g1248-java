package chat.dim.g1248.handler;

import java.util.List;

import chat.dim.g1248.protocol.GameHistoryContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;

/**
 *  Handler for Game History Content
 *  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *  Handler for customized content
 */
public abstract class GameHistoryContentHandler extends GameCustomizedContentHandler {

    @Override
    public List<Content> handleAction(String act, ID sender, CustomizedContent content, ReliableMessage rMsg) {
        if (act == null) {
            throw new IllegalArgumentException("action name empty: " + content);
        } else if (act.equals(GameHistoryContent.ACT_FETCH_REQ)) {
            // action "fetching"
            return handleFetchRequest(sender, content, rMsg);
        } else if (act.equals(GameHistoryContent.ACT_FETCH_RES)) {
            // action "fetched"
            return handleFetchResponse(sender, content, rMsg);
        }
        // TODO: define your actions here
        // ...

        return super.handleAction(act, sender, content, rMsg);
    }

    //
    //  Actions
    //

    protected abstract List<Content> handleFetchRequest(ID sender, CustomizedContent content, ReliableMessage rMsg);

    protected abstract List<Content> handleFetchResponse(ID sender, CustomizedContent content, ReliableMessage rMsg);
}
