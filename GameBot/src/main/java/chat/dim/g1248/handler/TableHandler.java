package chat.dim.g1248.handler;

import java.util.ArrayList;
import java.util.List;

import chat.dim.g1248.SharedDatabase;
import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.History;
import chat.dim.g1248.protocol.GameTableContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;
import chat.dim.utils.Log;

public class TableHandler extends GameTableContentHandler {

    private final SharedDatabase database;

    public TableHandler(SharedDatabase db) {
        super();
        database = db;
    }

    @Override
    protected List<Content> handleWatchRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        Log.info("[GAME] received watch request: " + sender + ", " + content);
        // 1. get tid, bid
        int tid;
        Object integer;
        integer = content.get("tid");
        if (integer == null) {
            return respondText("Request error", null);
        } else {
            tid = ((Number) integer).intValue();
        }
        // 2. get boards in this table
        List<Board> boards = database.getBoards(tid);
        if (boards == null) {
            boards = new ArrayList<>();
        }
        // 3. respond
        Content res = GameTableContent.watchResponse(tid, boards);
        List<Content> responses = new ArrayList<>();
        responses.add(res);
        return responses;
    }

    @Override
    protected List<Content> handleWatchResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // S -> C: "boards"
        throw new AssertionError("should not happen: " + content);
    }

    @Override
    protected List<Content> handlePlayRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        Log.info("[GAME] received play request: " + sender + ", " + content);
        // 1. get history
        History history = History.parseHistory(content.get("history"));
        if (history == null) {
            return respondText("Game history not found", null);
        }
        // 2. check player
        ID player = history.getPlayer();
        if (player == null) {
            player = sender;
            history.setPlayer(sender);
        } else {
            assert player.equals(sender) : "player not match: " + player + ", " + sender;
        }
        int tid = history.getTid();
        int bid = history.getBid();
        int gid = history.getGid();

        ID otherPlayer = null;

        // 3. check board
        Board board = database.getBoard(tid, bid);
        if (board != null) {
            ID boardPlayer = board.getPlayer();
            if (boardPlayer != null && !boardPlayer.equals(player)) {
                // this board is occupied by another player
                otherPlayer = boardPlayer;
            }
        }

        // 4. build responses
        List<Content> responses = new ArrayList<>();
        if (otherPlayer == null) {
            // no other player, update the board & history
            board = Board.from(history);
            boolean ok1 = database.updateBoard(tid, board);
            boolean ok2 = database.saveHistory(history);
            // the gid will be updated when origin value is '0'
            gid = history.getGid();
            // respond
            Content res = GameTableContent.playResponse(tid, bid, gid, player);
            res.put("OK", ok1 && ok2);
            responses.add(res);
        } else {
            Content res = GameTableContent.playResponse(tid, bid, gid, otherPlayer);
            res.put("OK", false);
            responses.add(res);
            // attach current board
            List<Board> boards = database.getBoards(tid);
            res = GameTableContent.watchResponse(tid, boards);
            responses.add(res);
        }
        return responses;
    }

    @Override
    protected List<Content> handlePlayResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // S -> C: "played"
        throw new AssertionError("should not happen: " + content);
    }
}
