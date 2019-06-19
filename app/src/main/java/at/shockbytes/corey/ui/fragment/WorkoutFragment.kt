package at.shockbytes.corey.ui.fragment

import android.os.Bundle
import android.os.SystemClock
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.ExercisePagerAdapter
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.dialog.WorkoutMessageDialogFragment
import at.shockbytes.corey.data.workout.WorkoutRepository
import kotlinx.android.synthetic.main.fragment_workout.*
import javax.inject.Inject

class WorkoutFragment : BaseFragment<AppComponent>() {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_background

    @Inject
    lateinit var workoutManager: WorkoutRepository

    private lateinit var workout: Workout

    override val layoutId = R.layout.fragment_workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workout = arguments?.getParcelable(ARG_WORKOUT)!!
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    public override fun setupViews() {

        fragmentManager?.run {
            fragment_workout_viewpager.apply {
                adapter = ExercisePagerAdapter(this@run, workout)
                pageMargin = 32
                makeFancyPageTransformation()
            }
        }

        fragment_workout_chronometer.base = SystemClock.elapsedRealtime()
        fragment_workout_chronometer.start()

        fragment_workout_progressbar.max = workout.exerciseCount
        fragment_workout_progressbar.progress = 1

        fragment_workout_btn_back.setOnClickListener {
            val item = fragment_workout_viewpager.currentItem - 1
            val isFirst = item < 0
            if (!isFirst) {
                fragment_workout_viewpager.setCurrentItem(item, true)
                fragment_workout_progressbar.progress = item + 1
            }
        }

        fragment_workout_btn_next.setOnClickListener {

            val item = fragment_workout_viewpager.currentItem + 1
            val isLast = item >= workout.exerciseCount
            if (!isLast) {
                fragment_workout_viewpager.setCurrentItem(item, true)
                fragment_workout_progressbar.progress = item + 1
            } else {
                finish()
            }
        }
    }

    override fun bindViewModel() = Unit

    override fun unbindViewModel() = Unit

    private fun finish() {

        fragment_workout_chronometer.stop()

        val elapsedSeconds = (SystemClock.elapsedRealtime() - fragment_workout_chronometer.base) / 60000
        val time = Math.ceil((elapsedSeconds / 60).toDouble()).toInt()
        workoutManager.updatePhoneWorkoutInformation(1, time)

        WorkoutMessageDialogFragment
                .newInstance(WorkoutMessageDialogFragment.MessageType.DONE)
                .setOnMessageAgreeClickedListener {
                    activity?.supportFinishAfterTransition()
                }
                .show(childFragmentManager, "dialogfragment-finish-workout")
    }

    companion object {

        private const val ARG_WORKOUT = "arg_workout"

        fun newInstance(workout: Workout): WorkoutFragment {
            val fragment = WorkoutFragment()
            val args = Bundle()
            args.putParcelable(ARG_WORKOUT, workout)
            fragment.arguments = args
            return fragment
        }
    }
}
