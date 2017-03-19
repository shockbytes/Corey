package at.shockbytes.corey.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.KeyEvent;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.fragment.WorkoutFragment;

public class WorkoutActivity extends WearableActivity {

    public interface OnWorkoutNavigationListener {

        void moveToNext();

        void moveToPrevious();

        void onEnterAmbient();

        void onUpdateAmbient();

        void onExitAmbient();

    }

    private static final String ARG_WORKOUT = "arg_workout";

    public static Intent newIntent(Context context, Workout w) {
        return new Intent(context, WorkoutActivity.class).putExtra(ARG_WORKOUT, w);
    }

    private OnWorkoutNavigationListener navigationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        setAmbientEnabled();

        Workout w = getIntent().getParcelableExtra(ARG_WORKOUT);
        WorkoutFragment workoutFragment = WorkoutFragment.newInstance(w);
        navigationListener = workoutFragment;
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_workout_container, workoutFragment)
                .commit();
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        navigationListener.onEnterAmbient();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        navigationListener.onUpdateAmbient();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        navigationListener.onExitAmbient();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_NAVIGATE_NEXT:
                navigationListener.moveToNext();
                break;
            case KeyEvent.KEYCODE_NAVIGATE_PREVIOUS:
                navigationListener.moveToPrevious();
                break;
        }

        return super.onKeyDown(keyCode, event);
    }



}
