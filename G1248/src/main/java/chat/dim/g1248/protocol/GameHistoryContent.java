package chat.dim.g1248.protocol;

import java.util.Map;

import chat.dim.g1248.model.History;

/**
 *  Game History Content
 *  ~~~~~~~~~~~~~~~~~~~~
 *
 *  JSON: {
 *      type : 0xCC,
 *      sn   : 123,
 *
 *      app   : "chat.dim.g1248",
 *      mod   : "{MODULE}",        // "room"
 *      act   : "{ACTION}",        // "watching", "boards" or "playing", "played"
 *
 *      gid     : {GAME_HISTORY_ID},
 *      history : {...}
 *  }
 */
public class GameHistoryContent extends GameCustomizedContent {

    public static final String MOD_NAME = "history";

    // querying game history
    public static final String ACT_FETCH_REQ = "fetching";
    public static final String ACT_FETCH_RES = "fetched";

    public GameHistoryContent(Map<String, Object> content) {
        super(content);
    }

    public GameHistoryContent(String act) {
        super(MOD_NAME, act);
    }

    public void setGid(int gid) {
        put("gid", gid);
    }

    public void setHistory(History history) {
        put("history", history.toMap());
    }

    //
    //  Factory methods
    //

    public static GameHistoryContent fetch(int gid) {
        GameHistoryContent content = new GameHistoryContent(ACT_FETCH_REQ);
        content.setGid(gid);
        return content;
    }

    public static GameHistoryContent fetchResponse(int gid, History history) {
        GameHistoryContent content = new GameHistoryContent(ACT_FETCH_RES);
        content.setGid(gid);
        content.setHistory(history);
        return content;
    }
}
