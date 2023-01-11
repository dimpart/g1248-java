package chat.dim.g1248.protocol;

import java.util.List;
import java.util.Map;

import chat.dim.g1248.model.Room;

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
 *      act   : "{ACTION}",        // "seeking", "rooms"
 *
 *      start : 0,
 *      end   : 20,
 *      rooms : [...]
 *  }
 */
public class GameHallContent extends GameCustomizedContent {

    public static final String MOD_NAME = "hall";

    // querying game rooms in the hall
    public static final String ACT_SEEK_REQ = "seeking";
    public static final String ACT_SEEK_RES = "rooms";

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

    public void setRooms(List<Room> rooms) {
        put("rooms", Room.revertRooms(rooms));
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

    public static GameHallContent seekResponse(int start, int end, List<Room> rooms) {
        GameHallContent content = new GameHallContent(ACT_SEEK_RES);
        content.setStart(start);
        content.setEnd(end);
        content.setRooms(rooms);
        return content;
    }
}
