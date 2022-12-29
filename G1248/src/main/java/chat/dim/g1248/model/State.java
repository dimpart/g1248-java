package chat.dim.g1248.model;

import java.util.List;

import chat.dim.format.Hex;
import chat.dim.math.Matrix;
import chat.dim.math.Point;
import chat.dim.utils.Log;

public class State extends Matrix {

    public State(int width) {
        //noinspection SuspiciousNameCombination
        super(width, width);
        assert width > 0 : "size error: " + width;
    }

    @Override
    public String toString() {
        return "score: " + getScore() +
                ", matrix: " + super.toString();
    }

    public List<Square> getSquares() {
        return Square.convert(toArray());
    }

    /**
     *  Calculate score
     *
     * @return current score
     */
    public int getScore() {
        int score = 0;
        int x, y;
        for (y = 0; y < size.height; ++y) {
            for (x = 0; x < size.width; ++x) {
                score += Square.getScore(getValue(x, y));
            }
        }
        return score;
    }

    /**
     *  Show next number
     *
     * @param step - next step
     * @return null on full
     */
    public Point showNumber(Step step) {
        int spaces = getEmptySpaces();
        if (spaces == 0) {
            // it's full
            return null;
        }
        int pos = step.getPosition(spaces);
        int x, y, val;
        for (y = 0; y < size.height; ++y) {
            for (x = 0; x < size.width; ++x) {
                val = getValue(x, y);
                if (val == 0) {
                    pos -= 1;
                }
                if (pos < 0) {
                    setValue(x, y, step.getNumber());
                    return new Point(x, y);
                }
            }
        }
        return null;
    }
    private int getEmptySpaces() {
        int count = 0;
        for (int y = 0; y < size.height; ++y) {
            for (int x = 0; x < size.width; ++x) {
                if (getValue(x, y) == 0) {
                    count += 1;
                }
            }
        }
        return count;
    }

    /**
     *  Do swipe
     *
     * @param step - next step
     */
    public boolean swipe(Step step) {
        boolean moved;
        Step.Direction dir = step.getDirection();
        switch (dir) {
            case UP:
                Log.debug("swipe up");
                transpose();
                moved = swipeLeft();
                transpose();
                break;
            case RIGHT:
                Log.debug("swipe right");
                flipY();
                moved = swipeLeft();
                flipY();
                break;
            case DOWN:
                Log.debug("swipe down");
                flipX();
                transpose();
                moved = swipeLeft();
                transpose();
                flipX();
                break;
            default:
                Log.debug("swipe left");
                moved = swipeLeft();
        }
        return moved;
    }
    private boolean swipeLeft() {
        int y, x, n;
        int end = size.width - 1;
        int value, next;
        boolean moved = false;
        for (y = 0; y < size.height; ++y) {
            // swipe this line
            for (x = 0; x < end;) {
                // get value at current position
                value = getValue(x, y);
                n = x + 1;
                if (value == 0) {
                    // current position empty,
                    // seek next number
                    for (; n < size.width; ++n) {
                        next = getValue(n, y);
                        if (next != 0) {
                            // got one, move it to current position
                            setValue(x, y, next);
                            setValue(n, y, 0);
                            moved = true;
                            break;
                        }
                    }
                    // DO NOT move x now, event moved
                    // waiting for next number equal
                } else {
                    // seek next number equals
                    for (; n < size.width; ++n) {
                        next = getValue(n, y);
                        if (next == 0) {
                            // skip empty space
                            continue;
                        }
                        if (next == value) {
                            // got same number, plus to current position
                            setValue(x, y, value << 1);
                            setValue(n, y, 0);
                            moved = true;
                        }
                        break;
                    }
                    // this position ok, go on
                    ++x;
                }
                if (n == size.width) {
                    // no more number after, this line finished
                    break;
                }
            }
        }
        return moved;
    }

    /**
     *  Run all steps to build current state
     *
     * @param steps - history steps
     * @return game state
     */
    public static State deduce(byte[] steps) {
        assert steps.length > 0 : "steps error: " + Hex.encode(steps);
        State state = new State(Board.DEFAULT_SIZE.width);
        // place first number
        Step next = new Step(steps[0]);
        state.showNumber(next);
        // run all steps after
        int index;
        for (index = 1; index < steps.length; ++index) {
            next = new Step(steps[index]);
            if (!state.swipe(next)) {
                throw new AssertionError("step error: " + next);
            }
            if (state.showNumber(next) == null) {
                // it's full
                break;
            }
        }
        if (index < steps.length) {
            throw new AssertionError("steps error: " + index + ", " + steps.length);
        }
        return state;
    }
}
