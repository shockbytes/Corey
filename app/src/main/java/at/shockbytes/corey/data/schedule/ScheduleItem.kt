package at.shockbytes.corey.data.schedule

import at.shockbytes.corey.common.core.workout.model.LocationType
import at.shockbytes.corey.common.core.workout.model.WorkoutIconType
import java.util.UUID

/**
 * Author:  Martin Macheiner
 * Date:    28.02.2017
 */
data class ScheduleItem(
    val name: String = "",
    var day: Int = -1,
    val id: String = UUID.randomUUID().toString(),
    val locationType: LocationType = LocationType.NONE,
    val workoutIconType: WorkoutIconType = WorkoutIconType.NONE
) {

    val isEmpty: Boolean
        get() = name.isEmpty()
}
