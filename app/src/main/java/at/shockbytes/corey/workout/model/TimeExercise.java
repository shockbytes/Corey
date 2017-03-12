package at.shockbytes.corey.workout.model;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;

import at.shockbytes.corey.R;

/**
 * @author Martin Macheiner
 *         Date: 20.03.2015.
 */
public class TimeExercise extends Exercise {

    // Both in seconds
    private int workDuration;
    private int restDuration;

    public TimeExercise() {
        this("", 0, 0);
    }

    public TimeExercise(String name) {
        this(name, 0);
    }

    public TimeExercise(String name, int reps) {
        super(name, reps);
    }

    public TimeExercise(String name, int reps, int workduration) {
        this(name, reps, workduration, workduration);
    }

    public TimeExercise(String name, int reps, int workduration, int restduration) {
        super(name, reps);
        workDuration = workduration;
        restDuration = restduration;
    }

    protected TimeExercise(Parcel in) {
        super(in);
        workDuration = in.readInt();
        restDuration = in.readInt();
    }

    public static final Creator<TimeExercise> CREATOR = new Creator<TimeExercise>() {
        @Override
        public TimeExercise createFromParcel(Parcel in) {
            return new TimeExercise(in);
        }

        @Override
        public TimeExercise[] newArray(int size) {
            return new TimeExercise[size];
        }
    };

    @Override
    public String getDisplayName(@NonNull Context context) {

        int minutes = getWorkoutDurationInSeconds() / 60;
        if (minutes != 0) {
            return context.getString(R.string.format_time_exercise, minutes, getName()).replaceAll("_", " ");
        } else {
            return getName().replaceAll("_", " ");
        }
    }

    public int getWorkoutDurationInSeconds() {
        return (repetitions * (workDuration + restDuration));
    }

    public int getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(int workDuration) {
        this.workDuration = workDuration;
    }

    public int getRestDuration() {
        return restDuration;
    }

    public void setRestDuration(int restDuration) {
        this.restDuration = restDuration;
    }

    @Override
    public String toString() {
        return super.toString() + "\nWorkduration:\t" + workDuration + "\nRestduration:\t" + restDuration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeInt(workDuration);
        parcel.writeInt(restDuration);
    }

}
