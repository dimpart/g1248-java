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
package chat.dim.type;

import java.util.Date;

public final class Time extends Date {

    public Time() {
        super();
    }
    public Time(long mills) {
        super(mills);
    }

    public static float getTimestamp(Date date) {
        return date.getTime() / 1000.0f;
    }

    //
    //  Factory method
    //

    public static Time parseTime(Object time) {
        if (time == null) {
            return null;
        } else if (time instanceof Time) {
            return (Time) time;
        } else if (time instanceof Date) {
            return new Time(((Date) time).getTime());
        }
        assert time instanceof Number : "time error: " + time;
        float value = ((Number) time).floatValue();
        return new Time((long) (value * 1000));
    }

    public static Time now() {
        return new Time();
    }
}
