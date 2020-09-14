package at.shockbytes.corey.data.workout.external

data class ExternalWorkout(
        val name: String,
        val burnedEnergy: Int,
        val timestamp: Long,
        val source: ExternalWorkoutSource
)
