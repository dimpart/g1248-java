package chat.dim.g1248.handler;

import java.util.List;

import chat.dim.Facebook;
import chat.dim.Messenger;
import chat.dim.g1248.protocol.TableContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;

/**
 *  Game Table
 *  ~~~~~~~~~~
 *
 *  Handler for customized content
 */
public class TableContentHandler extends GameContentHandler {

    public TableContentHandler(Facebook facebook, Messenger messenger) {
        super(facebook, messenger);
    }

    @Override
    public List<Content> handleAction(String act, ID sender, CustomizedContent content, ReliableMessage rMsg) {
        if (act == null) {
            throw new IllegalArgumentException("action name empty: " + content);
        } else if (act.equals(TableContent.ACT_WATCH_REQ)) {
            // action "watching"
            return handleWatching(sender, content, rMsg);
        } else if (act.equals(TableContent.ACT_WATCH_RES)) {
            // action "watched"
            return handleWatched(sender, content, rMsg);
        } else if (act.equals(TableContent.ACT_PLAY_REQ)) {
            // action "playing"
            return handlePlaying(sender, content, rMsg);
        } else if (act.equals(TableContent.ACT_PLAY_RES)) {
            // action "played"
            return handlePlayed(sender, content, rMsg);
        }
        // TODO: define your actions here
        // ...

        return super.handleAction(act, sender, content, rMsg);
    }

    //
    //  Actions
    //

    private List<Content> handleWatching(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // TODO: handle customized action with message content
        return null;
    }

    private List<Content> handleWatched(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // TODO: handle customized action with message content
        return null;
    }

    private List<Content> handlePlaying(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // TODO: handle customized action with message content
        return null;
    }

    private List<Content> handlePlayed(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // TODO: handle customized action with message content
        return null;
    }
}
