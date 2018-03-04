package at.shockbytes.corey.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.WearWorkoutOverviewAdapter;
import at.shockbytes.corey.common.core.util.WorkoutNameComparator;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.core.MainActivity;
import at.shockbytes.corey.core.WorkoutActivity;
import at.shockbytes.corey.util.MyOffsettingHelper;
import at.shockbytes.util.adapter.BaseAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkoutOverviewFragment extends Fragment
        implements BaseAdapter.OnItemClickListener<Workout>, MainActivity.OnWorkoutsLoadedListener {

    private static final String ARG_WORKOUTS = "arg_workouts";

    public static WorkoutOverviewFragment newInstance(ArrayList<Workout> workouts) {
        WorkoutOverviewFragment fragment = new WorkoutOverviewFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_WORKOUTS, workouts);
        fragment.setArguments(args);
        return fragment;
    }


    @BindView(R.id.fragment_workout_overview_rv)
    protected WearableRecyclerView recyclerView;

    private List<Workout> workouts;

    private Unbinder unbinder;

    public WorkoutOverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        workouts = getArguments().getParcelableArrayList(ARG_WORKOUTS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_workout_overview, container, false);
        unbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onItemClick(Workout workout, View v) {

        startActivity(WorkoutActivity.newIntent(getContext(), workout),
                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity()).toBundle());
    }


    private void setupRecyclerView() {

        Collections.sort(workouts, new WorkoutNameComparator());
        WearWorkoutOverviewAdapter adapter = new WearWorkoutOverviewAdapter(getContext(), workouts);

        recyclerView.setCenterEdgeItems(true);
        recyclerView.setOffsettingHelper(new MyOffsettingHelper());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onWorkoutLoaded(List<Workout> workouts) {

        this.workouts = new ArrayList<>(workouts);
        setupRecyclerView();
    }
}
