package chat.dim.math;

public final class Size {

    public static final Size ZERO = new Size(0, 0);

    public int width;
    public int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof Size) {
            Size size = (Size) other;
            return width == size.width && height == size.height;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(width) * 13 + Integer.hashCode(height);
    }

    @Override
    public String toString() {
        return width + "*" + height;
    }

    public static Size from(String string) {
        String[] pair = string.split("\\*");
        if (pair.length == 2) {
            int width = Integer.parseInt(pair[0]);
            int height = Integer.parseInt(pair[1]);
            return new Size(width, height);
        } else {
            return null;
        }
    }
}
