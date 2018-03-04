package at.shockbytes.corey.core;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.drawer.WearableDrawerLayout;
import android.support.wearable.view.drawer.WearableNavigationDrawer;
import android.view.Gravity;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.CoreyNavigationAdapter;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.fragment.RunningFragment;
import at.shockbytes.corey.fragment.WorkoutOverviewFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends WearableActivity
        implements CommunicationManager.OnHandheldDataListener, CoreyNavigationAdapter.OnNavigationItemSelectedListener {

    public interface OnWorkoutsLoadedListener {

        void onWorkoutLoaded(List<Workout> workouts);
    }

    @Inject
    protected CommunicationManager communicationManager;

    @BindView(R.id.main_navigation_drawer)
    protected WearableNavigationDrawer navigationDrawer;

    @BindView(R.id.main_drawer_layout)
    protected WearableDrawerLayout drawerLayout;

    private OnWorkoutsLoadedListener workoutListener;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((WearCoreyApp) getApplication()).getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this);
        setAmbientEnabled();
        setupNavigationDrawer();

        communicationManager.connectIfDeviceAvailable(this);

        onNavigationItemSelected(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        communicationManager.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        communicationManager.onPause();
    }

    @Override
    public void onWorkoutsAvailable(List<Workout> workouts) {
        if (workoutListener != null) {
            workoutListener.onWorkoutLoaded(workouts);
        }
    }

    @Override
    public void onCachedWorkoutsAvailable(List<Workout> workouts) {
        if (workoutListener != null) {
            workoutListener.onWorkoutLoaded(workouts);
        }
    }

    @Override
    public void onNavigationItemSelected(int index) {

        switch(index) {

            case 0:
                showWorkoutFragment();
                break;

            case 1:
                showRunningFragment();
                break;

            case 2:
                showSettings();
                break;
        }
    }

    private void showWorkoutFragment() {

        WorkoutOverviewFragment workoutOverviewFragment = WorkoutOverviewFragment
                .newInstance(communicationManager.getCachedWorkouts());
        workoutListener = workoutOverviewFragment;
        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, workoutOverviewFragment)
                .commit();
    }

    private void showRunningFragment() {

        getFragmentManager().beginTransaction()
                .replace(R.id.main_content, RunningFragment.Companion.newInstance())
                .commit();
    }

    private void showSettings() {
        startActivity(CoreyPreferenceActivity.Companion.newIntent(this));
    }

    private void setupNavigationDrawer() {

        navigationDrawer.setAdapter(new CoreyNavigationAdapter(this, getNavigationItems(), this));
        drawerLayout.peekDrawer(Gravity.TOP);
    }

    private List<CoreyNavigationAdapter.NavigationItem> getNavigationItems() {

        return Arrays.asList(
                new CoreyNavigationAdapter.NavigationItem(R.string.navigation_workout,
                        R.drawable.ic_workout),
                new CoreyNavigationAdapter.NavigationItem(R.string.navigation_running,
                        R.drawable.ic_tab_running),
                new CoreyNavigationAdapter.NavigationItem(R.string.navigation_settings,
                        R.drawable.ic_settings));
    }

}
