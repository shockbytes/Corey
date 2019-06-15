package at.shockbytes.corey.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.ui.fragment.workoutpager.WearExercisePagerFragment
import at.shockbytes.corey.ui.fragment.workoutpager.WearTimeExercisePagerFragment

/**
 * Author:  Martin Macheiner
 * Date:    03.12.2015
 */
class WearExercisePagerAdapter(
    fm: androidx.fragment.app.FragmentManager,
    private val workout: Workout
) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return workout.exercises.size
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {

        val exercise = workout.exercises[position]
        return if (exercise is TimeExercise) {
            WearTimeExercisePagerFragment.newInstance(exercise)
        } else {
            WearExercisePagerFragment.newInstance(exercise)
        }
    }
}