package at.shockbytes.corey.common.core.workout.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import at.shockbytes.corey.common.core.R

enum class WorkoutIconType(
    @DrawableRes val iconRes: Int?,
    @ColorRes val iconTint: Int? = null,
    @ColorRes val notificationTint: Int? = iconTint
) {
    NONE(null, null),
    // Built in
    LEGS(R.drawable.ic_bodyregion_legs, R.color.workout_icon_color),
    CORE(R.drawable.ic_bodyregion_core, R.color.workout_icon_color),
    ARMS(R.drawable.ic_bodyregion_arms, R.color.workout_icon_color),
    CHEST(R.drawable.ic_bodyregion_chest, R.color.workout_icon_color),
    FULL_BODY(R.drawable.ic_bodyregion_whole, R.color.workout_icon_color),
    // Custom
    FREELETICS(R.drawable.ic_freeletics, R.color.workout_icon_color),
    RUNNING(R.drawable.ic_tab_running, R.color.workout_icon_color),
    TENNIS(R.drawable.ic_tennis, R.color.workout_icon_color),
    WALKING(R.drawable.ic_walking, R.color.workout_icon_color),
    STRETCHING(R.drawable.ic_stretching, R.color.workout_icon_color),
    CYCLING(R.drawable.ic_biking, R.color.workout_icon_color),
    FOOTBALL(R.drawable.ic_football, R.color.workout_icon_color),
    WEIGHT_ROOM(R.drawable.ic_equipment_gym, R.color.workout_icon_color),
    SWIMMING(R.drawable.ic_swimming, R.color.workout_icon_color),
    HIKING(R.drawable.ic_hiking, R.color.workout_icon_color);

    companion object {

        fun fromBodyRegion(region: Workout.BodyRegion): WorkoutIconType {
            return when (region) {
                Workout.BodyRegion.LEGS -> LEGS
                Workout.BodyRegion.CORE -> CORE
                Workout.BodyRegion.ARMS -> ARMS
                Workout.BodyRegion.CHEST -> CHEST
                Workout.BodyRegion.FULL_BODY -> FULL_BODY
            }
        }
    }
}