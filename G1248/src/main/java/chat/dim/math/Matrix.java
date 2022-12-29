/* license: https://mit-license.org
 * ==============================================================================
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Albert Moky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ==============================================================================
 */
package chat.dim.math;

import chat.dim.g1248.model.Board;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Matrix
 *  ~~~~~~
 *
 *  values: [
 *       1,  2,  3,  4,
 *       5,  6,  7,  8,
 *       9, 10, 11, 12,
 *      13, 14, 15, 16
 *  ]
 */
public class Matrix {

    public final Size size;
    private final int[] values;

    public Matrix(Size s) {
        super();
        size = s;
        values = new int[s.width * s.height];
    }
    public Matrix(int width, int height) {
        this(new Size(width, height));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object other) {
        if (super.equals(other)) {
            // same object
            return true;
        } else if (other instanceof Matrix) {
            // compare values
            Matrix matrix = (Matrix) other;
            if (size.equals(matrix.size)) {
                return Arrays.equals(values, matrix.values);
            } else {
                return false;
            }
        } else if (other instanceof List) {
            // compare list
            int length = ((List<?>) other).size();
            if (length == size.width * size.height) {
                return compare((List<Integer>) other);
            } else if (length == size.height) {
                return compare2D((List<List<Integer>>) other);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    private boolean compare(List<Integer> array) {
        int index;
        for (index = 0; index < values.length; ++index) {
            if (array.get(index) != values[index]) {
                return false;
            }
        }
        return true;
    }
    private boolean compare2D(List<List<Integer>> array) {
        List<Integer> line;
        int x, y;
        for (y = 0; y < size.height; ++y) {
            line = array.get(y);
            if (line.size() != size.width) {
                return false;
            }
            for (x = 0; x < size.width; ++x) {
                if (line.get(x) != values[x + y * size.width]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int y = 0; y < size.height; ++y) {
            sb.append("\n");
            for (int x = 0; x < size.width; ++x) {
                sb.append("\t");
                sb.append(values[x + y * size.width]);
                sb.append(",");
            }
        }
        sb.setCharAt(sb.length() - 1, '\n');
        sb.append("]");
        return sb.toString();
    }

    public List<Integer> toArray() {
        return Arrays.stream(values).boxed().collect(Collectors.toList());
    }

    public void clear() {
        int index;
        for (index = 0; index < values.length; ++index) {
            values[index] = 0;
        }
    }

    public void copy(Matrix other) {
        assert size.equals(other.size) : "matrix size not match: " + size + ", " + other.size;
        copy(other.values);
    }
    public void copy(int[] other) {
        assert values.length == other.length : "values length not match: " + values.length + ", " + other.length;
        System.arraycopy(other, 0, values, 0, size.width * size.height);
    }

    public int getValue(int x, int y) {
        return values[x + y * size.width];
    }
    public void setValue(int x, int y, int value) {
        values[x + y * size.width] = value;
    }

    /**
     *  Diagonal Transpose
     *  ~~~~~~~~~~~~~~~~~~
     *
     *      | 1 2 3 |      | 1 4 7 |
     *      | 4 5 6 |  =>  | 2 5 8 |
     *      | 7 8 9 |      | 3 6 9 |
     */
    public void transpose() {
        if (size.width == size.height) {
            // diagonal transpose
            squareTranspose();
        } else {
            rectangleTranspose();
        }
    }
    private void squareTranspose() {
        int x, y;
        for (y = 1; y < size.height; ++y) {
            for (x = 0; x < y; ++x) {
                //noinspection SuspiciousNameCombination
                swap(x, y, y, x);
            }
        }
    }
    private void rectangleTranspose() {
        /*
         *      | 1 2 3 |      | 1 4 |
         *      | 4 5 6 |  =>  | 2 5 |
         *                     | 3 6 |
         */
        int w = size.height;
        int h = size.width;
        int[] buffer = new int[w * h];
        int x, y;
        for (y = 0; y < size.height; ++y) {
            for (x = 0; x < size.width; ++x) {
                // copy (x, y) to (y, x)
                buffer[y + x * w] = values[x + y * size.width];
            }
        }
        // update values & size
        System.arraycopy(buffer, 0, values, 0, w * h);
        size.width = w;
        size.height = h;
    }

    /**
     *  Horizontal Transform
     *  ~~~~~~~~~~~~~~~~~~~~
     *  Flip Up to Down
     *
     *      | 1 2 3 |      | 7 8 9 |
     *      | 4 5 6 |  =>  | 4 5 6 |
     *      | 7 8 9 |      | 1 2 3 |
     */
    public void flipX() {
        final int mid = size.height / 2;
        int x, y;
        for (y = 0; y < mid; ++y) {
            for (x = 0; x < size.width; ++x) {
                swap(x, y, x, size.height - 1 - y);
            }
        }
    }

    /**
     *  Vertical Transform
     *  ~~~~~~~~~~~~~~~~~~
     *  Flip Left to Right
     *
     *      | 1 2 3 |      | 3 2 1 |
     *      | 4 5 6 |  =>  | 6 5 4 |
     *      | 7 8 9 |      | 9 8 7 |
     */
    public void flipY() {
        int mid = size.width / 2;
        int x, y;
        for (y = 0; y < size.height; ++y) {
            for (x = 0; x < mid; ++x) {
                swap(x, y, size.width - 1 - x, y);
            }
        }
    }

    private void swap(int x1, int y1, int x2, int y2) {
        int temp = values[x1 + y1 * size.width];
        values[x1 + y1 * size.width] = values[x2 + y2 * size.width];
        values[x2 + y2 * size.width] = temp;
    }

    public static void main(String[] args) {
        final int width = Board.DEFAULT_SIZE.width;
        final int height = Board.DEFAULT_SIZE.height;
        Matrix matrix = new Matrix(width, height);
        int x, y, value = 0;
        for (y = 0; y < height; ++y) {
            for (x = 0; x < width; ++x) {
                matrix.setValue(x, y, ++value);
            }
        }
        System.out.println("init matrix: " + matrix);
        matrix.transpose();
        System.out.println("transpose matrix: " + matrix);
        matrix.flipX();
        System.out.println("flipX matrix: " + matrix);
        matrix.flipY();
        System.out.println("flipY matrix: " + matrix);
    }
}
