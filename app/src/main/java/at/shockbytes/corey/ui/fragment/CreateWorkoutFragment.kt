package at.shockbytes.corey.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import at.shockbytes.core.ui.activity.base.TintableBackNavigableActivity
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.ExerciseAdapter
import at.shockbytes.corey.ui.adapter.spinner.WorkoutCraftingSpinnerAdapter
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.dialog.AddExercisesDialogFragment
import at.shockbytes.corey.util.CoreyAppUtils
import at.shockbytes.corey.util.AppParams
import at.shockbytes.util.adapter.BaseItemTouchHelper
import at.shockbytes.util.view.ViewManager
import kotlinx.android.synthetic.main.fragment_create_workout.*
import kotterknife.bindView

class CreateWorkoutFragment : BaseFragment<AppComponent>(), AdapterView.OnItemSelectedListener {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    private enum class CardState {
        EXPANDED, COLLAPSED
    }

    private var workout: Workout = Workout() // Default initialize it to avoid nullability
    private var isUpdateMode: Boolean = false
    private var cardState: CardState = CardState.EXPANDED

    private val allColors = arrayOf(
            intArrayOf(R.color.workout_intensity_easy, R.color.workout_intensity_easy_dark),
            intArrayOf(R.color.workout_intensity_medium, R.color.workout_intensity_medium_dark),
            intArrayOf(R.color.workout_intensity_hard, R.color.workout_intensity_hard_dark),
            intArrayOf(R.color.workout_intensity_beast, R.color.workout_intensity_beast_dark))

    private lateinit var exerciseAdapter: ExerciseAdapter

    private val editName: EditText by bindView(R.id.fragment_create_workout_edit_name)
    private val editDuration: EditText by bindView(R.id.fragment_create_workout_edit_duration)
    private val spinnerBodyRegion: Spinner by bindView(R.id.fragment_create_workout_spinner_body_region)
    private val spinnerIntensity: Spinner by bindView(R.id.fragment_create_workout_spinner_intensity)
    private val recyclerViewExercises: androidx.recyclerview.widget.RecyclerView by bindView(R.id.fragment_create_workout_recyclerview_exercises)
    private val imgBtnExpandCollapse: ImageButton by bindView(R.id.fragment_create_workout_btn_exp_col_general)
    private val generalCollapseContainer: View by bindView(R.id.fragment_create_workout_general_collapse_container)

    override val layoutId = R.layout.fragment_create_workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val updated = arguments?.getParcelable<Workout>(ARG_WORKOUT)
        isUpdateMode = updated != null
        if (updated != null) {
            workout = updated
        }
    }

    override fun injectToGraph(appComponent: AppComponent?) = Unit

    override fun bindViewModel() = Unit

    override fun unbindViewModel() = Unit

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_workout, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_create_workout_done) {
            validateInput()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
        if (i > 0) {
            val newColors = allColors[i - 1].clone()

            (activity as? TintableBackNavigableActivity<*>)
                    ?.tintSystemBarsWithText(
                            actionBarTextColor = Color.WHITE,
                            actionBarColor = ContextCompat.getColor(context!!, newColors[0]),
                            statusBarColor = ContextCompat.getColor(context!!, newColors[1]),
                            title = null,
                            animated = true,
                            useSameColorsForBoth = false
                    )
        }
    }

    override fun onNothingSelected(adapterView: AdapterView<*>) {}

    override fun setupViews() {

        context?.let { ctx ->

            spinnerBodyRegion.adapter = WorkoutCraftingSpinnerAdapter(ctx,
                CoreyAppUtils.getBodyRegionSpinnerData(ctx))
            spinnerIntensity.adapter = WorkoutCraftingSpinnerAdapter(ctx,
                CoreyAppUtils.getIntensitySpinnerData(ctx))
            spinnerIntensity.onItemSelectedListener = this

            recyclerViewExercises.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(ctx)
            exerciseAdapter = ExerciseAdapter(ctx)
            exerciseAdapter.setItemsMovable(true)
            // exerciseAdapter.setOnItemMoveListener(this);
            val callback = BaseItemTouchHelper(exerciseAdapter, true, BaseItemTouchHelper.DragAccess.VERTICAL)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(recyclerViewExercises)
            recyclerViewExercises.adapter = exerciseAdapter
        }

        if (isUpdateMode) {
            fillFields()
        }

        fragment_create_workout_btn_add_exercise.setOnClickListener {
            onClickAddExercise()
        }

        fragment_create_workout_btn_exp_col_general.setOnClickListener {
            onClickExpandCollapse()
        }
    }

    private fun onClickExpandCollapse() {

        if (cardState == CardState.EXPANDED) {
            ViewManager.collapse(generalCollapseContainer)
            imgBtnExpandCollapse.animate().rotation(-180f).start()
            cardState = CardState.COLLAPSED
        } else if (cardState == CardState.COLLAPSED) {
            ViewManager.expand(generalCollapseContainer)
            imgBtnExpandCollapse.animate().rotation(0f).start()
            cardState = CardState.EXPANDED
        }
    }

    private fun onClickAddExercise() {

        if (cardState == CardState.EXPANDED) {
            onClickExpandCollapse()
        }

        AddExercisesDialogFragment.newInstance()
                .setOnExerciseCreatedListener { exercise ->
                    exerciseAdapter.addEntityAtLast(exercise)
                }
                .show(childFragmentManager, "dialog-fragment-add-exercise")
    }

    private fun validateInput() {

        // Name -- Not empty
        val name = editName.text.toString()
        if (name.isEmpty()) {
            showSnackbar(getString(R.string.validation_empty_name))
            return
        }

        // Duration -- Not empty
        val strDuration = editDuration.text.toString()
        if (strDuration.isEmpty()) {
            showSnackbar(getString(R.string.validation_empty_duration))
            return
        }
        val duration = Integer.parseInt(strDuration)

        // Body Region -- Not first selected
        val brIdx = spinnerBodyRegion.selectedItemPosition
        if (brIdx <= 0) {
            showSnackbar(getString(R.string.validation_empty_body_region))
            return
        }

        // Intensity -- Not first selected
        val inIdx = spinnerIntensity.selectedItemPosition
        if (brIdx <= 0) {
            showSnackbar(getString(R.string.validation_empty_intensity))
            return
        }

        // Workout items -- Not empty
        val exercises = exerciseAdapter.data
        if (exercises.size == 0) {
            showSnackbar(getString(R.string.validation_empty_exercises))
            return
        }

        workout.setName(name)
        workout.duration = duration
        workout.bodyRegion = Workout.BodyRegion.values()[brIdx - 1]
        workout.intensity = Workout.Intensity.values()[inIdx - 1]
        workout.exercises = exercises

        back2Main()
    }

    private fun fillFields() {

        editName.setText(workout.displayableName)
        editDuration.setText(workout.duration.toString())
        val brIdx = workout.bodyRegion.ordinal + 1
        spinnerBodyRegion.setSelection(brIdx, true)
        val inIdx = workout.intensity.ordinal + 1
        spinnerIntensity.setSelection(inIdx, true)
        exerciseAdapter.data = workout.exercises
    }

    private fun back2Main() {
        val data = Intent()
                .putExtra(AppParams.INTENT_EXTRA_NEW_WORKOUT, workout)
                .putExtra(AppParams.INTENT_EXTRA_WORKOUT_UPDATED, isUpdateMode)
        activity?.setResult(Activity.RESULT_OK, data)
        activity?.supportFinishAfterTransition()
    }

    companion object {

        private const val ARG_WORKOUT = "arg_workout_edit"

        fun newInstance(workout: Workout?): CreateWorkoutFragment {
            val fragment = CreateWorkoutFragment()
            val args = Bundle()
            args.putParcelable(ARG_WORKOUT, workout)
            fragment.arguments = args
            return fragment
        }
    }
}
