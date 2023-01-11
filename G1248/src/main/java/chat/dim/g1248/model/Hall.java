package chat.dim.g1248.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.dim.math.Range;
import chat.dim.type.Dictionary;
import chat.dim.type.Mapper;

/**
 *  Game Hall
 *  ~~~~~~~~~
 *
 *  JSON: {
 *      range : "{start},{end}",  // count(rooms) == end - start
 *      rooms : [
 *          {
 *              rid    : {ROOM_ID},
 *              // current playing boards
 *              boards : [
 *                  {
 *                      bid    : {BOARD_ID},     // 0, 1, 2, 3
 *                      player : "{PLAYER_ID}",  // current player
 *                  },
 *                  //...
 *              ],
 *              // score of the winner in this room, may be null
 *              best   : {
 *                  bid    : {BOARD_ID},
 *                  gid    : {GAME_ID},      // game id
 *                  player : "{PLAYER_ID}",  // game player
 *                  score  : 10000,          // game sore
 *                  time   : {TIMESTAMP}
 *              }
 *          },
 *          //...
 *      ]
 *  }
 */
public class Hall extends Dictionary {

    public Hall(Map<String, Object> hall) {
        super(hall);
    }

    /**
     *  Range for game rooms
     *
     * @return [start, end)
     */
    public Range getRange() {
        Object range = get("range");
        if (range instanceof String) {
            return Range.from((String) range);
        } else {
            return Range.ZERO;
        }
    }
    public void setRange(Range range) {
        put("range", range.toString());
    }
    public void setRange(int start, int end) {
        setRange(new Range(start, end));
    }

    /**
     *  Game rooms within range [start, end)
     *
     * @return rooms
     */
    @SuppressWarnings("unchecked")
    public List<Room> getRooms() {
        Object value = get("rooms");
        if (value == null) {
            return new ArrayList<>();
        }
        return Room.convertRooms((List<Object>) value);
    }
    public void setRooms(List<Room> rooms) {
        put("rooms", Room.revertRooms(rooms));
    }

    //
    //  Factory method
    //
    @SuppressWarnings("unchecked")
    public Hall parseHall(Object hall) {
        if (hall == null) {
            return null;
        } else if (hall instanceof Hall) {
            return (Hall) hall;
        } else if (hall instanceof Mapper) {
            hall = ((Mapper) hall).toMap();
        }
        return new Hall((Map<String, Object>) hall);
    }
}
