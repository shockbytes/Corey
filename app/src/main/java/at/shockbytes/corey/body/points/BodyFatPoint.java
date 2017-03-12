package at.shockbytes.corey.body.points;

import io.realm.RealmObject;

public class BodyFatPoint extends RealmObject {

    private long time;
    private double bodyFat;

    public BodyFatPoint() {
        this(0, 0);
    }

    public BodyFatPoint(long time, double bodyFat) {
        this.time = time;
        this.bodyFat = bodyFat;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(double bodyFat) {
        this.bodyFat = bodyFat;
    }

}
