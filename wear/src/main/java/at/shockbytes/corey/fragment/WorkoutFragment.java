package at.shockbytes.corey.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.core.WearCoreyApp;
import butterknife.ButterKnife;

public class WorkoutFragment extends Fragment {

    private static final String ARG_WORKOUT = "arg_workout";

    public static WorkoutFragment newInstance(Workout w) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_WORKOUT, w);
        fragment.setArguments(args);
        return fragment;
    }

    private Workout workout;

    public WorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((WearCoreyApp) getActivity().getApplication()).getAppComponent().inject(this);
        workout = getArguments().getParcelable(ARG_WORKOUT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_workout, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
