package chat.dim.g1248.protocol;

import java.util.List;
import java.util.Map;

import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.History;
import chat.dim.protocol.ID;

/**
 *  Game Room Content
 *  ~~~~~~~~~~~~~~~~~
 *
 *  JSON: {
 *      type : 0xCC,
 *      sn   : 123,
 *
 *      app   : "chat.dim.g1248",
 *      mod   : "{MODULE}",        // "room"
 *      act   : "{ACTION}",        // "watching", "boards" or "playing", "played"
 *
 *      rid     : {ROOM_ID},
 *      bid     : {BOARD_ID},
 *      gid     : {GAME_HISTORY_ID},
 *      player  : {PLAYER_ID},
 *      boards  : [...]
 *      history : {...}
 *  }
 */
public class GameRoomContent extends GameCustomizedContent {

    public static final String MOD_NAME = "room";

    // querying competitions on the room
    public static final String ACT_WATCH_REQ = "watching";
    public static final String ACT_WATCH_RES = "boards";

    // post next move on the board
    public static final String ACT_PLAY_REQ  = "playing";
    public static final String ACT_PLAY_RES  = "played";

    public GameRoomContent(Map<String, Object> content) {
        super(content);
    }

    public GameRoomContent(String act) {
        super(MOD_NAME, act);
    }

    public void setRid(int rid) {
        put("rid", rid);
    }

    public void setBid(int bid) {
        put("bid", bid);
    }

    public void setGid(int gid) {
        put("gid", gid);
    }

    public void setPlayer(ID player) {
        put("player", player.toString());
    }

    public void setBoards(List<Board> boards) {
        put("boards", Board.revertBoards(boards));
    }

    public void setHistory(History history) {
        put("history", history.toMap());
    }

    //
    //  Factory methods
    //

    public static GameRoomContent watch(int rid, int bid) {
        GameRoomContent content = new GameRoomContent(ACT_WATCH_REQ);
        content.setRid(rid);
        content.setBid(bid);
        return content;
    }

    public static GameRoomContent watchResponse(int rid, List<Board> boards) {
        GameRoomContent content = new GameRoomContent(ACT_WATCH_RES);
        content.setRid(rid);
        content.setBoards(boards);
        return content;
    }

    public static GameRoomContent play(History history) {
        GameRoomContent content = new GameRoomContent(ACT_PLAY_REQ);
        content.setHistory(history);
        return content;
    }

    public static GameRoomContent playResponse(int rid, int bid, int gid, ID player) {
        GameRoomContent content = new GameRoomContent(ACT_PLAY_RES);
        content.setRid(rid);
        content.setBid(bid);
        content.setGid(gid);
        content.setPlayer(player);
        return content;
    }
}
