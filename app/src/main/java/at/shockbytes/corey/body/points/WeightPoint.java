package at.shockbytes.corey.body.points;

import io.realm.RealmObject;

public class WeightPoint extends RealmObject implements Comparable<WeightPoint> {

    private long time;
    private double weight;

    public WeightPoint() {
        this(0, 0);
    }

    public WeightPoint(long time, double weight) {
        this.time = time;
        this.weight = weight;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }


    @Override
    public int compareTo(WeightPoint weightPoint) {
        return (int) (weight - weightPoint.weight);
    }
}
