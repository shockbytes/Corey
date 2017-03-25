package at.shockbytes.corey.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.ExerciseAdapter;
import at.shockbytes.corey.adapter.WorkoutCraftingSpinnerAdapter;
import at.shockbytes.corey.common.core.adapter.helper.ShockItemTouchHelper;
import at.shockbytes.corey.common.core.util.view.ViewManager;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.fragment.dialogs.AddExercisesDialogFragment;
import at.shockbytes.corey.util.AppParams;
import at.shockbytes.corey.util.AppResourceManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressWarnings("ConstantConditions")
public class CreateWorkoutFragment extends Fragment implements AdapterView.OnItemSelectedListener,
        AddExercisesDialogFragment.OnExerciseCreatedListener {

    private enum CardState {EXPANDED, COLLAPSED}

    public interface OnChangeSystemBarsColorListener {

        void onChangeSystemBarsColor(int from, int fromDark, int to, int toDark);
    }

    private static final String ARG_WORKOUT = "arg_workout_edit";

    public static CreateWorkoutFragment newInstance(Workout workout) {

        CreateWorkoutFragment fragment = new CreateWorkoutFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_WORKOUT, workout);
        fragment.setArguments(args);
        return fragment;
    }

    private Workout workout;
    private boolean isUpdateMode;

    private CardState cardState;
    private OnChangeSystemBarsColorListener listener;

    private int[] oldColors;
    private final int allColors[][] = new int[][] {
            {R.color.workout_intensity_easy, R.color.workout_intensity_easy_dark},
            {R.color.workout_intensity_medium, R.color.workout_intensity_medium_dark},
            {R.color.workout_intensity_hard, R.color.workout_intensity_hard_dark},
            {R.color.workout_intensity_beast, R.color.workout_intensity_beast_dark}
    };


    private ExerciseAdapter exerciseAdapter;

    @Bind(R.id.fragment_create_workout_edit_name)
    protected EditText editName;

    @Bind(R.id.fragment_create_workout_edit_duration)
    protected EditText editDuration;

    @Bind(R.id.fragment_create_workout_spinner_body_region)
    protected Spinner spinnerBodyRegion;

    @Bind(R.id.fragment_create_workout_spinner_intensity)
    protected Spinner spinnerIntensity;

    @Bind(R.id.fragment_create_workout_recyclerview_exercises)
    protected RecyclerView recyclerViewExercises;

    @Bind(R.id.fragment_create_workout_btn_exp_col_general)
    protected ImageButton imgBtnExpandCollapse;

    @Bind(R.id.fragment_create_workout_general_collapse_container)
    protected View generalCollapseContainer;

    public CreateWorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        oldColors = new int[] {R.color.workout_intensity_medium, R.color.workout_intensity_medium_dark};

        cardState = CardState.EXPANDED;
        Workout updated = getArguments().getParcelable(ARG_WORKOUT);
        isUpdateMode = updated != null;
        if (updated != null) {
            workout = updated;
        } else {
            workout = new Workout();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_create_workout, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_create_workout_done) {
            validateInput();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnChangeSystemBarsColorListener) context;
        } catch (IllegalStateException e) {
            Log.e("Corey", "Attached activity must implement listener!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_workout, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if (i > 0 && listener != null) {
            int[] newColors = allColors[i-1].clone();
            listener.onChangeSystemBarsColor(oldColors[0], oldColors[1], newColors[0], newColors[1]);
            oldColors = newColors.clone();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onExerciseCreated(Exercise exercise) {
        //exercise.normalizeNameForJson();
        exerciseAdapter.addEntityAtLast(exercise);
    }

    @OnClick(R.id.fragment_create_workout_btn_exp_col_general)
    protected void onClickExpandCollapse() {

        if (cardState == CardState.EXPANDED) {
            ViewManager.collapse(generalCollapseContainer);
            imgBtnExpandCollapse.animate().rotation(-180).start();
            cardState = CardState.COLLAPSED;
        } else if (cardState == CardState.COLLAPSED) {
            ViewManager.expand(generalCollapseContainer);
            imgBtnExpandCollapse.animate().rotation(0).start();
            cardState = CardState.EXPANDED;
        }
    }

    @OnClick(R.id.fragment_create_workout_btn_add_exercise)
    protected void onClickAddExercise() {

        if (cardState == CardState.EXPANDED) {
            onClickExpandCollapse();
        }

        AddExercisesDialogFragment fragment = AddExercisesDialogFragment.newInstance();
        fragment.setOnExerciseCreatedListener(this);
        fragment.show(getFragmentManager(), fragment.getTag());
    }

    private void fillFields() {

        editName.setText(workout.getDisplayableName());
        editDuration.setText(String.valueOf(workout.getDuration()));
        int brIdx = workout.getBodyRegion().ordinal() + 1;
        spinnerBodyRegion.setSelection(brIdx, true);
        int inIdx = workout.getIntensity().ordinal() + 1;
        spinnerIntensity.setSelection(inIdx, true);
        exerciseAdapter.setData(workout.getExercises());
    }

    private void setupViews() {

        spinnerBodyRegion.setAdapter(new WorkoutCraftingSpinnerAdapter(getContext(),
                AppResourceManager.getBodyRegionSpinnerData(getContext())));
        spinnerIntensity.setAdapter(new WorkoutCraftingSpinnerAdapter(getContext(),
                AppResourceManager.getIntensitySpinnerData(getContext())));
        spinnerIntensity.setOnItemSelectedListener(this);

        recyclerViewExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        exerciseAdapter = new ExerciseAdapter(getContext(), new ArrayList<Exercise>());
        exerciseAdapter.setItemsMovable(true);
        //exerciseAdapter.setOnItemMoveListener(this);
        ItemTouchHelper.Callback callback = new ShockItemTouchHelper(exerciseAdapter, true, false);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerViewExercises);
        recyclerViewExercises.setAdapter(exerciseAdapter);

        if (isUpdateMode) {
            fillFields();
        }
    }

    private void validateInput() {

        // Name -- Not empty
        String name = editName.getText().toString();
        if (name.isEmpty()) {
            Snackbar.make(getView(), R.string.validation_empty_name, Snackbar.LENGTH_LONG).show();
            return;
        }

        // Duration -- Not empty
        String strDuration = editDuration.getText().toString();
        if (strDuration.isEmpty()) {
            Snackbar.make(getView(), R.string.validation_empty_duration, Snackbar.LENGTH_LONG).show();
            return;
        }
        int duration = Integer.parseInt(strDuration);

        // Body Region -- Not first selected
        int brIdx = spinnerBodyRegion.getSelectedItemPosition();
        if (brIdx <= 0) {
            Snackbar.make(getView(), R.string.validation_empty_body_region, Snackbar.LENGTH_LONG).show();
            return;
        }

        // Intensity -- Not first selected
        int inIdx = spinnerIntensity.getSelectedItemPosition();
        if (brIdx <= 0) {
            Snackbar.make(getView(), R.string.validation_empty_intensity, Snackbar.LENGTH_LONG).show();
            return;
        }

        // Workout items -- Not empty
        List<Exercise> exercises = exerciseAdapter.getData();
        if (exercises.size() == 0) {
            Snackbar.make(getView(), R.string.validation_empty_exercises, Snackbar.LENGTH_LONG).show();
            return;
        }

        workout.setName(name)
                .setDuration(duration)
                .setBodyRegion(Workout.BodyRegion.values()[brIdx-1])
                .setIntensity(Workout.Intensity.values()[inIdx-1])
                .setExercises(exercises);

        notifyMainActivity();
    }

    private void notifyMainActivity() {

        Intent data = new Intent()
                .putExtra(AppParams.INTENT_EXTRA_NEW_WORKOUT, workout)
                .putExtra(AppParams.INTENT_EXTRA_WORKOUT_UPDATED, isUpdateMode);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().supportFinishAfterTransition();
    }


}
