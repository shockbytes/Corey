package at.shockbytes.corey.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.ui.fragment.workoutpager.ExercisePagerFragment
import at.shockbytes.corey.ui.fragment.workoutpager.TimeExercisePagerFragment

/**
 * Author:  Martin Macheiner
 * Date:    03.12.2015
 */
class ExercisePagerAdapter(fm: FragmentManager, private val workout: Workout) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return workout.exercises.size
    }

    override fun getItem(position: Int): Fragment {

        val exercise = workout.exercises[position]
        return if (exercise is TimeExercise) {
            TimeExercisePagerFragment.newInstance(exercise)
        } else {
            ExercisePagerFragment.newInstance(exercise)
        }
    }
}