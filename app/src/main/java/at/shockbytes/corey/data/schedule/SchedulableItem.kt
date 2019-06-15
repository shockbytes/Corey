package at.shockbytes.corey.data.schedule

import at.shockbytes.corey.common.core.workout.model.LocationType
import at.shockbytes.corey.common.core.workout.model.WorkoutIconType

data class SchedulableItem(
    val title: String,
    val locationType: LocationType,
    val workoutType: WorkoutIconType
)