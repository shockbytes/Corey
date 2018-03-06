package at.shockbytes.corey.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.ui.fragment.workoutpager.ExercisePagerFragment
import at.shockbytes.corey.ui.fragment.workoutpager.TimeExercisePagerFragment

/**
 * @author Martin Macheiner
 * Date: 03.12.2015.
 */
class ExercisePagerAdapter(fm: FragmentManager?, private val workout: Workout) : FragmentStatePagerAdapter(fm) {

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