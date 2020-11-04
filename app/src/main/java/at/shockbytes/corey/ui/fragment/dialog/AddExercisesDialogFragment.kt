package at.shockbytes.corey.ui.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ViewFlipper
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.AddExerciseAdapter
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.data.workout.WorkoutRepository
import at.shockbytes.util.adapter.BaseAdapter
import com.shawnlin.numberpicker.NumberPicker
import kotterknife.bindView
import javax.inject.Inject

/**
 * Author:  Martin Macheiner
 * Date:    24.02.2017
 */
class AddExercisesDialogFragment : BottomSheetDialogFragment(), TextWatcher, BaseAdapter.OnItemClickListener<Exercise> {

    @Inject
    lateinit var workoutManager: WorkoutRepository

    private lateinit var exerciseAdapter: AddExerciseAdapter

    private val behaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    private var exercise: Exercise = Exercise() // Default initialization
    private var isTimeExercise: Boolean = false

    private var exerciseCreatedListener: ((Exercise) -> Unit)? = null

    private val editTextFilter: EditText by bindView(R.id.fragment_create_workout_bottom_sheet_edit_filter)
    private val rvAddExercises: RecyclerView by bindView(R.id.fragment_create_workout_bottom_sheet_recyclerview)
    private val viewFlipper: ViewFlipper by bindView(R.id.fragment_create_workout_bottom_sheet_viewflipper)
    private val numberPickerRepetitions: NumberPicker by bindView(R.id.fragment_create_workout_bottom_sheet_numberpicker_reps)
    private val numberPickerWorkDuration: NumberPicker by bindView(R.id.fragment_create_workout_bottom_sheet_numberpicker_workduration)
    private val numberPickerRestDuration: NumberPicker by bindView(R.id.fragment_create_workout_bottom_sheet_numberpicker_restduration)
    private val btnReps: Button by bindView(R.id.fragment_create_workout_bottom_sheet_btn_reps)
    private val btnTime: Button by bindView(R.id.fragment_create_workout_bottom_sheet_btn_time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity?.application as CoreyApp).appComponent.inject(this)
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.dialogfragment_add_exercises, null)
        dialog.setContentView(contentView)
        val layoutParams = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(behaviorCallback)
            behavior.isHideable = false
        }
        setupViews()
    }

    override fun onItemClick(t: Exercise, position: Int, v: View) {

        exercise = t
        isTimeExercise = t is TimeExercise
        if (isTimeExercise) {
            numberPickerRepetitions.value = 5
        }

        val buttonText = if (isTimeExercise)
            getString(R.string.add_time_exercise)
        else
            getString(R.string.add_exercise)
        btnReps.text = buttonText

        viewFlipper.showNext()
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        exerciseAdapter.filter(charSequence.toString())
    }

    override fun afterTextChanged(editable: Editable) {}

    fun setOnExerciseCreatedListener(listener: (Exercise) -> Unit): AddExercisesDialogFragment {
        this.exerciseCreatedListener = listener
        return this
    }

    private fun onClickBtnReps() {

        exercise.repetitions = numberPickerRepetitions.value

        if (!isTimeExercise) {
            exerciseCreatedListener?.invoke(exercise)
            dismiss()
        } else {
            viewFlipper.showNext()
        }
    }

    private fun onClickBtnTime() {

        val timeExercise = exercise as TimeExercise
        val workDuration = numberPickerWorkDuration.value * 30
        val restDuration = numberPickerRestDuration.value * 30
        timeExercise.workDuration = workDuration
        timeExercise.restDuration = restDuration
        exerciseCreatedListener?.invoke(timeExercise)
        dismiss()
    }

    private fun setupViews() {

        viewFlipper.setInAnimation(context, R.anim.slide_in_right)
        viewFlipper.setOutAnimation(context, R.anim.slide_out_left)

        rvAddExercises.layoutManager = GridLayoutManager(context, 3)
        exerciseAdapter = AddExerciseAdapter(
                requireContext(),
                listOf(),
                onItemClickListener = this
        ) { item, query -> item.name.contains(query) }
        rvAddExercises.adapter = exerciseAdapter

        workoutManager.exercises
                .subscribe { exercises ->
                    exerciseAdapter.setData(exercises, false)
                }

        btnReps.setOnClickListener { onClickBtnReps() }
        btnTime.setOnClickListener { onClickBtnTime() }

        editTextFilter.addTextChangedListener(this)

        val formatter = NumberPicker.Formatter { value -> (value * 30).toString() }
        numberPickerRestDuration.formatter = formatter
        numberPickerWorkDuration.formatter = formatter
    }

    companion object {

        fun newInstance(): AddExercisesDialogFragment {
            val fragment = AddExercisesDialogFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
