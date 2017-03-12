package at.shockbytes.corey.fragment.workoutpager;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import at.shockbytes.corey.R;
import at.shockbytes.corey.workout.model.Exercise;
import butterknife.Bind;
import butterknife.ButterKnife;


public class ExercisePagerFragment extends Fragment {

    private static final String ARG_EXERCISE = "arg_exercise";

    private Exercise exercise;

    @Bind(R.id.fragment_pageritem_exercise_txt)
    protected TextView text;

    public ExercisePagerFragment() {
    }

    public static ExercisePagerFragment newInstance(Exercise exercise) {
        ExercisePagerFragment fragment = new ExercisePagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EXERCISE, exercise);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exercise = getArguments().getParcelable(ARG_EXERCISE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pageritem_exercise, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text.setText(exercise.getDisplayName(getContext()));
    }
}
