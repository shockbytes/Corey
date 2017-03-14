package at.shockbytes.corey.common.core.workout.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import at.shockbytes.corey.common.core.R;


/**
 * @author Martin Macheiner
 *         Date: 27.10.2015.
 */
public class Workout implements Comparable<Workout>, Parcelable {

    public enum Intensity {EASY, MEDIUM, HARD, BEAST}

    public enum BodyRegion {LEGS, CORE, ARMS, CHEST, FULL_BODY}

    private String id;

    private String name;
    private int duration; // in minutes
    private List<Exercise> exercises;

    private Intensity intensity;
    private BodyRegion bodyRegion;

    public Workout() {
        this("", 0, Intensity.MEDIUM, BodyRegion.CORE);
    }

    public Workout(String name, int duration, Intensity intensity, BodyRegion bodyRegion) {
        this(name, duration, intensity, bodyRegion, new ArrayList<Exercise>());
    }

    public Workout(String name, int duration, Intensity intensity,
                   BodyRegion bodyRegion, ArrayList<Exercise> exercises) {
        setName(name);
        this.duration = duration;
        this.intensity = intensity;
        this.bodyRegion = bodyRegion;
        this.exercises = exercises;
    }

    protected Workout(Parcel in) {
        id = in.readString();
        name = in.readString();
        duration = in.readInt();
        bodyRegion = BodyRegion.values()[in.readInt()];
        intensity = Intensity.values()[in.readInt()];
        Parcelable[] parcels = in.readParcelableArray(Exercise.class.getClassLoader());
        exercises = new ArrayList<>();
        for (Parcelable p: parcels) {
            exercises.add((Exercise) p);
        }
    }

    public String getName() {
        return name;
    }

    public Workout setName(String name) {
        this.name = "\"" + name + "\"";
        return this;
    }

    public Workout setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public Workout setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        return this;
    }

    public int getExerciseCount() {
        return exercises.size();
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public Workout setIntensity(Intensity intensity) {
        this.intensity = intensity;
        return this;
    }

    public Workout setBodyRegion(BodyRegion bodyRegion) {
        this.bodyRegion = bodyRegion;
        return this;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public int getColorResForIntensity() {

        int color = 0;
        switch (intensity) {

            case EASY:
                color = R.color.workout_intensity_easy;
                break;
            case MEDIUM:
                color = R.color.workout_intensity_medium;
                break;
            case HARD:
                color = R.color.workout_intensity_hard;
                break;
            case BEAST:
                color = R.color.workout_intensity_beast;
                break;
        }
        return color;
    }

    public int getDarkColorResForIntensity() {

        int color = 0;
        switch (intensity) {

            case EASY:
                color = R.color.workout_intensity_easy_dark;
                break;
            case MEDIUM:
                color = R.color.workout_intensity_medium_dark;
                break;
            case HARD:
                color = R.color.workout_intensity_hard_dark;
                break;
            case BEAST:
                color = R.color.workout_intensity_beast_dark;
                break;
        }
        return color;
    }

    public int getImageResForBodyRegion() {

        int bodyRegionImage = 0;
        switch (bodyRegion) {

            case LEGS:
                bodyRegionImage = R.drawable.ic_bodyregion_legs;
                break;

            case CORE:
                bodyRegionImage = R.drawable.ic_bodyregion_core;
                break;

            case ARMS:
                bodyRegionImage = R.drawable.ic_bodyregion_arms;
                break;

            case CHEST:
                bodyRegionImage = R.drawable.ic_bodyregion_chest;
                break;

            case FULL_BODY:
                bodyRegionImage = R.drawable.ic_bodyregion_whole;
                break;
        }
        return bodyRegionImage;
    }

    public BodyRegion getBodyRegion() {
        return bodyRegion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final Creator<Workout> CREATOR = new Creator<Workout>() {
        @Override
        public Workout createFromParcel(Parcel in) {
            return new Workout(in);
        }

        @Override
        public Workout[] newArray(int size) {
            return new Workout[size];
        }
    };

    @Override
    public int compareTo(@NonNull Workout another) {
        return id.compareTo(another.id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Workout && compareTo((Workout) obj) == 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeInt(duration);
        parcel.writeInt(bodyRegion.ordinal());
        parcel.writeInt(intensity.ordinal());
        parcel.writeParcelableArray(exercises.toArray(new Exercise[exercises.size()]), i);
    }

    @Override
    public String toString() {

        String str = "";
        for (Exercise ex : exercises) {
            str += ex.toString()+"\n\n";
        }

        return "Title:\t" + name +
                "\nId:" + id +
                "\nDuration: " + duration + " min" +
                "\nIntensity: " + intensity +
                "\nMuscles: " + bodyRegion +
                "\nExercises: " + exercises.size() +
                "\n" + str;
    }
}
