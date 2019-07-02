package at.shockbytes.corey.ui.fragment.workoutpager

import android.os.Bundle
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.dagger.WearAppComponent
import at.shockbytes.corey.ui.fragment.WearableBaseFragment
import kotterknife.bindView

class WearExercisePagerFragment : WearableBaseFragment() {

    private lateinit var exercise: Exercise

    private val text: TextView by bindView(R.id.fragment_wear_pageritem_exercise_txt)

    override val layoutId = R.layout.fragment_wear_pageritem_exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exercise = arguments?.getParcelable(ARG_EXERCISE) ?: return
    }

    override fun setupViews() {
        context?.let { ctx ->
            text.text = exercise.getDisplayName(ctx)
        }
    }

    override fun injectToGraph(appComponent: WearAppComponent) {
        // Do nothing...
    }

    override fun bindViewModel() {
    }

    companion object {

        private const val ARG_EXERCISE = "arg_exercise"

        fun newInstance(exercise: Exercise): WearExercisePagerFragment {
            val fragment = WearExercisePagerFragment()
            val args = Bundle()
            args.putParcelable(ARG_EXERCISE, exercise)
            fragment.arguments = args
            return fragment
        }
    }
}
