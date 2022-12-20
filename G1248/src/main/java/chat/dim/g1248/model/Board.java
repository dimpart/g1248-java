package chat.dim.g1248.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.dim.math.Size;
import chat.dim.protocol.ID;
import chat.dim.type.Dictionary;
import chat.dim.type.Mapper;

/**
 *  Game Board
 *  ~~~~~~~~~~
 *
 *  JSON: {
 *      bid    : {BOARD_ID},     // 0, 1, 2, 3
 *      player : "{PLAYER_ID}",  // current player
 *
 *      // details, will not show in hall
 *      gid    : {GAME_ID},      // game id
 *      score  : 10000,          // current sore
 *      state  : [               // current state
 *          0, 1, 2, 4,
 *          0, 1, 2, 4,
 *          0, 1, 2, 4,
 *          0, 1, 2, 4
 *      ],
 *      size   : "4*4"
 *  }
 */
public class Board extends Dictionary {

    public Board(Map<String, Object> board) {
        super(board);
    }

    /**
     *  Get Board ID
     *
     * @return board ID
     */
    public int getBid() {
        Object bid = get("bid");
        return bid == null ? 0 : ((Number) bid).intValue();
    }
    public void setBid(int id) {
        put("bid", id);
    }

    /**
     *  Get Game ID
     *
     * @return game history ID
     */
    public int getGid() {
        Object gid = get("gid");
        return gid == null ? 0 : ((Number) gid).intValue();
    }
    public void setGid(int id) {
        put("gid", id);
    }

    /**
     *  Get Game Player
     *
     * @return player ID
     */
    public ID getPlayer() {
        return ID.parse(get("player"));
    }
    public void setPlayer(ID player) {
        put("player", player.toString());
    }

    /**
     *  Get Score Value
     *
     * @return score value
     */
    public int getScore() {
        Object score = get("score");
        return score == null ? 0 : ((Number) score).intValue();
    }
    public void setScore(int score) {
        put("score", score);
    }

    /**
     *  Get squares as current gate state
     *
     * @return squares
     */
    @SuppressWarnings("unchecked")
    public List<Square> getState() {
        Object state = get("state");
        return Square.convert((List<Integer>) state);
    }
    public void setState(List<?> state) {
        put("state", Square.revert(state));
    }

    // board size
    public Size getSize() {
        Object size = get("size");
        if (size instanceof String) {
            return Size.from((String) size);
        } else {
            return DEFAULT_SIZE;
        }
    }
    public void setSize(Size size) {
        put("size", size.toString());
    }
    public void setSize(int width, int height) {
        setSize(new Size(width, height));
    }
    public static final Size DEFAULT_SIZE = new Size(4, 4);

    //
    //  Factory method
    //
    @SuppressWarnings("unchecked")
    public static Board parseBoard(Object board) {
        if (board == null) {
            return null;
        } else if (board instanceof Board) {
            return (Board) board;
        } else if (board instanceof Mapper) {
            board = ((Mapper) board).toMap();
        }
        return new Board((Map<String, Object>) board);
    }

    public static List<Board> convert(List<Object> array) {
        List<Board> boards = new ArrayList<>();
        Board value;
        for (Object item : array) {
            value = parseBoard(item);
            assert value != null : "board error: " + item;
            boards.add(value);
        }
        return boards;
    }
    public static List<Object> revert(List<Board> boards) {
        List<Object> array = new ArrayList<>();
        for (Board item : boards) {
            assert item != null : "boards error: " + boards;
            array.add(item.toMap());
        }
        return array;
    }
}
