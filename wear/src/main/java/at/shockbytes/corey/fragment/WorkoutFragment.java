package at.shockbytes.corey.fragment;


import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.WearExercisePagerAdapter;
import at.shockbytes.corey.common.core.util.view.NonSwipeableViewPager;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.core.CommunicationManager;
import at.shockbytes.corey.core.WearCoreyApp;
import at.shockbytes.corey.core.WorkoutActivity;
import at.shockbytes.corey.util.MediaButtonHandler;
import at.shockbytes.corey.workout.PulseLogger;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.SENSOR_SERVICE;

public class WorkoutFragment extends Fragment implements SensorEventListener, WorkoutActivity.OnWorkoutNavigationListener {

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

    @Inject
    protected Vibrator vibrator;

    @Inject
    protected CommunicationManager communicationManager;

    private BottomSheetBehavior bottomSheetBehavior;

    private Workout workout;

    private PulseLogger pulseLogger;

    private SensorManager sensorManager;
    private Sensor sensor;
    private final int SENSOR_REQUEST_CODE = 0x4103;

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

    @Override
    public void onPause() {
        super.onPause();

        if (sensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SENSOR_REQUEST_CODE && permissions[0].equals(Manifest.permission.BODY_SENSORS) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeHeartRate();
        }
    }

    @Override
    public void moveToNext() {

        int item = viewPager.getCurrentItem() + 1;
        boolean isLast = item >= (workout.getExerciseCount());
        if (!isLast) {
            vibrator.vibrate(150);
            viewPager.setCurrentItem(item, true);
            progressBar.setProgress(item + 1);
        } else {
            vibrator.vibrate(new long[] {0, 300,150, 300}, -1);
            stopWorkout();
        }
    }

    @Override
    public void moveToPrevious() {

        int item = viewPager.getCurrentItem() - 1;
        boolean isFirst = item < 0;
        if (!isFirst) {
            vibrator.vibrate(150);
            viewPager.setCurrentItem(item, true);
            progressBar.setProgress(item + 1);
        } else {
            vibrator.vibrate(new long[] {0, 200,100, 200}, -1);
        }
    }

    @Override
    public void onEnterAmbient() {

    }

    @Override
    public void onUpdateAmbient() {

    }

    @Override
    public void onExitAmbient() {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int pulse = (int) event.values[0];
        pulseLogger.logPulse(pulse);
        String text = (pulse > 0) ? String.valueOf(pulse) : "---";
        txtPulse.setText(text);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
                ? R.drawable.ic_music_play
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
                : R.drawable.ic_music_play;
        imgbtnMediaPlayPause.setImageResource(icon);

        progressBar.setProgressTintList(ColorStateList
                .valueOf(ContextCompat.getColor(getContext(), workout.getColorResForIntensity())));
    }

    private void setupWorkout() {

        pulseLogger = new PulseLogger();
        initializeHeartRate();

        viewPager.setAdapter(new WearExercisePagerAdapter(getFragmentManager(), workout));
        viewPager.setPageMargin(32);
        viewPager.makeFancyPageTransformation();

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

        progressBar.setMax(workout.getExerciseCount());
        progressBar.setProgress(1);
    }

    private void stopWorkout() {

        chronometer.stop();

        long elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.getBase()) / 60000;
        int avgPulse = pulseLogger.getAveragePulse(true);

        communicationManager.synchronizeWorkoutInformation(avgPulse, 1,
                (int) (Math.ceil(elapsedSeconds/60)));

        getActivity().finishAfterTransition();
    }

    private void initializeHeartRate() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BODY_SENSORS)
                == PackageManager.PERMISSION_GRANTED) {

            sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
            if (sensor != null) {
                int interval = 1000000;
                sensorManager.registerListener(this, sensor, interval);
            }
        } else {
            requestPermissions(new String[] {Manifest.permission.BODY_SENSORS}
                    , SENSOR_REQUEST_CODE);
        }
    }

}
