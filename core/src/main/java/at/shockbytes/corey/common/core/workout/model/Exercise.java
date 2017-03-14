package at.shockbytes.corey.common.core.workout.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import at.shockbytes.corey.common.core.R;


/**
 * @author Martin Macheiner
 *         Date: 20.03.2015.
 */
public class Exercise implements Parcelable {

    protected String name;
    protected int repetitions;

    public Exercise() {
        this("", 0);
    }

    public Exercise(String name) {
        this(name, 0);
    }

    public Exercise(String name, int repetitions) {
        setName(name);
        this.repetitions = repetitions;
    }

    protected Exercise(Parcel in) {
        name = in.readString();
        repetitions = in.readInt();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public String getDisplayName(@NonNull Context context) {

        if (repetitions == 0) {
            return name.replaceAll("_", " ");
        }
        return context.getString(R.string.format_exercise, repetitions, name).replaceAll("_", " ");
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Exercise:\t\t" + name + "\nRepetetions:\t" + repetitions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(repetitions);
    }

}
