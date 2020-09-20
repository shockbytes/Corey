package at.shockbytes.corey.common.core

/**
 * See: https://globalrph.com/medcalcs/harris-benedict-equation-updated-basal-metabolic-rate/
 */
enum class ActivityLevel(val factor: Double, val level: Int) {
    SEDENTARY(1.2, level = 1),
    MILD_ACTIVITY(1.375, level = 2),
    MODERATE_ACTIVITY(1.55, level = 3),
    HEAVY_ACTIVITY(1.7, level = 4),
    EXTREME(1.9, level = 5);

    companion object {

        fun ofLevel(level: Int): ActivityLevel {
            return values().find { it.level == level }
                    ?: throw IllegalStateException("Level $level out of bounds for ActivityLevel enum!")
        }
    }
}