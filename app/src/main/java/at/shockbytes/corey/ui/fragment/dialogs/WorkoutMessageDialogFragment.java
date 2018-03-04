package at.shockbytes.corey.ui.fragment.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import at.shockbytes.corey.R;

/**
 * @author Martin Macheiner
 *         Date: 26.02.2017.
 */

public class WorkoutMessageDialogFragment extends DialogFragment {

    public enum MessageType {QUIT, DONE}

    public interface OnMessageAgreeClickedListener {

        void onMessageAgreeClicked();
    }

    public static final String ARG_MSG_TYPE = "arg_message_type";

    public static WorkoutMessageDialogFragment newInstance(MessageType type) {
        WorkoutMessageDialogFragment fragment = new WorkoutMessageDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MSG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    private MessageType msgType;

    private OnMessageAgreeClickedListener listener;

    public WorkoutMessageDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msgType = (MessageType) getArguments().getSerializable(ARG_MSG_TYPE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(getTitle())
                .setMessage(getMessage())
                .setCancelable(false)
                .setPositiveButton(getPositiveText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (listener != null) {
                            listener.onMessageAgreeClicked();
                        }
                        dismiss();
                    }
                })
                .setNegativeButton(getNegativeText(), null)
                .create();
    }

    public void setOnMessageAgreeClickedListener(OnMessageAgreeClickedListener listener) {
        this.listener = listener;
    }

    private int getTitle() {
        return (msgType == MessageType.QUIT)
                ? R.string.workout_message_title_quit
                : R.string.workout_message_title_done;
    }

    private int getMessage() {
        return (msgType == MessageType.QUIT)
                ? R.string.workout_message_msg_quit
                : R.string.workout_message_msg_done;
    }

    private int getPositiveText() {
        return (msgType == MessageType.QUIT)
                ? R.string.workout_message_pos_quit
                : R.string.workout_message_pos_done;
    }

    private int getNegativeText() {
        return (msgType == MessageType.QUIT)
                ? R.string.workout_message_neg_quit
                : R.string.workout_message_neg_done;
    }

}
