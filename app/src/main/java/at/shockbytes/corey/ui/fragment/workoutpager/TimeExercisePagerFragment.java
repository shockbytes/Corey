package at.shockbytes.corey.ui.fragment.workoutpager;


import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.workout.model.TimeExercise;
import at.shockbytes.corey.ui.fragment.dialogs.TimeExerciseCountdownDialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TimeExercisePagerFragment extends Fragment {

    private static final String ARG_EXERCISE = "arg_exercise";

    private final int TIMER_MILLIS = 20;
    private final int TICKS_FOR_SECOND = 50;

    private TimeExercise exercise;

    private Vibrator vibrator;
    private boolean isVibrationEnabled;

    private int secondsUntilFinish;
    private Flowable<Long> timerObservable;
    private Disposable timerDisposable;

    @BindView(R.id.fragment_pageritem_time_exercise_txt_exercise)
    protected TextView txtExercise;

    @BindView(R.id.fragment_pageritem_time_exercise_txt_time)
    protected TextView txtTime;

    @BindView(R.id.fragment_pageritem_time_exercise_progressbar)
    protected ProgressBar progressBar;

    public TimeExercisePagerFragment() {
    }

    public static TimeExercisePagerFragment newInstance(TimeExercise exercise) {
        TimeExercisePagerFragment fragment = new TimeExercisePagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_EXERCISE, exercise);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exercise = getArguments().getParcelable(ARG_EXERCISE);
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        isVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean(getString(R.string.prefs_vibrations_key), false);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pageritem_time_exercise, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    @OnClick(R.id.fragment_pageritem_time_exercise_btn_start)
    protected void onClickButtonStart() {

        int countdown = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getInt(getString(R.string.prefs_time_countdown_key), 5);

        TimeExerciseCountdownDialogFragment fragment = TimeExerciseCountdownDialogFragment
                .newInstance(countdown);
        fragment.setCountdownCompleteListener(new TimeExerciseCountdownDialogFragment
                .OnCountDownCompletedListener() {
            @Override
            public void onCountdownCompleted() {

                progressBar.setProgress(0);
                timerDisposable = timerObservable.subscribe(new Consumer<Long>() {

                    long seconds = 0;

                    @Override
                    public void accept(Long aLong) {

                        long toGo = secondsUntilFinish - seconds;
                        progressBar.setProgress(progressBar.getProgress() + 10);

                        // Timer will fire every 10 milliseconds
                        if (aLong % TICKS_FOR_SECOND == 0) {
                            displayTime(toGo);
                            seconds++;
                        }

                        if (toGo < 0) {
                            timerDisposable.dispose();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.wtf("Corey", throwable.toString());
                    }
                });
            }
        });
        fragment.show(getFragmentManager(), fragment.getTag());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timerDisposable != null && !timerDisposable.isDisposed()) {
            timerDisposable.dispose();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (timerDisposable != null && !isVisibleToUser) {
            timerDisposable.dispose();
        }
    }

    private void initialize() {

        secondsUntilFinish = exercise.getWorkoutDurationInSeconds();

        progressBar.setMax(secondsUntilFinish * 1000);
        progressBar.setSecondaryProgress(progressBar.getMax());
        txtTime.setText(calculateDisplayString(secondsUntilFinish));
        txtExercise.setText(exercise.getDisplayName(getContext()));

        timerObservable = Flowable.interval(TIMER_MILLIS, TimeUnit.MILLISECONDS)
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private String calculateDisplayString(int seconds) {

        int mins = 0;
        while (seconds >= 60) {
            mins++;
            seconds -= 60;
        }
        return mins + ":" + ((seconds >= 10) ? seconds : "0" + seconds);
    }

    private void vibrate(long secondsToGo) {

        if (isVibrationEnabled) {
            int vibrationIntensity = 0;
            if (secondsToGo == 0) {
                vibrationIntensity = 800;
            } else if (secondsToGo % (exercise.getRestDuration() + exercise.getWorkDuration()) == 0) {
                // Full round
                vibrationIntensity = 300;
            } else if (secondsToGo % exercise.getWorkDuration() == 0) {
                // Work done
                vibrationIntensity = 150;
            }
            vibrator.vibrate(vibrationIntensity);
        }
    }

    private void displayTime(long secondsToGo) {

        //Calculate displayable string
        long seconds = secondsToGo;
        long minutes = 0;
        while (seconds >= 60) {
            minutes++;
            seconds -= 60;
        }

        vibrate(secondsToGo);
        txtTime.setText(minutes + ":" + ((seconds >= 10) ? seconds : "0" + seconds));
    }

}
