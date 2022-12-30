package chat.dim.g1248.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import chat.dim.math.Size;
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
 *      tid    : {TABLE_ID},     // table id
 *      gid    : {GAME_ID},      // game id
 *      score  : 10000,          // current sore
 *      time   : {TIMESTAMP},    // last update time
 *      state  : [               // current state
 *          0, 1, 2, 4,
 *          0, 1, 2, 4,
 *          0, 1, 2, 4,
 *          0, 1, 2, 4
 *      ],
 *      size   : "4*4"
 *  }
 */
public class Board extends Score {

    public Board(Map<String, Object> board) {
        super(board);
    }

    public Board(int tid, int bid, Size size) {
        super();
        setTid(tid);
        setBid(bid);
        assert size.width == size.height : "error size: " + size;
        State state = new State(size.width);
        setSquares(state.toArray());
        setSize(size);
    }

    /**
     *  Get squares as current gate state
     *
     * @return squares
     */
    @SuppressWarnings("unchecked")
    public List<Square> getSquares() {
        Object state = get("state");
        return Square.convert((List<Integer>) state);
    }
    public void setSquares(List<?> state) {
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
    //  Factory methods
    //

    public static Board from(History history) {
        Map<String, Object> info = history.copyMap(false);
        info.remove("steps");
        return new Board(info);
    }

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

    public static List<Board> convertBoards(List<Object> array) {
        List<Board> boards = new ArrayList<>();
        Board value;
        for (Object item : array) {
            value = parseBoard(item);
            assert value != null : "board error: " + item;
            boards.add(value);
        }
        return boards;
    }
    public static List<Object> revertBoards(List<Board> boards) {
        List<Object> array = new ArrayList<>();
        for (Board item : boards) {
            assert item != null : "boards error: " + boards;
            array.add(item.toMap());
        }
        return array;
    }
}
