package at.shockbytes.corey.core;

import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WearableRecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.WearWorkoutOverviewAdapter;
import at.shockbytes.corey.common.core.adapter.BaseAdapter;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.util.MyOffsettingHelper;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends WearableActivity implements BaseAdapter.OnItemClickListener<Workout>,
        CommunicationManager.OnHandheldDataListener {

    @Inject
    protected CommunicationManager communicationManager;

    @Bind(R.id.fragment_main_rv)
    protected WearableRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((WearCoreyApp) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        setAmbientEnabled();

        communicationManager.connectIfDeviceAvailable(this);

        setupRecyclerView(new ArrayList<Workout>());
    }

    @Override
    protected void onStart() {
        super.onStart();
        communicationManager.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        communicationManager.onPause();
    }

    @Override
    public void onItemClick(Workout workout, View v) {

        startActivity(WorkoutActivity.newIntent(getApplicationContext(), workout),
                ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
    }

    private void setupRecyclerView(List<Workout> data) {

        WearWorkoutOverviewAdapter adapter = new WearWorkoutOverviewAdapter(getApplicationContext(), data);

        recyclerView.setCenterEdgeItems(true);
        recyclerView.setOffsettingHelper(new MyOffsettingHelper());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onWorkoutsAvailable(List<Workout> workouts) {
        setupRecyclerView(workouts);
    }

    @Override
    public void onCachedWorkoutsAvailable(List<Workout> workouts) {
        setupRecyclerView(workouts);
    }

}
