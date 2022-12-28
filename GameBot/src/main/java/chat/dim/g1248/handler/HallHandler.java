package chat.dim.g1248.handler;

import java.util.ArrayList;
import java.util.List;

import chat.dim.g1248.SharedDatabase;
import chat.dim.g1248.model.Table;
import chat.dim.g1248.protocol.GameHallContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;
import chat.dim.utils.Log;

public class HallHandler extends GameHallContentHandler {

    private final SharedDatabase database;

    public HallHandler(SharedDatabase db) {
        super();
        database = db;
    }

    @Override
    protected List<Content> handleSeekRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        Log.info("[GAME] received seek request: " + sender + ", " + content);
        // 1. get range: [start, end)
        int start = 0;
        int end = 20;
        Object integer;
        integer = content.get("start");
        if (integer != null) {
            start = ((Number) integer).intValue();
        }
        integer = content.get("end");
        if (integer != null) {
            end = ((Number) integer).intValue();
        }
        // 2. get tables with the range
        List<Table> tables = database.getTables(start, end);
        if (tables == null || tables.size() == 0) {
            return respondText("Tables not found", null);
        }
        // 3. respond
        GameHallContent res = GameHallContent.seekResponse(start, end, tables);
        List<Content> responses = new ArrayList<>();
        responses.add(res);
        return responses;
    }

    @Override
    protected List<Content> handleSeekResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // S -> C: "tables"
        throw new AssertionError("should not happen: " + content);
    }
}
