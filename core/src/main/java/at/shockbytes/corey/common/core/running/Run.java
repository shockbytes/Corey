package at.shockbytes.corey.common.core.running;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author Martin Macheiner
 *         Date: 05.09.2017.
 */

public class Run implements Parcelable {

    private static final int LOCATIONS_FOR_CURRENT_PACE = 10;

    private List<Location> locations;

    private long id;

    private long startTime;
    private double distance;
    private long time; // in ms
    private int calories;
    private String avgPace; // 00:00

    public Run() {
        this(System.currentTimeMillis());
    }

    public Run(long startTime) {
        this.startTime = startTime;
        locations = new ArrayList<>();
    }

    protected Run(Parcel in) {
        locations = in.createTypedArrayList(Location.CREATOR);
        id = in.readLong();
        startTime = in.readLong();
        distance = in.readDouble();
        time = in.readLong();
        calories = in.readInt();
        avgPace = in.readString();
    }

    public static final Creator<Run> CREATOR = new Creator<Run>() {
        @Override
        public Run createFromParcel(Parcel in) {
            return new Run(in);
        }

        @Override
        public Run[] newArray(int size) {
            return new Run[size];
        }
    };

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getCalories() {
        return calories;
    }

    public void setAvgPace(String avgPace) {
        this.avgPace = avgPace;
    }

    public String getAveragePace() {
        return avgPace;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {

        return "Start time:\t\t\t" +
                new Date(startTime).toString() +
                "\nDistance:\t\t\t\t" +
                distance +
                " km\nTime:\t\t\t\t\t" +
                time / 1000 +
                " seconds\nAvg. pace:\t\t\t" +
                avgPace +
                " km/h\nCalories:\t\t\t\t" +
                calories +
                " kcal\nLocation points:\t\t\t" +
                locations.size() +
                "\n---------------------\n";
    }

    double getCurrentPaceDistance() {

        if (locations.size() < LOCATIONS_FOR_CURRENT_PACE) {
            return 0;
        }
        return locations.get(locations.size() - LOCATIONS_FOR_CURRENT_PACE)
                .distanceTo(locations.get(locations.size() - 1)) / 1000d;
    }

    long getCurrentPaceTime() {

        if (locations.size() < LOCATIONS_FOR_CURRENT_PACE) {
            return 0;
        }
        return (locations.get(locations.size() - 1)).getTime() -
                locations.get(locations.size() - LOCATIONS_FOR_CURRENT_PACE).getTime();
    }

    public LatLng getStartLatLng() {
        if (locations != null && locations.size() > 0) {
            return new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());
        }
        return null;
    }

    public LatLng getLastLatLng() {
        if (locations != null && locations.size() > 0) {
            return new LatLng(locations.get(locations.size()-1).getLatitude(),
                    locations.get(locations.size()-1).getLongitude());
        }
        return null;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(locations);
        parcel.writeLong(id);
        parcel.writeLong(startTime);
        parcel.writeDouble(distance);
        parcel.writeLong(time);
        parcel.writeInt(calories);
        parcel.writeString(avgPace);
    }

}
