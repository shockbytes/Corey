package at.shockbytes.corey.ui.fragment;


import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.ExercisePagerAdapter;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.dagger.AppComponent;
import at.shockbytes.corey.ui.fragment.dialogs.WorkoutMessageDialogFragment;
import at.shockbytes.corey.workout.WorkoutManager;
import at.shockbytes.util.view.NonSwipeableViewPager;
import butterknife.BindView;
import butterknife.OnClick;

public class WorkoutFragment extends BaseFragment {

    private static final String ARG_WORKOUT = "arg_workout";

    public static WorkoutFragment newInstance(Workout workout) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_WORKOUT, workout);
        fragment.setArguments(args);
        return fragment;
    }

    private Workout workout;

    @Inject
    protected WorkoutManager workoutManager;

    @BindView(R.id.fragment_workout_viewpager)
    protected NonSwipeableViewPager viewPager;

    @BindView(R.id.fragment_workout_progressbar)
    protected ProgressBar progressBar;

    @BindView(R.id.fragment_workout_chronometer)
    protected Chronometer chronometer;

    public WorkoutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workout = getArguments().getParcelable(ARG_WORKOUT);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_workout;
    }

    @Override
    protected void injectToGraph(@NotNull AppComponent appComponent) {
        appComponent.inject(this);
    }

    @Override
    public void setupViews() {

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
            finish();
        }
    }

    private void finish() {

        chronometer.stop();

        long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 60000;
        int time = (int) (Math.ceil(elapsedSeconds/60));
        workoutManager.updatePhoneWorkoutInformation(1, time);

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
