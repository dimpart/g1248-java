package chat.dim.g1248.protocol;

import java.util.List;
import java.util.Map;

import chat.dim.g1248.model.Table;

/**
 *  Game Hall Content
 *  ~~~~~~~~~~~~~~~~~
 *
 *  JSON: {
 *      type : 0xCC,
 *      sn   : 123,
 *
 *      app   : "chat.dim.g1248",
 *      mod   : "{MODULE}",        // "hall"
 *      act   : "{ACTION}",        // "seeking", "tables"
 *
 *      start  : 0,
 *      end    : 20,
 *      tables : [...]
 *  }
 */
public class GameHallContent extends GameCustomizedContent {

    public static final String MOD_NAME = "hall";

    // querying game tables in the hall
    public static final String ACT_SEEK_REQ = "seeking";
    public static final String ACT_SEEK_RES = "tables";

    public GameHallContent(Map<String, Object> content) {
        super(content);
    }

    public GameHallContent(String act) {
        super(MOD_NAME, act);
    }

    public void setStart(int start) {
        put("start", start);
    }

    public void setEnd(int end) {
        put("end", end);
    }

    public void setTables(List<Table> tables) {
        put("tables", Table.revert(tables));
    }

    //
    //  Factory methods
    //

    public static GameHallContent seek(int start, int end) {
        GameHallContent content = new GameHallContent(ACT_SEEK_REQ);
        content.setStart(start);
        content.setEnd(end);
        return content;
    }

    public static GameHallContent seekResponse(int start, int end, List<Table> tables) {
        GameHallContent content = new GameHallContent(ACT_SEEK_RES);
        content.setStart(start);
        content.setEnd(end);
        content.setTables(tables);
        return content;
    }
}
