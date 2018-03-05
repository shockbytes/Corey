package at.shockbytes.corey.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.ExerciseAdapter;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.util.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Martin Macheiner
 *         Date: 01.11.2015.
 */
public class WorkoutDetailActivity extends AppCompatActivity {

    private static final String ARG_WORKOUT = "arg_workout";

    public static Intent newIntent(Context context, @NonNull Workout workout) {
        return new Intent(context, WorkoutDetailActivity.class)
                .putExtra(ARG_WORKOUT, workout);
    }

    @BindView(R.id.activity_training_detail_imgview_ext_toolbar)
    protected ImageView imgViewExtToolbar;

    @BindView(R.id.activity_training_detail_imgview_body_region)
    protected ImageView imgViewMuscles;

    @BindView(R.id.activity_training_detail_txt_duration)
    protected TextView txtDuration;

    @BindView(R.id.activity_training_detail_txt_exercise_count)
    protected TextView txtExerciseCount;

    @BindView(R.id.activity_training_recyclerview)
    protected RecyclerView recyclerViewExercises;

    @BindView(R.id.activity_training_btn_start)
    protected Button btnStart;

    @BindView(R.id.activity_training_detail_txt_title)
    protected TextView txtName;

    private Workout workout;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
            getWindow().setExitTransition(new Fade(Fade.OUT));
            //getWindow().setEnterTransition(new Explode());
        }
        setContentView(R.layout.activity_workout_detail);
        unbinder = ButterKnife.bind(this);

        if (getIntent().getExtras() != null && getSupportActionBar() != null) {

            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            workout = getIntent().getParcelableExtra(ARG_WORKOUT);
            if (workout != null) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getWindow().setStatusBarColor(ContextCompat
                            .getColor(this, workout.getDarkColorResForIntensity()));
                }
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat
                        .getColor(this, workout.getColorResForIntensity())));
            }
        }

        initializeViews();
    }

    private void initializeViews() {

        btnStart.setText(getString(R.string.activity_workout_detail_btn_start, workout.getDisplayableName()));

        imgViewExtToolbar.setBackgroundResource(workout.getColorResForIntensity());
        imgViewMuscles.setImageDrawable(AppUtils.INSTANCE.createRoundedBitmapFromResource(this,
                workout.getImageResForBodyRegion(), workout.getColorResForIntensity()));

        txtName.setText(workout.getDisplayableName());
        txtDuration.setText(getString(R.string.duration_with_minutes, workout.getDuration()));
        txtExerciseCount.setText(getString(R.string.exercises_with_count, workout.getExerciseCount()));

        ExerciseAdapter exerciseAdapter = new ExerciseAdapter(this, new ArrayList<Exercise>());
        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExercises.setAdapter(exerciseAdapter);
        exerciseAdapter.setData(workout.getExercises());

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = WorkoutActivity.newIntent(getApplicationContext(), workout);
                ActivityOptionsCompat options = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(WorkoutDetailActivity.this);
                startActivity(intent, options.toBundle());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources()
                .getDisplayMetrics());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setElevation(dp);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

}
