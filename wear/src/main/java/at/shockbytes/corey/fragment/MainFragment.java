package at.shockbytes.corey.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wearable.view.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.core.WearCoreyApp;
import at.shockbytes.corey.workout.WearableWorkoutManager;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment {

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    protected WearableWorkoutManager workoutManager;

    @Bind(R.id.fragment_main_rv)
    protected WearableRecyclerView recyclerView;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((WearCoreyApp) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {

        // TODO setup adapter

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setCircularScrollingGestureEnabled(true);
    }
}
