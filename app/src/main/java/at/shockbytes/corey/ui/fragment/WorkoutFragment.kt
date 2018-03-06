package at.shockbytes.corey.ui.fragment


import android.os.Bundle
import android.os.SystemClock
import android.widget.Chronometer
import android.widget.ProgressBar
import at.shockbytes.corey.R
import at.shockbytes.corey.adapter.ExercisePagerAdapter
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.dialog.WorkoutMessageDialogFragment
import at.shockbytes.corey.workout.WorkoutManager
import at.shockbytes.util.view.NonSwipeableViewPager
import butterknife.OnClick
import kotterknife.bindView
import javax.inject.Inject

class WorkoutFragment : BaseFragment() {

    @Inject
    protected lateinit var workoutManager: WorkoutManager

    private lateinit var workout: Workout

    private val viewPager: NonSwipeableViewPager by bindView(R.id.fragment_workout_viewpager)
    private val progressBar: ProgressBar by bindView(R.id.fragment_workout_progressbar)
    private val chronometer: Chronometer by bindView(R.id.fragment_workout_chronometer)

    override val layoutId = R.layout.fragment_workout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workout = arguments!!.getParcelable(ARG_WORKOUT)
    }

    override fun injectToGraph(appComponent: AppComponent) {
        appComponent.inject(this)
    }

    public override fun setupViews() {

        viewPager.adapter = ExercisePagerAdapter(fragmentManager, workout)
        viewPager.pageMargin = 32
        viewPager.makeFancyPageTransformation()

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()

        progressBar.max = workout.exerciseCount
        progressBar.progress = 1
    }

    @OnClick(R.id.fragment_workout_btn_back)
    fun onClickBack() {

        val item = viewPager.currentItem - 1
        val isFirst = item < 0
        if (!isFirst) {
            viewPager.setCurrentItem(item, true)
            progressBar.progress = item + 1
        }
    }

    @OnClick(R.id.fragment_workout_btn_next)
    fun onClickNext() {

        val item = viewPager.currentItem + 1
        val isLast = item >= workout.exerciseCount
        if (!isLast) {
            viewPager.setCurrentItem(item, true)
            progressBar.progress = item + 1
        } else {
            finish()
        }
    }

    private fun finish() {

        chronometer.stop()

        val elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.base) / 60000
        val time = Math.ceil((elapsedSeconds / 60).toDouble()).toInt()
        workoutManager.updatePhoneWorkoutInformation(1, time)

        WorkoutMessageDialogFragment.newInstance(WorkoutMessageDialogFragment.MessageType.DONE)
                .setOnMessageAgreeClickedListener {
                    activity?.supportFinishAfterTransition()
                }
                .show(fragmentManager, "dialogfragment-finish-workout")
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
