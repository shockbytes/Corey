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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.AddExerciseAdapter;
import at.shockbytes.corey.core.CoreyApp;
import at.shockbytes.corey.workout.WorkoutManager;
import at.shockbytes.corey.workout.model.Exercise;
import at.shockbytes.corey.workout.model.TimeExercise;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * @author Martin Macheiner
 *         Date: 24.02.2017.
 */

public class AddExercisesDialogFragment extends BottomSheetDialogFragment
        implements AddExerciseAdapter.OnItemClickListener, TextWatcher {

    public interface OnExerciseCreatedListener {

        void onExerciseCreated(Exercise exercise);

    }

    public static AddExercisesDialogFragment newInstance() {
        AddExercisesDialogFragment fragment = new AddExercisesDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Exercise exercise;

    private boolean isTimeExercise;

    private AddExerciseAdapter addExerciseAdapter;
    private OnExerciseCreatedListener onExerciseCreatedListener;

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

    @Inject
    protected WorkoutManager workoutManager;

    @Bind(R.id.fragment_create_workout_bottom_sheet_edit_filter)
    protected EditText editTextFilter;

    @Bind(R.id.fragment_create_workout_bottom_sheet_recyclerview)
    protected RecyclerView rvAddExercises;

    @Bind(R.id.fragment_create_workout_bottom_sheet_viewflipper)
    protected ViewFlipper viewFlipper;

    @Bind(R.id.fragment_create_workout_bottom_sheet_btn_reps)
    protected Button btnReps;

    @Bind(R.id.fragment_create_workout_bottom_sheet_numberpicker_reps)
    protected NumberPicker numberPickerRepetitions;

    @Bind(R.id.fragment_create_workout_bottom_sheet_numberpicker_workduration)
    protected NumberPicker numberPickerWorkDuration;

    @Bind(R.id.fragment_create_workout_bottom_sheet_numberpicker_restduration)
    protected NumberPicker numberPickerRestDuration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CoreyApp) getActivity().getApplication()).getAppComponent().inject(this);
        isTimeExercise = false;
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
            ((BottomSheetBehavior) behavior).setHideable(false);
        }
        setupViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(Exercise t, View v) {

        exercise = t;
        isTimeExercise = (t instanceof TimeExercise);
        if (isTimeExercise) {
            numberPickerRepetitions.setValue(5);
        }

        String buttonText = isTimeExercise
                ? getString(R.string.add_time_exercise)
                :getString(R.string.add_exercise);
        btnReps.setText(buttonText);

        viewFlipper.showNext();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        addExerciseAdapter.filter(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    public void setOnExerciseCreatedListener(OnExerciseCreatedListener onExerciseCreatedListener) {
        this.onExerciseCreatedListener = onExerciseCreatedListener;
    }

    @OnClick(R.id.fragment_create_workout_bottom_sheet_btn_reps)
    protected void onClickBtnReps() {

        exercise.setRepetitions(numberPickerRepetitions.getValue());

        if (!isTimeExercise) {
            if (onExerciseCreatedListener != null) {
                onExerciseCreatedListener.onExerciseCreated(exercise);
                dismiss();
            }
        } else {
            viewFlipper.showNext();
        }
    }

    @OnClick(R.id.fragment_create_workout_bottom_sheet_btn_time)
    protected void onClickBtnTime() {

        TimeExercise timeExercise = (TimeExercise) exercise;
        int workDuration = numberPickerWorkDuration.getValue() * 30;
        int restDuration = numberPickerRestDuration.getValue() * 30;
        timeExercise.setWorkDuration(workDuration);
        timeExercise.setRestDuration(restDuration);
        if (onExerciseCreatedListener != null) {
            onExerciseCreatedListener.onExerciseCreated(timeExercise);
            dismiss();
        }
    }

    private void setupViews() {

        viewFlipper.setInAnimation(getContext(), R.anim.slide_in_right);
        viewFlipper.setOutAnimation(getContext(), R.anim.slide_out_left);

        rvAddExercises.setLayoutManager(new GridLayoutManager(getContext(), 3));
        addExerciseAdapter = new AddExerciseAdapter(getContext(), new ArrayList<Exercise>());
        addExerciseAdapter.setOnItemClickListener(this);
        rvAddExercises.setAdapter(addExerciseAdapter);

        workoutManager.getExercises().subscribe(new Action1<List<Exercise>>() {
            @Override
            public void call(List<Exercise> exercises) {
                addExerciseAdapter.setData(exercises, false);
            }
        });

        editTextFilter.addTextChangedListener(this);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value*30);
            }
        };
        numberPickerRestDuration.setFormatter(formatter);
        numberPickerWorkDuration.setFormatter(formatter);

    }

}
