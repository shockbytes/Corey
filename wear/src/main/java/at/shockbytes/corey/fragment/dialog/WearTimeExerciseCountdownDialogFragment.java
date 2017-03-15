package at.shockbytes.corey.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import at.shockbytes.corey.R;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Martin Macheiner
 *         Date: 21.03.2015.
 */
public class WearTimeExerciseCountdownDialogFragment extends DialogFragment {

    private TextView txtTimer;

    public interface OnCountDownCompletedListener {
        void onCountdownCompleted();
    }

    private static final String ARG_SECONDS = "arg_seconds";

    public static WearTimeExerciseCountdownDialogFragment newInstance(int seconds) {

        WearTimeExerciseCountdownDialogFragment fragment = new WearTimeExerciseCountdownDialogFragment();
        Bundle args = new Bundle(1);
        args.putInt(ARG_SECONDS, seconds);
        fragment.setArguments(args);
        return fragment;
    }

    private int countdown;
    private OnCountDownCompletedListener listener;

    private Subscription subscription = null;

    public WearTimeExerciseCountdownDialogFragment() {
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

        View v = inflater.inflate(R.layout.dialogfragment_wear_countdown, container, false);
        txtTimer = (TextView) v.findViewById(R.id.dialogfr_countdown_txt_timer);
        txtTimer.setText(String.valueOf(countdown));
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    private void initialize() {

        subscription = rx.Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {

                        countdown--;
                        if (countdown == 0) {
                            if (listener != null) {
                                listener.onCountdownCompleted();
                            }
                            subscription.unsubscribe();
                            dismiss();
                        }
                        txtTimer.setText(String.valueOf(countdown));
                    }
                });
    }
}
