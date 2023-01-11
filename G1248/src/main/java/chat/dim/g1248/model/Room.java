package chat.dim.g1248.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.dim.type.Dictionary;
import chat.dim.type.Mapper;

/**
 *  Game Room
 *  ~~~~~~~~~
 *
 *  JSON: {
 *      rid    : {ROOM_ID},
 *      // current playing boards
 *      boards : [
 *          {
 *              bid    : {BOARD_ID},     // 0, 1, 2, 3
 *              player : "{PLAYER_ID}",  // current player
 *
 *              // details, will not show in hall
 *              gid    : {GAME_ID},      // game id
 *              score  : 10000,          // current sore
 *              matrix : [               // current state matrix
 *                  0, 1, 2, 4,
 *                  0, 1, 2, 4,
 *                  0, 1, 2, 4,
 *                  0, 1, 2, 4
 *              ],
 *              size   : "4*4"
 *          },
 *          //...
 *      ]
 *  }
 */
public class Room extends Dictionary {

    public Room(Map<String, Object> room) {
        super(room);
    }

    // create new room
    public Room() {
        super();
    }

    /**
     *  Get Room ID
     *
     * @return 0
     */
    public int getRid() {
        Object rid = get("rid");
        return rid == null ? 0 : ((Number) rid).intValue();
    }
    public void setRid(int rid) {
        put("rid", rid);
    }

    /**
     *  Current playing boards in this room
     *
     * @return boards
     */
    @SuppressWarnings("unchecked")
    public List<Board> getBoards() {
        Object value = get("boards");
        if (value == null) {
            return new ArrayList<>();
        }
        return Board.convertBoards((List<Object>) value);
    }
    public void setBoards(List<Board> boards) {
        put("boards", Board.revertBoards(boards));
    }

    /**
     *  Get best score
     *
     * @return the winner's score
     */
    public Score getBest() {
        return Score.parseScore(get("best"));
    }
    public void setBest(Score best) {
        put("best", best.toMap());
    }

    //
    //  Factory methods
    //
    @SuppressWarnings("unchecked")
    public static Room parseRoom(Object room) {
        if (room == null) {
            return null;
        } else if (room instanceof Room) {
            return (Room) room;
        } else if (room instanceof Mapper) {
            room = ((Mapper) room).toMap();
        }
        return new Room((Map<String, Object>) room);
    }

    public static List<Room> convertRooms(List<Object> array) {
        List<Room> rooms = new ArrayList<>();
        Room value;
        for (Object item : array) {
            value = parseRoom(item);
            assert value != null : "room error: " + item;
            rooms.add(value);
        }
        return rooms;
    }
    public static List<Object> revertRooms(List<Room> rooms) {
        List<Object> array = new ArrayList<>();
        for (Room item : rooms) {
            assert item != null : "rooms error: " + rooms;
            array.add(item.toMap());
        }
        return array;
    }
}
