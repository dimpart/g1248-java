package chat.dim.math;

public final class Range {

    public static final Range ZERO = new Range(0, 0);

    public int start;
    public int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof Range) {
            Range range = (Range) other;
            return start == range.start && end == range.end;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(start) * 13 + Integer.hashCode(end);
    }

    @Override
    public String toString() {
        return start + "," + end;
    }

    public static Range from(String string) {
        String[] pair = string.split(",");
        if (pair.length == 2) {
            int start = Integer.parseInt(pair[0]);
            int end = Integer.parseInt(pair[1]);
            return new Range(start, end);
        } else {
            return null;
        }
    }
}
