package at.shockbytes.corey.fragment.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import at.shockbytes.corey.R;

/**
 * @author Martin Macheiner
 *         Date: 08.03.2017.
 */

public class AddGoalDialogFragment extends DialogFragment {

    public interface OnGoalMessagedAddedListener {

        void onGoalMessageAdded(String goalMsg);
    }

    public static AddGoalDialogFragment newInstance() {
        AddGoalDialogFragment fragment = new AddGoalDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText editGoal;

    private OnGoalMessagedAddedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getContext())
                .setTitle(R.string.set_goal)
                .setIcon(R.mipmap.ic_launcher)
                .setView(createView())
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (!editGoal.getText().toString().isEmpty()
                                && listener != null) {
                            listener.onGoalMessageAdded(editGoal.getText().toString());
                            dismiss();
                        }
                    }
                })
                .create();
    }

    public void setOnGoalMessageAddedListener(OnGoalMessagedAddedListener listener) {
        this.listener = listener;
    }

    private View createView() {
        editGoal = (EditText) LayoutInflater.from(getContext())
                .inflate(R.layout.dialogfragment_add_goal, null, false);
        return editGoal;
    }
}
