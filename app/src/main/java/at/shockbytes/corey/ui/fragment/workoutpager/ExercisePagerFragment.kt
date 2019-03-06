package at.shockbytes.corey.ui.fragment.workoutpager

import android.os.Bundle
import android.widget.TextView
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.dagger.AppComponent
import kotterknife.bindView

class ExercisePagerFragment : BaseFragment<AppComponent>() {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    private var exercise: Exercise? = null

    private val text: TextView by bindView(R.id.fragment_pageritem_exercise_txt)

    override val layoutId = R.layout.fragment_pageritem_exercise

    override fun injectToGraph(appComponent: AppComponent?) = Unit

    override fun setupViews() {
        text.text = exercise?.getDisplayName(context!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exercise = arguments?.getParcelable(ARG_EXERCISE)
    }

    override fun bindViewModel() = Unit

    override fun unbindViewModel() = Unit

    companion object {

        private const val ARG_EXERCISE = "arg_exercise"

        fun newInstance(exercise: Exercise): ExercisePagerFragment {
            val fragment = ExercisePagerFragment()
            val args = Bundle()
            args.putParcelable(ARG_EXERCISE, exercise)
            fragment.arguments = args
            return fragment
        }
    }
}
