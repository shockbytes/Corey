package at.shockbytes.corey.common.core.workout.model

import android.content.Context
import android.os.Parcelable
import at.shockbytes.corey.common.core.R
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize


/**
 * @author Martin Macheiner
 * Date: 20.03.2015.
 */
@Parcelize
class TimeExercise(@Exclude private var o_name: String = "",
                   @Exclude private var o_reps: Int = 0,
                   var workDuration: Int = 0,
                   var restDuration: Int = workDuration) : Exercise(o_name, o_reps), Parcelable {

    val workoutDurationInSeconds: Int
        @Exclude
        get() = repetitions * (workDuration + restDuration)

    override fun getDisplayName(context: Context): String {

        val minutes = workoutDurationInSeconds / 60
        return if (minutes != 0) {
            context.getString(R.string.format_time_exercise, minutes, name).replace("_".toRegex(), " ")
        } else {
            name.replace("_".toRegex(), " ")
        }
    }

}
