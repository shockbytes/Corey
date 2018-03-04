package at.shockbytes.corey.ui.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.WorkoutAdapter;
import at.shockbytes.corey.common.core.util.WorkoutNameComparator;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.dagger.AppComponent;
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener;
import at.shockbytes.corey.ui.activity.CreateWorkoutActivity;
import at.shockbytes.corey.ui.activity.WorkoutDetailActivity;
import at.shockbytes.corey.util.AppParams;
import at.shockbytes.corey.workout.WorkoutManager;
import at.shockbytes.util.adapter.BaseAdapter;
import at.shockbytes.util.view.RecyclerViewWithEmptyView;
import butterknife.BindView;
import io.reactivex.functions.Consumer;

/**
 * @author Martin Macheiner
 *         Date: 26.10.2015.
 */
public class WorkoutOverviewFragment extends BaseFragment
        implements BaseAdapter.OnItemClickListener<Workout>,
        WorkoutAdapter.OnWorkoutPopupItemSelectedListener, LiveWorkoutUpdateListener {

    public static WorkoutOverviewFragment newInstance() {
        WorkoutOverviewFragment fragment = new WorkoutOverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private WorkoutAdapter adapter;

    @Inject
    protected WorkoutManager workoutManager;

    @BindView(R.id.fragment_training_rv)
    protected RecyclerViewWithEmptyView recyclerView;

    //@Bind(R.id.fragment_training_empty_view)
    //protected View emptyView;

    @Override
    public void onDestroy() {
        workoutManager.unregisterLiveForWorkoutUpdates();
        super.onDestroy();
    }

    @Override
    public void onItemClick(Workout w, View v) {

        Intent intent = WorkoutDetailActivity.newIntent(getContext(), w);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                getActivity(),
                new Pair<>(v.findViewById(R.id.item_training_imgview_body_region),
                        getString(R.string.transition_workout_body_region)),
                new Pair<>(v.findViewById(R.id.item_training_container_duration),
                        getString(R.string.transition_workout_duration)),
                new Pair<>(v.findViewById(R.id.item_training_cardview),
                        getString(R.string.transition_workout_card)),
                new Pair<>(v.findViewById(R.id.item_training_txt_title),
                        getString(R.string.transition_workout_name)),
                new Pair<>(v.findViewById(R.id.item_training_container_exercises),
                        getString(R.string.transition_workout_exercise_count))
        );
        getActivity().startActivity(intent, options.toBundle());
    }

    @Override
    public void onDelete(Workout w) {
        adapter.deleteEntity(w);
        workoutManager.deleteWorkout(w);
    }

    @Override
    public void onEdit(Workout w) {
        Intent intent = CreateWorkoutActivity.newIntent(getContext(), w);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity());
        getActivity().startActivityForResult(intent, AppParams.REQUEST_CODE_CREATE_WORKOUT, options.toBundle());
    }

    private RecyclerView.LayoutManager getLayoutManagerForOrientation() {

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        } else {
            return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }
    }

    @Override
    protected void setupViews() {

        recyclerView.setLayoutManager(getLayoutManagerForOrientation());
        adapter = new WorkoutAdapter(getActivity(), new ArrayList<Workout>(), this);
        adapter.setOnItemClickListener(this);
        // recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(adapter);

        workoutManager.getWorkouts().subscribe(new Consumer<List<Workout>>() {
            @Override
            public void accept(List<Workout> workouts) {
                Collections.sort(workouts, new WorkoutNameComparator());
                adapter.setData(workouts);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Snackbar.make(getView(), R.string.snackbar_cannot_load_workouts, Snackbar.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });

        workoutManager.registerLiveForWorkoutUpdates(this);
    }

    @Override
    public void onWorkoutAdded(Workout workout) {
        adapter.addEntityAtLast(workout);
    }

    @Override
    public void onWorkoutDeleted(Workout workout) {
        adapter.deleteEntity(workout);
    }

    @Override
    public void onWorkoutChanged(Workout workout) {
        adapter.updateEntity(workout);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_workout_overview;
    }

    @Override
    protected void injectToGraph(@NotNull AppComponent appComponent) {
        appComponent.inject(this);
    }
}
