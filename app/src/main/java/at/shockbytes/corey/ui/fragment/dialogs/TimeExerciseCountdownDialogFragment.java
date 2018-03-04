package at.shockbytes.corey.ui.fragment.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import at.shockbytes.corey.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Martin Macheiner
 *         Date: 21.03.2015.
 */
public class TimeExerciseCountdownDialogFragment extends DialogFragment {

    private TextView txtTimer;

    public interface OnCountDownCompletedListener {
        void onCountdownCompleted();
    }

    private static final String ARG_SECONDS = "arg_seconds";

    public static TimeExerciseCountdownDialogFragment newInstance(int seconds) {

        TimeExerciseCountdownDialogFragment fragment = new TimeExerciseCountdownDialogFragment();
        Bundle args = new Bundle(1);
        args.putInt(ARG_SECONDS, seconds);
        fragment.setArguments(args);
        return fragment;
    }

    private int countdown;
    private OnCountDownCompletedListener listener;

    private Disposable disposable = null;

    public TimeExerciseCountdownDialogFragment() {
    }

    public void setCountdownCompleteListener(OnCountDownCompletedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        setStyle(STYLE_NO_TITLE, 0);
        countdown = getArguments().getInt(ARG_SECONDS);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialogfragment_countdown, container, false);
        txtTimer = v.findViewById(R.id.dialogfr_countdown_txt_timer);
        txtTimer.setText(String.valueOf(countdown));
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void initialize() {

        disposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) {

                        countdown--;
                        if (countdown == 0) {
                            if (listener != null) {
                                listener.onCountdownCompleted();
                            }
                            disposable.dispose();
                            dismiss();
                        }
                        txtTimer.setText(String.valueOf(countdown));
                    }
                });
    }
}
