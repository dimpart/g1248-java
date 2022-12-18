package chat.dim.g1248.model;

import chat.dim.math.Matrix;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

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

    /**
     *  Calculate score
     *
     * @return current score
     */
    public int getScore() {
        int score = 0;
        int x, y, num;
        for (y = 0; y < size.height; ++y) {
            for (x = 0; x < size.width; ++x) {
                num = getValue(x, y);
                score += Math.pow(3, Math.log(num) / LN2);
            }
        }
        return score;
    }
    private static final double LN2 = Math.log(2);

    /**
     *  Show next number
     *
     * @param step - next step
     * @return false on full
     */
    public boolean showNumber(Step step) {
        int spaces = getEmptySpaces();
        if (spaces == 0) {
            // it's full
            return false;
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
                    return spaces > 1;
                }
            }
        }
        return false;
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
                System.out.println("swipe up");
                transpose();
                moved = swipeLeft();
                transpose();
                break;
            case RIGHT:
                System.out.println("swipe right");
                flipY();
                moved = swipeLeft();
                flipY();
                break;
            case DOWN:
                System.out.println("swipe down");
                flipX();
                transpose();
                moved = swipeLeft();
                transpose();
                flipX();
                break;
            default:
                System.out.println("swipe left");
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

    public boolean stepForward(byte next) {
        Step step = new Step(next);
        // 1. do swipe
        boolean moved = swipe(step);
        // 2. show next number
        boolean alive = showNumber(step);
        return moved || alive;
    }

    /**
     *  Run all steps to build current state
     *
     * @param steps - history steps
     * @return game state
     */
    public static State deduce(byte[] steps) {
        State state = new State(4);
        int index;
        for (index = 0; index < steps.length; ++index) {
            if (!state.stepForward(steps[index])) {
                // it's full
                break;
            }
        }
        if (index < steps.length) {
            throw new ValueException("steps error: " + index + ", " + steps.length);
        }
        return state;
    }

    public static void main(String[] args) {
        boolean moved;
        State state = new State(4);
        moved = state.stepForward((byte) 0xAA);
        System.out.println("state: " + state + ", moved: " + moved);
        moved = state.stepForward((byte) 0x55);
        System.out.println("state: " + state + ", moved: " + moved);
        moved = state.stepForward((byte) 0x99);
        System.out.println("state: " + state + ", moved: " + moved);
        moved = state.stepForward((byte) 0x22);
        System.out.println("state: " + state + ", moved: " + moved);
        moved = state.stepForward((byte) 0xFF);
        System.out.println("state: " + state + ", moved: " + moved);
        moved = state.stepForward((byte) 0x33);
        System.out.println("state: " + state + ", moved: " + moved);
    }
}
