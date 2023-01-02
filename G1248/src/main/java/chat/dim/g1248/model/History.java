package chat.dim.g1248.model;

import java.util.Date;
import java.util.Map;

import chat.dim.format.Base64;
import chat.dim.format.Hex;
import chat.dim.math.Size;
import chat.dim.type.Mapper;
import chat.dim.type.Pair;
import chat.dim.utils.Log;

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
        correct();
    }

    // create new game history
    public History(int tid, int bid, Size size) {
        super(tid, bid, size);
    }

    @Override
    public Map<String, Object> toMap() {
        correct();
        return super.toMap();
    }

    private void correct() {
        // 1. deduce state from history steps
        byte[] steps = getSteps();
        Pair<State, Integer> result = State.deduce(steps, getSize());
        State matrix = result.first;
        int count = result.second;
        if (count < steps.length) {
            // step error
            byte[] partial = new byte[count];
            System.arraycopy(steps, 0, partial, 0, count);
            setSteps(partial);
            setSquares(matrix.toArray());
            setScore(matrix.getScore());
            Log.error("steps error, cut at " + count + "/" + steps.length + ": " + Hex.encode(partial));
        }
    }

    public boolean isValid() {
        try {
            return getMatrix() != null;
        } catch (AssertionError e) {
            //e.printStackTrace();
            return false;
        }
    }
    public boolean isOver() {
        return getMatrix().isOver();
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
        // update time
        setTime(new Date());
    }

    public State getMatrix() {
        // 1. deduce state from history steps
        byte[] steps = getSteps();
        State matrix = State.deduce(steps, getSize()).first;
        // 2. check score
        if (matrix.getScore() != getScore()) {
            throw new AssertionError("score not match");
        }
        // 3. check state
        if (!matrix.equals(get("state"))) {
            throw new AssertionError("state not match");
        }
        // OK
        return matrix;
    }
    public void setMatrix(State state) {
        setSquares(state.toArray());
        setScore(state.getScore());
        setSize(state.size);
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

