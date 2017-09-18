package at.shockbytes.corey.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.body.BodyManager;
import at.shockbytes.corey.body.wearable.WearableManager;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.fragment.BodyFragment;
import at.shockbytes.corey.fragment.ScheduleFragment;
import at.shockbytes.corey.fragment.WorkoutOverviewFragment;
import at.shockbytes.corey.fragment.dialogs.DesiredWeightDialogFragment;
import at.shockbytes.corey.util.AppParams;
import at.shockbytes.corey.util.schedule.ScheduleManager;
import at.shockbytes.corey.workout.WorkoutManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import icepick.Icepick;
import icepick.State;

public class MainActivity extends AppCompatActivity
        implements TabLayout.OnTabSelectedListener {

    public static Intent newIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @State
    protected int tabPosition;

    @Inject
    protected BodyManager bodyManager;

    @Inject
    protected WorkoutManager workoutManager;

    @Inject
    protected ScheduleManager scheduleManager;

    @Inject
    protected WearableManager wearableManager;

    @Bind(R.id.main_content)
    protected FrameLayout mainContent;

    @Bind(R.id.main_layout)
    protected View mainLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.main_appbar)
    protected AppBarLayout appBar;

    @Bind(R.id.main_tablayout)
    protected TabLayout tabLayout;

    @Bind(R.id.main_fab_edit)
    protected FloatingActionButton fabNewWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((CoreyApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);

        tabPosition = 2; // Initialize it with one, can be overwritten by statement below
        Icepick.restoreInstanceState(this, savedInstanceState);

        initializeViews();
        bodyManager.poke(this);
        workoutManager.poke();
        scheduleManager.poke();
        wearableManager.connect(this);

        if (bodyManager.getDesiredWeight() <= 0) { // Ask for desired weight when not set
            askForDesiredWeight();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            activityTransition(SettingsActivity.newIntent(getApplicationContext()), -1);
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            supportFinishAfterTransition();
        } else if (id == R.id.action_desired_weight) {
            askForDesiredWeight();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wearableManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == AppParams.REQUEST_CODE_CREATE_WORKOUT && resultCode == RESULT_OK) {

            Workout w = data.getParcelableExtra(AppParams.INTENT_EXTRA_NEW_WORKOUT);
            boolean isUpdated = data.getBooleanExtra(AppParams.INTENT_EXTRA_WORKOUT_UPDATED, false);

            if (!isUpdated) {
                workoutManager.addWorkout(w);
            } else {
                workoutManager.updateWorkout(w);
            }
        }
    }

    private void initializeViews() {

        setSupportActionBar(toolbar);
        tabLayout.addOnTabSelectedListener(this);
        TabLayout.Tab initialTab = tabLayout.getTabAt(tabPosition);
        if (initialTab != null) {
            initialTab.select();
        }

        fabNewWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityTransition(CreateWorkoutActivity.newIntent(getApplicationContext(), null),
                        AppParams.REQUEST_CODE_CREATE_WORKOUT);
            }
        });
    }

    private void askForDesiredWeight() {
        DesiredWeightDialogFragment fragment = DesiredWeightDialogFragment.newInstance();
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    private void activityTransition(Intent intent, int reqCodeForResult) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this);
        if (reqCodeForResult > 0) {
            startActivityForResult(intent, reqCodeForResult, options.toBundle());
        } else {
            startActivity(intent, options.toBundle());
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        appBar.setExpanded(true, true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        tabPosition = tab.getPosition();
        switch (tabPosition) {

            case 0:
                fabNewWorkout.hide();
                //ft.replace(R.id.main_content, RunningOverviewFragment.newInstance());
                break;

            case 1:
                fabNewWorkout.show();
                ft.replace(R.id.main_content, WorkoutOverviewFragment.newInstance());
                break;

            case 2:
                fabNewWorkout.hide();
                ft.replace(R.id.main_content, ScheduleFragment.newInstance());
                break;

            case 3:
                fabNewWorkout.hide();
                ft.replace(R.id.main_content, BodyFragment.newInstance());
                break;
        }
        ft.commit();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
