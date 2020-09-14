package at.shockbytes.corey.common.core

/**
 * See: https://globalrph.com/medcalcs/harris-benedict-equation-updated-basal-metabolic-rate/
 */
enum class ActivityLevel(val factor: Double) {
    SEDENTARY(1.2),
    MILD_ACTIVITY(1.375),
    MODERATE_ACTIVITY(1.55),
    HEAVY_ACTIVITY(1.7),
    EXTREME(1.9)
}