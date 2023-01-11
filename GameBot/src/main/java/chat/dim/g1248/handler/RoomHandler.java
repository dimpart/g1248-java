package chat.dim.g1248.handler;

import java.util.ArrayList;
import java.util.List;

import chat.dim.Roster;
import chat.dim.g1248.SharedDatabase;
import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.History;
import chat.dim.g1248.protocol.GameRoomContent;
import chat.dim.protocol.Content;
import chat.dim.protocol.CustomizedContent;
import chat.dim.protocol.ID;
import chat.dim.protocol.ReliableMessage;
import chat.dim.utils.Log;

public class RoomHandler extends GameRoomContentHandler {

    private final SharedDatabase database;

    public RoomHandler(SharedDatabase db) {
        super();
        database = db;
    }

    @Override
    protected List<Content> handleWatchRequest(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        Log.info("[GAME] received watch request: " + sender + ", " + content);
        // 1. get rid, bid
        int rid = 0, bid = -1;
        Object integer;
        integer = content.get("rid");
        if (integer != null) {
            rid = ((Number) integer).intValue();
        }
        integer = content.get("bid");
        if (integer != null) {
            bid = ((Number) integer).intValue();
        }
        if (rid <= 0) {
            return respondText("Watch request error", null);
        } else {
            // mark online
            Roster roster = Roster.getInstance();
            roster.addPlayer(rid, bid, sender, 0);
        }

        // 2. respond
        List<Content> responses = new ArrayList<>();
        attachAllBoards(rid, responses);
        return responses;
    }

    @Override
    protected List<Content> handleWatchResponse(ID sender, CustomizedContent content, ReliableMessage rMsg) {
        // S -> C: "boards"
        throw new AssertionError("should not happen: " + content);
    }

    private void attachAllBoards(int rid, List<Content> responses) {
        List<Board> boards = database.getBoards(rid);
        if (boards == null) {
            //boards = new ArrayList<>();
            return;
        }
        Content res = GameRoomContent.watchResponse(rid, boards);
        responses.add(res);
    }
    private void attachPlayingBoard(int rid, int bid, Board board, List<Content> responses) {
        int gid = board.getGid();
        ID player = board.getPlayer();
        Content res = GameRoomContent.playResponse(rid, bid, gid, player);
        responses.add(res);
    }

    // send new board as a watch response to all online users in this room
    private void broadcast(int rid, ID player, Board board) {
        List<Board> array = new ArrayList<>();
        array.add(board);
        Content res = GameRoomContent.watchResponse(rid, array);
        Roster roster = Roster.getInstance();
        roster.broadcast(rid, player, res);
    }

    private Board getPlayingBoard(int rid, int bid, ID player) {
        Board board = database.getBoard(rid, bid);
        if (board == null) {
            // board not exists
            return null;
        }
        ID boardPlayer = board.getPlayer();
        if (boardPlayer == null || boardPlayer.equals(player)) {
            // it's my board, erase it
            return null;
        }
        // check record
        Roster roster = Roster.getInstance();
        if (roster.checkPlayer(rid, bid, boardPlayer, 0)) {
            // this board is occupied by another player
            return board;
        } else {
            // the other player on this board is expired, erase it
            return null;
        }
    }
    private List<Content> postHistory(ID sender, History history) {
        int rid = history.getRid();
        int bid = history.getBid();
        ID player = history.getPlayer();
        if (player == null) {
            player = sender;
            history.setPlayer(sender);
        } else {
            assert player.equals(sender) : "player not match: " + player + ", " + sender;
        }
        if (rid <= 0 || bid < 0) {
            return respondText("Play request error", null);
        } else {
            // mark online
            Roster roster = Roster.getInstance();
            roster.addPlayer(rid, bid, player, 0);
        }
        List<Content> responses = new ArrayList<>();

        // check playing board
        Board board = getPlayingBoard(rid, bid, player);
        if (board == null) {
            // no other player on this board, update history
            boolean ok1 = database.saveHistory(history);
            if (!ok1) {
                Log.error("failed to save history: " + history);
                return responses;
            }
            // add board
            board = Board.from(history);
            boolean ok2 = database.updateBoard(rid, board);
            if (ok2) {
                broadcast(rid, player, board);
            } else {
                Log.error("failed to update board: " + board);
                return responses;
            }
            // the gid will be updated when origin value is '0'
            int gid = history.getGid();
            // respond
            Content res = GameRoomContent.playResponse(rid, bid, gid, player);
            res.put("OK", true);
            responses.add(res);
        } else {
            // this board is occupied by another player, respond to the sender
            // to kick it out
            attachPlayingBoard(rid, bid, board, responses);
            // attach current boards of this room as a watch response
            // to let the sender refresh this room status
            attachAllBoards(rid, responses);
        }

        return responses;
    }
    private List<Content> keepOnline(ID sender, CustomizedContent content) {
        int rid = 0, bid = -1;
        Object intValue;
        intValue = content.get("rid");
        if (intValue != null) {
            rid = ((Number) intValue).intValue();
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
        if (rid <= 0 || bid < 0) {
            return respondText("Play request error", null);
        } else {
            // mark online
            Roster roster = Roster.getInstance();
            roster.addPlayer(rid, bid, player, 0);
        }

        // check playing board
        Board board = getPlayingBoard(rid, bid, player);
        if (board == null) {
            // no other player on this board, return nothing
            return null;
        }
        List<Content> responses = new ArrayList<>();

        // this board is occupied by another player, respond to the sender
        // to kick it out
        attachPlayingBoard(rid, bid, board, responses);
        // attach current boards of this room as a watch response
        // to let the sender refresh this room status
        attachAllBoards(rid, responses);

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
