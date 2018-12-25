package at.shockbytes.corey.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.ui.fragment.workoutpager.WearExercisePagerFragment
import at.shockbytes.corey.ui.fragment.workoutpager.WearTimeExercisePagerFragment


/**
 * Author:  Martin Macheiner
 * Date:    03.12.2015
 */
class WearExercisePagerAdapter(fm: FragmentManager,
                               private val workout: Workout) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return workout.exercises.size
    }

    override fun getItem(position: Int): Fragment {

        val exercise = workout.exercises[position]
        return if (exercise is TimeExercise) {
            WearTimeExercisePagerFragment.newInstance(exercise)
        } else {
            WearExercisePagerFragment.newInstance(exercise)
        }
    }

}