package at.shockbytes.corey.fragment;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.WearExercisePagerAdapter;
import at.shockbytes.corey.common.core.util.view.NonSwipeableViewPager;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.TimeExercise;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.core.WearCoreyApp;
import at.shockbytes.corey.util.MediaButtonHandler;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WorkoutFragment extends Fragment {

    private static final String ARG_WORKOUT = "arg_workout";

    public static WorkoutFragment newInstance(Workout w) {
        WorkoutFragment fragment = new WorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_WORKOUT, w);
        fragment.setArguments(args);
        return fragment;
    }

    @Bind(R.id.fragment_workout_viewpager)
    protected NonSwipeableViewPager viewPager;

    @Bind(R.id.fragment_workout_chronometer)
    protected Chronometer chronometer;

    @Bind(R.id.fragment_workout_txt_pulse)
    protected TextView txtPulse;

    @Bind(R.id.fragment_workout_progress)
    protected ProgressBar progressBar;

    @Bind(R.id.fragment_workout_bottom_sheet)
    protected View bottomSheetView;

    @Bind(R.id.fragment_workout_media_btn_start_pause)
    protected ImageButton imgbtnMediaPlayPause;

    @Inject
    protected MediaButtonHandler mediaButtonHandler;

    private Workout workout;

    private BottomSheetBehavior bottomSheetBehavior;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupWorkout();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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

    @OnClick(R.id.fragment_workout_btn_music)
    protected void onClickMusic() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @OnClick(R.id.fragment_workout_media_btn_previous)
    protected void onClickMediaPrevious() {
        mediaButtonHandler.previous();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.fragment_workout_media_btn_next)
    protected void onClickMediaNext() {
        mediaButtonHandler.next();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @OnClick(R.id.fragment_workout_media_btn_start_pause)
    protected void onClickMediaStartPause() {

        int icon = mediaButtonHandler.isMusicPlayed()
                ? R.drawable.ic_play
                : R.drawable.ic_music_pause;
        imgbtnMediaPlayPause.setImageResource(icon);

        mediaButtonHandler.playPause();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }


    private void setupViews() {

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        int icon = mediaButtonHandler.isMusicPlayed()
                ? R.drawable.ic_music_pause
                : R.drawable.ic_play;
        imgbtnMediaPlayPause.setImageResource(icon);

    }

    private void setupWorkout() {

        // TODO Remove later
        workout.setExercises(Arrays.asList(new Exercise("MARS", 10), new Exercise("Sars", 20), new TimeExercise("schräge Planke", 2, 30,30)));

        viewPager.setAdapter(new WearExercisePagerAdapter(getFragmentManager(), workout));
        viewPager.setPageMargin(32);
        viewPager.makeFancyPageTransformation();

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        progressBar.setMax(workout.getExerciseCount());
        progressBar.setProgress(1);
    }

    private void stopWorkout() {
        Toast.makeText(getContext(), "Finished", Toast.LENGTH_SHORT).show();
        chronometer.stop();
    }

}
