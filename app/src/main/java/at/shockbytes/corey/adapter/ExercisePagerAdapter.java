package at.shockbytes.corey.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import at.shockbytes.corey.fragment.workoutpager.ExercisePagerFragment;
import at.shockbytes.corey.fragment.workoutpager.TimeExercisePagerFragment;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.TimeExercise;
import at.shockbytes.corey.common.core.workout.model.Workout;

/**
 * @author Martin Macheiner
 *         Date: 03.12.2015.
 */
public class ExercisePagerAdapter extends FragmentStatePagerAdapter {

    private Workout workout;

    public ExercisePagerAdapter(FragmentManager fm, Workout workout) {
        super(fm);
        this.workout = workout;
    }

    @Override
    public int getCount() {
        return workout.getExercises().size();
    }

    @Override
    public Fragment getItem(int position) {

        Exercise exercise = workout.getExercises().get(position);
        if (exercise instanceof TimeExercise) {
            TimeExercise timeExercise = (TimeExercise) exercise;
            return TimeExercisePagerFragment.newInstance(timeExercise);
        } else {
            return ExercisePagerFragment.newInstance(exercise);
        }
    }

}