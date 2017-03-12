package at.shockbytes.corey.fragment.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.shawnlin.numberpicker.NumberPicker;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.core.CoreyApp;
import at.shockbytes.corey.body.BodyManager;

/**
 * @author Martin Macheiner
 *         Date: 26.02.2017.
 */

public class DesiredWeightDialogFragment extends DialogFragment {

    public static DesiredWeightDialogFragment newInstance() {
        DesiredWeightDialogFragment fragment = new DesiredWeightDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Inject
    protected BodyManager bodyManager;

    private NumberPicker numberPickerWeight;

    public DesiredWeightDialogFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CoreyApp) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getContext())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.dialogfragment_enter_desiredweight_title)
                .setView(getDialogView())
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int dreamWeight = numberPickerWeight.getValue();
                        bodyManager.setDesiredWeight(dreamWeight);
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.dialogfragment_enter_desiredweight_neg_btn, null)
                .create();
    }

    private View getDialogView() {

        numberPickerWeight = (NumberPicker) LayoutInflater.from(getContext()).inflate(R.layout.dialogfragment_dreamweight, null, false);
        numberPickerWeight.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return value + " " + bodyManager.getWeightUnit();
            }
        });
        return numberPickerWeight;
    }

}
