package chat.dim.g1248.model;

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

    /**
     *  Swipe direction
     */
    public enum Direction {

        LEFT  (0),  // 00:  x--
        RIGHT (1),  // 01:  x++
        UP    (2),  // 10:  y--
        DOWN  (3);  // 11:  y++

        final int value;

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
