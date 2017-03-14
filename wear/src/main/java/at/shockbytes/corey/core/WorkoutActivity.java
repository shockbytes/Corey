package at.shockbytes.corey.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.fragment.WorkoutFragment;

public class WorkoutActivity extends Activity {

    private static final String ARG_WORKOUT = "arg_workout";

    public static Intent newIntent(Context context, Workout w) {
        return new Intent(context, WorkoutActivity.class).putExtra(ARG_WORKOUT, w);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Workout w = getIntent().getParcelableExtra(ARG_WORKOUT);
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_workout_container, WorkoutFragment.newInstance(w))
                .commit();
    }
}
