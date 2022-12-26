package chat.dim.g1248.handler;

import java.util.ArrayList;
import java.util.List;

import chat.dim.g1248.SharedDatabase;
import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.History;
import chat.dim.g1248.protocol.GameCustomizedContent;
import chat.dim.g1248.protocol.GameTableContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;

public class TableHandler extends GameTableContentHandler {

    private final SharedDatabase database;

    public TableHandler(SharedDatabase db) {
        super();
        database = db;
    }

    @Override
    protected List<Content> handleWatchRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // 1. get tid, bid
        int tid;
        int bid = -1;
        Object integer;
        integer = content.get("tid");
        if (integer == null) {
            return respondText("Request error", null);
        } else {
            tid = ((Number) integer).intValue();
        }
        integer = content.get("bid");
        if (integer != null) {
            bid = ((Number) integer).intValue();
        }
        // 2. get boards in this table
        List<Board> boards = database.getBoards(tid);
        if (boards == null) {
            boards = new ArrayList<>();
        }
        History history = null;
        if (bid >= 0) {
            // 2.2. get history for current game in this board
            for (Board item : boards) {
                if (item.getBid() == bid) {
                    int gid = item.getGid();
                    history = database.getHistory(gid);
                }
            }
        }
        // 3. respond
        Content res = GameCustomizedContent.createResponse(content,
                GameTableContent.MOD_NAME, GameTableContent.ACT_WATCH_RES);
        res.put("boards", Board.revert(boards));
        if (history != null) {
            res.put("history", history.toMap());
        }
        List<Content> responses = new ArrayList<>();
        responses.add(Content.parse(res));
        return responses;
    }

    @Override
    protected List<Content> handleWatchResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // S -> C: "boards"
        throw new AssertionError("should not happen: " + content);
    }

    @Override
    protected List<Content> handlePlayRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // 1. get history
        History history = History.parseHistory(content.get("history"));
        if (history == null) {
            return respondText("Game history not found", null);
        }
        // 2. save history
        boolean ok = database.saveHistory(history);
        ID player = history.getPlayer();
        // 3. respond
        Content res = GameCustomizedContent.createResponse(content,
                GameTableContent.MOD_NAME, GameTableContent.ACT_PLAY_RES);
        res.remove("history");
        res.put("tid", history.getTid());
        res.put("bid", history.getBid());
        res.put("gid", history.getGid());
        if (player != null) {
            res.put("player", player.toString());
        }
        res.put("OK", ok);
        List<Content> responses = new ArrayList<>();
        responses.add(Content.parse(res));
        return responses;
    }

    @Override
    protected List<Content> handlePlayResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // S -> C: "played"
        throw new AssertionError("should not happen: " + content);
    }
}
