package at.shockbytes.corey.common.core.workout.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import at.shockbytes.corey.common.core.R
import at.shockbytes.corey.common.core.Sortable

/**
 * Author:  Martin Macheiner
 * Date:    20.03.2015
 */
open class Exercise(
    open var name: String = "",
    open var repetitions: Int = 0,
    open var equipment: Equipment = Equipment.BODYWEIGHT
) : Parcelable, Sortable {

    constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readInt(),
            parcel.readSerializable() as Equipment)

    open fun getDisplayName(context: Context): String {
        return if (repetitions == 0) {
            name.replace("_".toRegex(), " ")
        } else context.getString(R.string.format_exercise, repetitions, name).replace("_".toRegex(), " ")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(repetitions)
        parcel.writeSerializable(equipment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return ArrayList<Exercise>(size).toTypedArray()
        }
    }
}
