package at.shockbytes.corey.data.workout.external

import at.shockbytes.corey.common.core.CoreyDate

data class ExternalWorkout(
    val name: String,
    val burnedEnergy: Int,
    val date: CoreyDate,
    val source: ExternalWorkoutSource
)
