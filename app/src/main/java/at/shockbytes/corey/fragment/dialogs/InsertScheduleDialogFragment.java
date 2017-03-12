package at.shockbytes.corey.fragment.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.AddScheduleItemAdapter;
import at.shockbytes.corey.core.CoreyApp;
import at.shockbytes.corey.util.schedule.ScheduleManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * @author Martin Macheiner
 *         Date: 24.02.2017.
 */

public class InsertScheduleDialogFragment extends BottomSheetDialogFragment
        implements AddScheduleItemAdapter.OnItemClickListener, TextWatcher {

    public interface OnScheduleItemSelectedListener {

        void onScheduleItemSelected(String item, int day);

    }

    private static final String ARG_DAY = "arg_day";

    public static InsertScheduleDialogFragment newInstance(int day) {
        InsertScheduleDialogFragment fragment = new InsertScheduleDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }


    private BottomSheetBehavior.BottomSheetCallback behaviorCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    private AddScheduleItemAdapter addScheduleItemAdapter;
    private OnScheduleItemSelectedListener onScheduleItemSelectedListener;

    private int editDay;

    @Inject
    protected ScheduleManager scheduleManager;

    @Bind(R.id.fragment_create_workout_bottom_sheet_edit_filter)
    protected EditText editTextFilter;

    @Bind(R.id.fragment_create_workout_bottom_sheet_recyclerview)
    protected RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CoreyApp) getActivity().getApplication()).getAppComponent().inject(this);
        editDay = getArguments().getInt(ARG_DAY);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialogfragment_add_exercises, null);
        ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(behaviorCallback);
        }
        setupViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(String item, View v) {

        if (onScheduleItemSelectedListener != null) {
            onScheduleItemSelectedListener.onScheduleItemSelected(item, editDay);
        }
        dismiss();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        addScheduleItemAdapter.filter(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    public void setOnScheduleItemSelectedListener(OnScheduleItemSelectedListener onScheduleItemSelectedListener) {
        this.onScheduleItemSelectedListener = onScheduleItemSelectedListener;
    }

    private void setupViews() {

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        addScheduleItemAdapter = new AddScheduleItemAdapter(getContext(), new ArrayList<String>());
        addScheduleItemAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(addScheduleItemAdapter);

        scheduleManager.getItemsForScheduling().subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> strings) {
                addScheduleItemAdapter.setData(strings, false);
            }
        });

        editTextFilter.addTextChangedListener(this);
    }

}
