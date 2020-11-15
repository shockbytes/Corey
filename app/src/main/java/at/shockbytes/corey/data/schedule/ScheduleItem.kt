package at.shockbytes.corey.data.schedule

import at.shockbytes.corey.common.core.workout.model.LocationType
import at.shockbytes.corey.common.core.workout.model.WorkoutIconType
import at.shockbytes.corey.data.firebase.FirebaseStorable
import java.util.UUID

/**
 * Author:  Martin Macheiner
 * Date:    28.02.2017
 */
data class ScheduleItem(
    val name: String = "",
    val day: Int = -1,
    val id: String = UUID.randomUUID().toString(),
    val locationType: LocationType = LocationType.NONE,
    val workoutIconType: WorkoutIconType = WorkoutIconType.NONE
) : FirebaseStorable {

    val isEmpty: Boolean
        get() = name.isEmpty()

    override fun copyWithNewId(newId: String): FirebaseStorable = copy(id = newId)
}
