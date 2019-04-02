package at.shockbytes.corey.data.schedule

import at.shockbytes.corey.common.core.workout.model.LocationType

data class SchedulableItem(
    val title: String,
    val locationType: LocationType
)