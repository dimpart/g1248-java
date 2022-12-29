package chat.dim.g1248.handler;

import java.util.ArrayList;
import java.util.List;

import chat.dim.g1248.SharedDatabase;
import chat.dim.g1248.model.History;
import chat.dim.g1248.protocol.GameHistoryContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;
import chat.dim.utils.Log;

public class HistoryHandler extends GameHistoryContentHandler {

    private final SharedDatabase database;

    public HistoryHandler(SharedDatabase db) {
        super();
        database = db;
    }

    @Override
    protected List<Content> handleFetchRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        Log.info("[GAME] received fetch request: " + sender + ", " + content);
        // 1. get tid, bid
        int gid;
        Object integer;
        integer = content.get("gid");
        if (integer == null) {
            return respondText("Request error", null);
        } else {
            gid = ((Number) integer).intValue();
        }
        // 2. get game history with gid
        History history = database.getHistory(gid);
        if (history == null) {
            return respondText("History not found", null);
        }
        // 3. respond
        GameHistoryContent res = GameHistoryContent.fetchResponse(gid, history);
        List<Content> responses = new ArrayList<>();
        responses.add(res);
        return responses;
    }

    @Override
    protected List<Content> handleFetchResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // S -> C: "fetched"
        throw new AssertionError("should not happen: " + content);
    }
}
