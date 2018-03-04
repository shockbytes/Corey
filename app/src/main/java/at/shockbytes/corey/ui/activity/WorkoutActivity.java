package at.shockbytes.corey.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.transition.Fade;
import android.view.MenuItem;
import android.view.Window;

import at.shockbytes.corey.R;
import at.shockbytes.corey.ui.fragment.WorkoutFragment;
import at.shockbytes.corey.ui.fragment.dialogs.WorkoutMessageDialogFragment;
import at.shockbytes.corey.common.core.workout.model.Workout;

public class WorkoutActivity extends AppCompatActivity {

    private static final String ARG_WORKOUT = "arg_workout";

    public static Intent newIntent(Context context, Workout workout) {
        return new Intent(context, WorkoutActivity.class)
                .putExtra(ARG_WORKOUT, workout);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().setExitTransition(new Fade());
            getWindow().setEnterTransition(new Explode());
        }
        setContentView(R.layout.activity_workout);

        Workout w = getIntent().getParcelableExtra(ARG_WORKOUT);

        lockOrientation();
        setupActionBar(w.getDisplayableName(), w.getColorResForIntensity(), w.getDarkColorResForIntensity());
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, WorkoutFragment.newInstance(w))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            showQuitDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar(String workoutName, int actionbarColor, int statusColor) {

        if (getSupportActionBar() != null) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(workoutName);
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.drawable.ic_cancel);
            ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, actionbarColor)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, statusColor));
            }
        }
    }

    private void lockOrientation() {
        int o = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(o);
    }

    private void showQuitDialog() {
        WorkoutMessageDialogFragment fragment = WorkoutMessageDialogFragment
                .newInstance(WorkoutMessageDialogFragment.MessageType.QUIT);
        fragment.setOnMessageAgreeClickedListener(new WorkoutMessageDialogFragment
                .OnMessageAgreeClickedListener() {
            @Override
            public void onMessageAgreeClicked() {
                supportFinishAfterTransition();
            }
        });
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

}
