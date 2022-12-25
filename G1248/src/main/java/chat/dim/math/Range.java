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
