package chat.dim.g1248.model;

import java.util.ArrayList;
import java.util.List;

public class Square extends Number {

    private int value;

    public Square(int v) {
        super();
        setValue(v);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public static boolean checkValue(int value) {
        if (value == 0) {
            return true;
        } else {
            double power = log2(value);
            return Math.floor(power) == power;
        }
    }

    private static double log2(int value) {
        return Math.log(value) / LN2;
    }
    private static final double LN2 = Math.log(2);

    public static int getScore(int value) {
        assert checkValue(value) : "value error: " + value;
        if (value == 0) {
            return 0;
        } else {
            return (int) Math.pow(3, log2(value));
        }
    }

    public int getScore() {
        return getScore(value);
    }

    /**
     *  Get number order
     *
     *  mapping: {
     *      0: 0,
     *      1: 1,
     *      2: 2,
     *      4: 3,
     *      8: 4,
     *      //...
     *  }
     *
     * @return order
     */
    public static int getOrder(int value) {
        assert checkValue(value) : "value error: " + value;
        if (value == 0) {
            return 0;
        } else {
            return (int) (log2(value) + 1);
        }
    }
    public int getOrder() {
        return getOrder(value);
    }

    public void setValue(int v) {
        assert checkValue(v) : "value error: " + value;
        value = v;
    }
    public int getValue() {
        return value;
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    //
    //  Factory methods
    //
    public static Square from(int value) {
        if (checkValue(value)) {
            return new Square(value);
        }
        throw new AssertionError("value error: " + value);
    }

    public static List<Square> convert(List<Integer> array) {
        if (array == null || array.size() == 0) {
            return new ArrayList<>();
        }
        List<Square> squares = new ArrayList<>();
        for (Integer item : array) {
            squares.add(from(item));
        }
        return squares;
    }
    public static List<Integer> revert(List<?> squares) {
        if (squares == null || squares.size() == 0) {
            return new ArrayList<>();
        }
        List<Integer> array = new ArrayList<>();
        for (Object item : squares) {
            if (item instanceof Number) {
                array.add(((Number) item).intValue());
            }
        }
        return array;
    }
}
