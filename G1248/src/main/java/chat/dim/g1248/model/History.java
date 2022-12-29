package chat.dim.g1248.model;

import java.util.Map;

import chat.dim.format.Base64;
import chat.dim.math.Size;
import chat.dim.type.Mapper;

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
public class History extends Board {

    public History(Map<String, Object> history) {
        super(history);
    }

    // create new game history
    public History(int tid, int bid, Size size) {
        super(tid, bid, size);
    }

    /**
     *  Get size
     *
     * @return game board size
     */
    public Size getBoardSize() {
        return getSize();
    }
    public void setBoardSize(Size size) {
        setSize(size);
    }
    public void setBoardSize(int size) {
        setSize(size, size);
    }

    /**
     *  Get history steps
     *
     * @return steps records
     */
    public byte[] getSteps() {
        String steps = (String) get("steps");
        if (steps == null || steps.length() == 0) {
            return new byte[0];
        }
        return Base64.decode(steps);
    }
    public void setSteps(byte[] steps) {
        String base64 = Base64.encode(steps);
        put("steps", base64);
    }
    public void addStep(byte next) {
        byte[] steps = getSteps();
        byte[] buffer;
        if (steps == null || steps.length == 0) {
            buffer = new byte[1];
        } else {
            buffer = new byte[steps.length + 1];
            System.arraycopy(steps, 0, buffer, 0, steps.length);
        }
        buffer[buffer.length - 1] = next;
        setSteps(buffer);
    }

    public State getMatrix() {
        // 1. deduce state
        byte[] steps = getSteps();
        State state = State.deduce(steps);
        // 2. check board size
        if (!getBoardSize().equals(state.size)) {
            throw new AssertionError("board size not match");
        }
        // 3. check score
        if (state.getScore() != getScore()) {
            throw new AssertionError("score not match");
        }
        // 4. check state
        if (!state.equals(get("state"))) {
            throw new AssertionError("state not match");
        }
        // OK
        return state;
    }
    public void setMatrix(State state) {
        setSquares(state.toArray());
        setScore(state.getScore());
        setBoardSize(state.size);
    }

    //
    //  Factory method
    //

    @SuppressWarnings("unchecked")
    public static History parseHistory(Object history) {
        if (history == null) {
            return null;
        } else if (history instanceof History) {
            return (History) history;
        } else if (history instanceof Mapper) {
            history = ((Mapper) history).toMap();
        }
        return new History((Map<String, Object>) history);
    }
}

