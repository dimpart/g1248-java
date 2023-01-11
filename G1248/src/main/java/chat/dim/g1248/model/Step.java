package chat.dim.g1248.model;

import java.util.Random;

/**
 *  History Step
 *  ~~~~~~~~~~~~
 *
 *  data format:
 *
 *          0   1   2   3   4   5   6   7
 *        +---+---+---+---+---+---+---+---+
 *        |       |                   |   |
 *        |   A   |         B         | C |
 *        |       |                   |   |
 *        +---+---+---+---+---+---+---+---+
 *
 *  protocol:
 *      A   - swipe direction; it must be ZERO at first step
 *      C   - next number, 0 => "1", 1 => "2"
 *      B+C - next position to show the number
 */
public final class Step {

    private final byte value;

    public Step(byte step) {
        value = step;
    }

    @Override
    public String toString() {
        return "<" + getClass().getSimpleName() + " move=\"" + getDirection() + "\" number=" + getNumber() + " />";
    }

    public byte getByte() {
        return value;
    }

    /**
     *  Get swipe direction
     *
     * @return Left|Right|Up|Down
     */
    public Direction getDirection() {
        return Direction.from((value >> 6) & 3);
    }

    /**
     *  Get next number to show up
     *
     * @return 1|2
     */
    public int getNumber() {
        return (value & 1) + 1;
    }

    /**
     *  Get next position to show up
     *
     * @param spaces - count of empty spaces
     * @return offset
     */
    public int getPosition(int spaces) {
        int rand = value & 0x3F;
        return rand % spaces;
    }

    //
    //  Factory methods
    //

    public static Step from(byte value) {
        return new Step(value);
    }

    public static Step first() {
        Random random = new Random();
        byte suffix = (byte) (random.nextInt() & 0x3F);
        return from(suffix);
    }

    public static Step next(Direction direction) {
        Random random = new Random();
        byte suffix = (byte) (random.nextInt() & 0x3F);
        byte prefix = (byte) ((direction.value & 0x03) << 6);
        return from((byte) (prefix | suffix));
    }

    /**
     *  Swipe direction
     */
    public enum Direction {

        LEFT  (0),  // 00:  x--
        RIGHT (1),  // 01:  x++
        UP    (2),  // 10:  y--
        DOWN  (3);  // 11:  y++

        public final int value;

        Direction(int d) {
            value = d;
        }

        public boolean equals(Direction other) {
            return value == other.value;
        }
        public boolean equals(int other) {
            return value == other;
        }

        static Direction from(int dir) {
            if (dir == 0) {
                return LEFT;
            } else if (dir == 1) {
                return RIGHT;
            } else if (dir == 2) {
                return UP;
            } else {
                return DOWN;
            }
        }
    }
}
