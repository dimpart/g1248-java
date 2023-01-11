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
 *      matrix : [               // current state matrix
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
        setSize(size);
        setMatrix(new Stage(size));
    }

    /**
     *  Get squares as current gate state matrix
     *
     * @return squares
     */
    @SuppressWarnings("unchecked")
    public Stage getMatrix() {
        Object matrix = get("matrix");
        if (matrix instanceof List) {
            List<Integer> numbers = (List<Integer>) matrix;
            Size size = getSize();
            assert numbers.size() == size.width * size.height : "matrix size not match: " + size + ", " + numbers;
            Stage stage = new Stage(size);
            stage.copy(numbers);
            return stage;
        }
        throw new AssertionError("matrix error: " + matrix);
    }
    public void setMatrix(Stage matrix) {
        setMatrix(matrix.toArray());
    }
    public void setMatrix(List<Integer> numbers) {
        put("matrix", numbers);
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
