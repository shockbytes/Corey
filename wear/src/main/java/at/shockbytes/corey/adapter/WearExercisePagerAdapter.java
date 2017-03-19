package at.shockbytes.corey.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.TimeExercise;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.fragment.workoutpager.WearExercisePagerFragment;
import at.shockbytes.corey.fragment.workoutpager.WearTimeExercisePagerFragment;


/**
 * @author Martin Macheiner
 *         Date: 03.12.2015.
 */

// FragmentStatePagerAdapter
public class WearExercisePagerAdapter extends FragmentStatePagerAdapter {

    private Workout workout;

    public WearExercisePagerAdapter(FragmentManager fm, Workout workout) {
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
            return WearTimeExercisePagerFragment.newInstance(timeExercise);
        } else {
            return WearExercisePagerFragment.newInstance(exercise);
        }
    }

}