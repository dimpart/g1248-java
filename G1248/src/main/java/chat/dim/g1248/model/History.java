package chat.dim.g1248.model;

import java.util.List;
import java.util.Map;

import chat.dim.format.Base64;
import chat.dim.math.Size;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

/**
 *  Game History
 *  ~~~~~~~~~~~~
 *
 *  JSON: {
 *      tid    : {TABLE_ID},
 *      bid    : {BOARD_ID},
 *      gid    : {GAME_ID},      // game id
 *      player : "{PLAYER_ID}",  // game player
 *      score  : 10000,          // game sore
 *      time   : {TIMESTAMP},
 *
 *      steps  : "BASE64",       // encoded steps
 *
 *      state  : [               // current state
 *          0, 1, 2, 4,
 *          0, 1, 2, 4,
 *          0, 1, 2, 4,
 *          0, 1, 2, 4
 *      ],
 *      size   : "4*4"
 *  }
 */
public class History extends Score {

    public History(Map<String, Object> history) {
        super(history);
    }

    /**
     *  Get size
     *
     * @return game board size
     */
    public Size getBoardSize() {
        Object size = get("size");
        if (size instanceof String) {
            return Size.from((String) size);
        } else {
            return Board.DEFAULT_SIZE;
        }
    }
    public void setBoardSize(Size size) {
        put("size", size.toString());
    }
    public void setBoardSize(int width, int height) {
        setBoardSize(new Size(width, height));
    }

    /**
     *  Get history steps
     *
     * @return steps records
     */
    public byte[] getSteps() {
        Object steps = get("steps");
        if (steps == null) {
            return null;
        }
        return Base64.decode((String) steps);
    }
    public void setSteps(byte[] steps) {
        String base64 = Base64.encode(steps);
        put("steps", base64);
    }

    public State getState() {
        // 1. deduce state
        byte[] steps = getSteps();
        State state = State.deduce(steps);
        // 2. check score
        if (state.getScore() != getScore()) {
            throw new ValueException("score not match");
        }
        // 3. check state
        if (!state.equals(get("state"))) {
            throw new ValueException("state not match");
        }
        // 4. check board size
        if (!getBoardSize().equals(state.size)) {
            throw new ValueException("board size not match");
        }
        // OK
        return state;
    }
    public void setState(State state) {
        put("state", state.toArray());
        setScore(state.getScore());
        setBoardSize(state.size);
    }
    public void setState(List<Integer> state) {
        put("state", state);
    }
}

