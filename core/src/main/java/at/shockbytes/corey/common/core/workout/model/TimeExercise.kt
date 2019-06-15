package at.shockbytes.corey.common.core.workout.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import at.shockbytes.corey.common.core.R
import com.google.firebase.database.Exclude

/**
 * Author:  Martin Macheiner
 * Date:    20.03.2015
 */
class TimeExercise(
    name: String,
    reps: Int = 0,
    eq: Equipment = Equipment.BODYWEIGHT,
    var workDuration: Int = 0,
    var restDuration: Int = workDuration
) : Exercise(name, reps, eq), Parcelable {

    val workoutDurationInSeconds: Int
        @Exclude
        get() = repetitions * (workDuration + restDuration)

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readInt(),
            parcel.readSerializable() as Equipment,
            parcel.readInt(),
            parcel.readInt())

    override fun getDisplayName(context: Context): String {

        val minutes = workoutDurationInSeconds / 60
        return if (minutes != 0) {
            context.getString(R.string.format_time_exercise, minutes, name).replace("_".toRegex(), " ")
        } else {
            name.replace("_".toRegex(), " ")
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(workDuration)
        parcel.writeInt(restDuration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TimeExercise> {
        override fun createFromParcel(parcel: Parcel): TimeExercise {
            return TimeExercise(parcel)
        }

        override fun newArray(size: Int): Array<TimeExercise?> {
            return ArrayList<TimeExercise>(size).toTypedArray()
        }
    }
}
