package chat.dim.g1248.protocol;

import java.util.List;
import java.util.Map;

import chat.dim.g1248.model.Board;
import chat.dim.g1248.model.History;
import chat.dim.protocol.ID;

/**
 *  Game Table Content
 *  ~~~~~~~~~~~~~~~~~~
 *
 *  JSON: {
 *      type : 0xCC,
 *      sn   : 123,
 *
 *      app   : "chat.dim.g1248",
 *      mod   : "{MODULE}",        // "table"
 *      act   : "{ACTION}",        // "watching", "boards" or "playing", "played"
 *      extra : info               // action parameters
 *  }
 */
public class GameTableContent extends GameCustomizedContent {

    public static final String MOD_NAME = "table";

    // querying competitions on the table
    public static final String ACT_WATCH_REQ = "watching";
    public static final String ACT_WATCH_RES = "boards";

    // post next move on the board
    public static final String ACT_PLAY_REQ  = "playing";
    public static final String ACT_PLAY_RES  = "played";

    public GameTableContent(Map<String, Object> content) {
        super(content);
    }

    public GameTableContent(String act) {
        super(MOD_NAME, act);
    }

    public void setTid(int tid) {
        put("tid", tid);
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
        put("boards", Board.revert(boards));
    }

    public void setHistory(History history) {
        put("history", history.toMap());
    }

    //
    //  Factory methods
    //

    public static GameTableContent watch(int tid, int bid) {
        GameTableContent content = new GameTableContent(ACT_WATCH_REQ);
        content.setTid(tid);
        content.setBid(bid);
        return content;
    }

    public static GameTableContent watchResponse(int tid, List<Board> boards) {
        GameTableContent content = new GameTableContent(ACT_WATCH_RES);
        content.setTid(tid);
        content.setBoards(boards);
        return content;
    }

    public static GameTableContent play(History history) {
        GameTableContent content = new GameTableContent(ACT_PLAY_REQ);
        content.setHistory(history);
        return content;
    }

    public static GameTableContent playResponse(int tid, int bid, int gid, ID player) {
        GameTableContent content = new GameTableContent(ACT_PLAY_RES);
        content.setTid(tid);
        content.setBid(bid);
        content.setGid(gid);
        content.setPlayer(player);
        return content;
    }
}
