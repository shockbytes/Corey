package at.shockbytes.corey.body.points;

import android.os.Parcel;
import android.support.annotation.NonNull;

import io.realm.RealmObject;

public class BmiPoint extends RealmObject implements Comparable<BmiPoint> {

    private long time;
    private double bmi;

    public BmiPoint() {
        this(0, 0);
    }

    public BmiPoint(long time, double bmi) {
        this.time = time;
        this.bmi = bmi;
    }

    protected BmiPoint(Parcel in) {
        time = in.readLong();
        bmi = in.readDouble();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }

    @Override
    public int compareTo(@NonNull BmiPoint bmiPoint) {
        if (bmi == bmiPoint.bmi) {
            return 0;
        } else if (bmi > bmiPoint.bmi) {
            return 1;
        } else {
            return -1;
        }
    }
}