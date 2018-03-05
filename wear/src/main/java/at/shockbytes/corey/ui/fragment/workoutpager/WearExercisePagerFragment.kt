package at.shockbytes.corey.ui.fragment.workoutpager


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder


class WearExercisePagerFragment : Fragment() {

    private lateinit var exercise: Exercise

    @BindView(R.id.fragment_wear_pageritem_exercise_txt)
    protected lateinit var text: TextView

    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exercise = arguments.getParcelable(ARG_EXERCISE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_wear_pageritem_exercise, container, false)
        unbinder = ButterKnife.bind(this, v)
        return v
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder?.unbind()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text.text = exercise.getDisplayName(context)
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
