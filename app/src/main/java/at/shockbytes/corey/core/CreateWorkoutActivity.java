package at.shockbytes.corey.core;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;

import at.shockbytes.corey.R;
import at.shockbytes.corey.fragment.CreateWorkoutFragment;
import at.shockbytes.corey.common.core.workout.model.Workout;

/**
 * @author Martin Macheiner
 *         Date: 27.10.2015.
 */
public class CreateWorkoutActivity extends AppCompatActivity
        implements CreateWorkoutFragment.OnChangeSystemBarsColorListener {

    private static String ARG_EDIT = "arg_edit_workout";

    public static Intent newIntent(Context context, Workout editWorkout) {
        return new Intent(context, CreateWorkoutActivity.class)
                .putExtra(ARG_EDIT, editWorkout);
    }

    private Workout workout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setExitTransition(new Fade(Fade.OUT));
            getWindow().setEnterTransition(new Slide(Gravity.BOTTOM));
        }
        setContentView(R.layout.activity_create_workout);
        setupActionBar();

        if (getIntent().getExtras() != null) {
            workout = getIntent().getExtras().getParcelable(ARG_EDIT);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_create_workout, CreateWorkoutFragment.newInstance(workout))
                .commit();
    }

    private void setupActionBar() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, workout.getDarkColorResForIntensity()));
            }*/
            //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, workout.getColorResForIntensity())));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChangeSystemBarsColor(int from, int fromDark, int to, int toDark) {
        tintSystemBars(ContextCompat.getColor(this, from), ContextCompat.getColor(this, fromDark),
                ContextCompat.getColor(this, to), ContextCompat.getColor(this, toDark));
    }

    private void tintSystemBars(final int fromToolbar, final int fromStatusBar,
                                final int toToolbar, final int toStatusBar) {

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                //Get current position
                float position = animation.getAnimatedFraction();

                //Blend colors and apply to bars
                int blended = blendColors(fromStatusBar, toStatusBar, position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(blended);
                }

                //Apply to toolbar and actionbar
                blended = blendColors(fromToolbar, toToolbar, position);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(blended));
                }
            }
        });
        anim.setDuration(250).start();
    }

    private int blendColors(int from, int to, float ratio) {

        final float inverseRatio = 1f - ratio;

        float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }


}

