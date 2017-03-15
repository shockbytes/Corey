package at.shockbytes.corey.core;

import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DefaultOffsettingHelper;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.WearWorkoutOverviewAdapter;
import at.shockbytes.corey.common.core.adapter.BaseAdapter;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.workout.WearableWorkoutManager;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends WearableActivity implements BaseAdapter.OnItemClickListener<Workout> {

    @Inject
    protected WearableWorkoutManager workoutManager;

    @Bind(R.id.fragment_main_rv)
    protected WearableRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((WearCoreyApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        setAmbientEnabled();

        setupRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        // TODO Maybe do something for ambient mode
    }

    @Override
    public void onItemClick(Workout workout, View v) {

        startActivity(WorkoutActivity.newIntent(getApplicationContext(), workout),
                ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    private void setupRecyclerView() {

        WearWorkoutOverviewAdapter adapter = new WearWorkoutOverviewAdapter(getApplicationContext(), getDummyData());

        recyclerView.setCenterEdgeItems(true);
        recyclerView.setOffsettingHelper(new DefaultOffsettingHelper());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private List<Workout> getDummyData() {

        List<Workout> list = new ArrayList<>();
        list.add(new Workout("Core", 60, Workout.Intensity.HARD, Workout.BodyRegion.CORE).setId("1"));
        list.add(new Workout("Full beast", 120, Workout.Intensity.BEAST, Workout.BodyRegion.FULL_BODY).setId("2"));
        list.add(new Workout("Legday", 30, Workout.Intensity.MEDIUM, Workout.BodyRegion.LEGS).setId("3"));
        list.add(new Workout("Easy cheesy", 15, Workout.Intensity.EASY, Workout.BodyRegion.ARMS).setId("4"));
        list.add(new Workout("Pump", 70, Workout.Intensity.HARD, Workout.BodyRegion.CHEST).setId("5"));
        list.add(new Workout("Core", 60, Workout.Intensity.HARD, Workout.BodyRegion.CORE).setId("6"));
        list.add(new Workout("Full beast", 120, Workout.Intensity.BEAST, Workout.BodyRegion.FULL_BODY).setId("7"));
        list.add(new Workout("Legday", 30, Workout.Intensity.MEDIUM, Workout.BodyRegion.LEGS).setId("8"));
        list.add(new Workout("Easy cheesy", 15, Workout.Intensity.EASY, Workout.BodyRegion.ARMS).setId("9"));
        list.add(new Workout("Pump", 70, Workout.Intensity.HARD, Workout.BodyRegion.CHEST).setId("10"));
        return list;
    }
}
