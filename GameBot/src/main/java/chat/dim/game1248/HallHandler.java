package chat.dim.game1248;

import java.util.List;

import chat.dim.SharedDatabase;
import chat.dim.g1248.handler.GameHallContentHandler;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;

public class HallHandler extends GameHallContentHandler {

    private final SharedDatabase database;

    public HallHandler(SharedDatabase db) {
        super();
        database = db;
    }

    @Override
    protected List<Content> handleSeekRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        return null;
    }

    @Override
    protected List<Content> handleSeekResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        return null;
    }
}
