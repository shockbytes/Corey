package at.shockbytes.corey.fragment;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ProgressBar;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.ExercisePagerAdapter;
import at.shockbytes.corey.fragment.dialogs.WorkoutMessageDialogFragment;
import at.shockbytes.corey.util.view.NonSwipeableViewPager;
import at.shockbytes.corey.workout.model.Workout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkoutFragment extends Fragment {

    private static final String ARG_WORKOUT = "arg_workout";

    public static WorkoutFragment newInstance(Workout workout) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_WORKOUT, workout);
        fragment.setArguments(args);
        return fragment;
    }

    private Workout workout;

    @Bind(R.id.fragment_workout_viewpager)
    protected NonSwipeableViewPager viewPager;

    @Bind(R.id.fragment_workout_progressbar)
    protected ProgressBar progressBar;

    @Bind(R.id.fragment_workout_chronometer)
    protected Chronometer chronometer;

    public WorkoutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workout = getArguments().getParcelable(ARG_WORKOUT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workout, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void setupViews() {

        viewPager.setAdapter(new ExercisePagerAdapter(getFragmentManager(), workout));
        viewPager.setPageMargin(32);
        viewPager.makeFancyPageTransformation();

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        progressBar.setMax(workout.getExerciseCount());
        progressBar.setProgress(1);
    }

    @OnClick(R.id.fragment_workout_btn_back)
    protected void onClickBack() {

        int item = viewPager.getCurrentItem() - 1;
        boolean isFirst = item < 0;
        if (!isFirst) {
            viewPager.setCurrentItem(item, true);
            progressBar.setProgress(item + 1);
        }
    }

    @OnClick(R.id.fragment_workout_btn_next)
    protected void onClickNext() {

        int item = viewPager.getCurrentItem() + 1;
        boolean isLast = item >= (workout.getExerciseCount());
        if (!isLast) {
            viewPager.setCurrentItem(item, true);
            progressBar.setProgress(item + 1);
        } else {
            stopWorkout();
        }
    }

    private void stopWorkout() {

        chronometer.stop();

        WorkoutMessageDialogFragment fragment = WorkoutMessageDialogFragment
                .newInstance(WorkoutMessageDialogFragment.MessageType.DONE);
        fragment.setOnMessageAgreeClickedListener(new WorkoutMessageDialogFragment
                .OnMessageAgreeClickedListener() {
            @Override
            public void onMessageAgreeClicked() {
                getActivity().supportFinishAfterTransition();
            }
        });
        fragment.show(getFragmentManager(), fragment.getTag());
    }

}
