package chat.dim.g1248.handler;

import java.util.ArrayList;
import java.util.List;

import chat.dim.Roster;
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
        int tid = 0;
        Object integer;
        integer = content.get("tid");
        if (integer != null) {
            tid = ((Number) integer).intValue();
        }
        if (tid <= 0) {
            return respondText("Watch request error", null);
        } else {
            // mark online
            Roster roster = Roster.getInstance();
            roster.addPlayer(tid, sender, 0);
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

    // send new board as a watch response to all online users in this table
    private void broadcast(int tid, ID player, Board board) {
        List<Board> array = new ArrayList<>();
        array.add(board);
        Content res = GameTableContent.watchResponse(tid, array);
        Roster roster = Roster.getInstance();
        roster.broadcast(tid, player, res);
    }

    private List<Content> postHistory(ID sender, History history) {
        int tid = history.getTid();
        int bid = history.getBid();
        ID player = history.getPlayer();
        if (player == null) {
            player = sender;
            history.setPlayer(sender);
        } else {
            assert player.equals(sender) : "player not match: " + player + ", " + sender;
        }
        if (tid <= 0 || bid < 0) {
            return respondText("Play request error", null);
        } else {
            // mark online
            Roster roster = Roster.getInstance();
            roster.addPlayer(tid, player, 0);
        }

        // check the board
        ID otherPlayer = null;
        Board board = database.getBoard(tid, bid);
        if (board != null) {
            ID boardPlayer = board.getPlayer();
            if (boardPlayer != null && !boardPlayer.equals(player)) {
                // this board is occupied by another player
                otherPlayer = boardPlayer;
            }
        }

        // build responses
        List<Content> responses = new ArrayList<>();
        if (otherPlayer == null) {
            // no other player, update the board & history
            board = Board.from(history);
            boolean ok1 = database.updateBoard(tid, board);
            boolean ok2 = database.saveHistory(history);
            if (ok1/* && ok2*/) {
                broadcast(tid, player, board);
            }
            // the gid will be updated when origin value is '0'
            int gid = history.getGid();
            // respond
            Content res = GameTableContent.playResponse(tid, bid, gid, player);
            res.put("OK", ok1 && ok2);
            responses.add(res);
        } else {
            int gid = board.getGid();
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
    private List<Content> keepOnline(ID sender, CustomizedContent content) {
        int tid = 0, bid = -1;
        Object intValue;
        intValue = content.get("tid");
        if (intValue != null) {
            tid = ((Number) intValue).intValue();
        }
        intValue = content.get("bid");
        if (intValue != null) {
            bid = ((Number) intValue).intValue();
        }
        ID player = ID.parse(content.get("player"));
        if (player == null) {
            player = sender;
        } else {
            assert player.equals(sender) : "player not match: " + player + ", " + sender;
        }
        if (tid <= 0 || bid < 0) {
            return respondText("Play request error", null);
        } else {
            // mark online
            Roster roster = Roster.getInstance();
            roster.addPlayer(tid, player, 0);
        }

        // check the board
        ID otherPlayer = null;
        Board board = database.getBoard(tid, bid);
        if (board != null) {
            ID boardPlayer = board.getPlayer();
            if (boardPlayer != null && !boardPlayer.equals(player)) {
                // this board is occupied by another player
                otherPlayer = boardPlayer;
            }
        }
        if (otherPlayer == null) {
            // no other player on this board, return nothing
            return null;
        }
        int gid = board.getGid();

        // build responses
        List<Content> responses = new ArrayList<>();
        Content res = GameTableContent.playResponse(tid, bid, gid, otherPlayer);
        res.put("OK", false);
        responses.add(res);
        // attach current board
        List<Board> boards = database.getBoards(tid);
        res = GameTableContent.watchResponse(tid, boards);
        responses.add(res);
        return responses;
    }

    @Override
    protected List<Content> handlePlayRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        Log.info("[GAME] received play request: " + sender + ", " + content);
        // 1. get history
        History history = History.parseHistory(content.get("history"));
        if (history == null) {
            // just keep the seat
            return keepOnline(sender, content);
        } else {
            // post history
            return postHistory(sender, history);
        }
    }

    @Override
    protected List<Content> handlePlayResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // S -> C: "played"
        throw new AssertionError("should not happen: " + content);
    }
}
