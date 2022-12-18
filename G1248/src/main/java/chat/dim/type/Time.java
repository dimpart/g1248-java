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
