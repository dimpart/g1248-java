package chat.dim.g1248.handler;

import java.util.List;

import chat.dim.Facebook;
import chat.dim.Messenger;
import chat.dim.g1248.protocol.HallContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;

/**
 *  Game Hall
 *  ~~~~~~~~~
 *
 *  Handler for customized content
 */
public class HallContentHandler extends GameContentHandler {

    public HallContentHandler(Facebook facebook, Messenger messenger) {
        super(facebook, messenger);
    }

    @Override
    public List<Content> handleAction(String act, ID sender, CustomizedContent content, ReliableMessage rMsg) {
        if (act == null) {
            throw new IllegalArgumentException("action name empty: " + content);
        } else if (act.equals(HallContent.ACT_SEEK_REQ)) {
            // action "seeking"
            return handleSeeking(sender, content, rMsg);
        } else if (act.equals(HallContent.ACT_SEEK_RES)) {
            // action "sought"
            return handleSought(sender, content, rMsg);
        }
        // TODO: define your actions here
        // ...

        return super.handleAction(act, sender, content, rMsg);
    }

    //
    //  Actions
    //

    private List<Content> handleSeeking(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // TODO: handle customized action with message content
        return null;
    }

    private List<Content> handleSought(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // TODO: handle customized action with message content
        return null;
    }
}
