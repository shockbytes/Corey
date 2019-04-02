package at.shockbytes.corey.common.core.workout.model

import android.os.Parcelable
import at.shockbytes.corey.common.core.R
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

/**
 * Author:  Martin Macheiner
 * Date:    27.10.2015
 */
@Parcelize
data class Workout(
    var id: String = "",
    private var name: String = "", // private necessary for custom setter
    var duration: Int = 0, // in minutes
    var exercises: MutableList<Exercise> = mutableListOf(),
    var intensity: Intensity = Intensity.MEDIUM,
    var bodyRegion: BodyRegion = BodyRegion.CORE,
    var locationType: LocationType = LocationType.INDOOR
) : Comparable<Workout>, Parcelable {

    enum class Intensity {
        EASY, MEDIUM, HARD, BEAST
    }

    enum class BodyRegion {
        LEGS, CORE, ARMS, CHEST, FULL_BODY
    }

    val displayableName: String
        @Exclude
        get() = name.replace("_".toRegex(), " ")

    val exerciseCount: Int
        get() = exercises.size

    val colorResForIntensity: Int
        @Exclude
        get() {
            return when (intensity) {
                Workout.Intensity.EASY -> R.color.workout_intensity_easy
                Workout.Intensity.MEDIUM -> R.color.workout_intensity_medium
                Workout.Intensity.HARD -> R.color.workout_intensity_hard
                Workout.Intensity.BEAST -> R.color.workout_intensity_beast
            }
        }

    val darkColorResForIntensity: Int
        @Exclude
        get() {
            return when (intensity) {
                Workout.Intensity.EASY -> R.color.workout_intensity_easy_dark
                Workout.Intensity.MEDIUM -> R.color.workout_intensity_medium_dark
                Workout.Intensity.HARD -> R.color.workout_intensity_hard_dark
                Workout.Intensity.BEAST -> R.color.workout_intensity_beast_dark
            }
        }

    val imageResForBodyRegion: Int
        @Exclude
        get() {
            return when (bodyRegion) {
                Workout.BodyRegion.LEGS -> R.drawable.ic_bodyregion_legs
                Workout.BodyRegion.CORE -> R.drawable.ic_bodyregion_core
                Workout.BodyRegion.ARMS -> R.drawable.ic_bodyregion_arms
                Workout.BodyRegion.CHEST -> R.drawable.ic_bodyregion_chest
                Workout.BodyRegion.FULL_BODY -> R.drawable.ic_bodyregion_whole
            }
        }

    val equipment: Equipment
        @Exclude
        get() {
            val max = exercises.map { it.equipment.ordinal }.max() ?: 0
            return Equipment.fromIndex(max)
        }

    fun setName(value: String) {
        name = value.replace(" ", "_")
    }

    fun getName(): String {
        return name
    }

    override fun compareTo(other: Workout): Int {
        return id.compareTo(other.id)
    }
}
