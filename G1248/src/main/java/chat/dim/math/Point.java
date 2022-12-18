package chat.dim.math;

public final class Point {

    public static final Point ZERO = new Point(0, 0);

    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof Point) {
            Point point = (Point) other;
            return x == point.x && y == point.y;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) * 13 + Integer.hashCode(y);
    }

    @Override
    public String toString() {
        return x + "," + y;
    }

    public static Point from(String string) {
        String [] pair = string.split(",");
        if (pair.length == 2) {
            int x = Integer.getInteger(pair[0]);
            int y = Integer.getInteger(pair[1]);
            return new Point(x, y);
        } else {
            return null;
        }
    }
}
