package at.shockbytes.corey.ui.fragment.workoutpager

import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import at.shockbytes.core.ui.fragment.BaseFragment
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.fragment.dialog.TimeExerciseCountdownDialogFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_pageritem_time_exercise.*
import kotterknife.bindView
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TimeExercisePagerFragment : BaseFragment<AppComponent>() {

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_foreground

    @Inject
    lateinit var vibrator: Vibrator

    private lateinit var exercise: TimeExercise

    private var isVibrationEnabled: Boolean = false
    private var secondsUntilFinish: Int = 0
    private var timerDisposable: Disposable? = null

    private val txtExercise: TextView by bindView(R.id.fragment_pageritem_time_exercise_txt_exercise)
    private val txtTime: TextView by bindView(R.id.fragment_pageritem_time_exercise_txt_time)
    private val progressBar: ProgressBar by bindView(R.id.fragment_pageritem_time_exercise_progressbar)

    override val layoutId = R.layout.fragment_pageritem_time_exercise

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exercise = arguments?.getParcelable(ARG_EXERCISE)!!
        isVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(getString(R.string.prefs_vibrations_key), false)
    }

    override fun setupViews() {
        secondsUntilFinish = exercise.workoutDurationInSeconds

        progressBar.max = secondsUntilFinish * 1000
        progressBar.secondaryProgress = progressBar.max
        txtTime.text = calculateDisplayString(secondsUntilFinish)
        txtExercise.text = exercise.getDisplayName(context!!)

        fragment_pageritem_time_exercise_btn_start.setOnClickListener {
            onClickButtonStart(it)
        }
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun bindViewModel() = Unit

    override fun unbindViewModel() = Unit

    private fun onClickButtonStart(v: View) {
        v.isEnabled = false

        val countdown = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(getString(R.string.prefs_time_countdown_key), 5)

        TimeExerciseCountdownDialogFragment
                .newInstance(countdown)
                .setCountdownCompleteListener {
                    startTimer()
                }
                .show(childFragmentManager, "dialog-fragment-countdown-time-exercise")
    }

    override fun onDestroy() {
        super.onDestroy()
        timerDisposable?.dispose()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (timerDisposable != null && !isVisibleToUser) {
            timerDisposable?.dispose()
        }
    }

    private fun startTimer() {

        progressBar.progress = 0
        var seconds: Long = 0

        timerDisposable = Observable.interval(TIMER_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ tick ->

                    val toGo = secondsUntilFinish - seconds
                    progressBar.progress = progressBar.progress + 10

                    // Timer will fire every 10 milliseconds
                    if (tick % TICKS_FOR_SECOND == 0L) {
                        displayTime(toGo)
                        seconds++
                    }

                    if (toGo < 0) {
                        timerDisposable?.dispose()
                    }
                }, { throwable ->
                    Timber.e(throwable)
                })
    }

    private fun calculateDisplayString(seconds: Int): String {
        var secs = seconds

        var mins = 0
        while (secs >= 60) {
            mins++
            secs -= 60
        }
        val secsFormatted = secs.toString().padStart(2, '0')
        return "$mins:$secsFormatted"
    }

    private fun vibrate(secondsToGo: Long) {

        if (isVibrationEnabled) {
            val vibrationIntensity = when {
                secondsToGo == 0L -> 800L
                secondsToGo % (exercise.restDuration + exercise.workDuration) == 0L -> 300L // Full round
                secondsToGo % exercise.workDuration == 0L -> 150L // Work done
                else -> -1
            }
            if (vibrationIntensity > 0) {
                vibrator.vibrate(vibrationIntensity)
            }
        }
    }

    private fun displayTime(secondsToGo: Long) {

        // Calculate displayable string
        var seconds = secondsToGo
        var minutes: Long = 0
        while (seconds >= 60) {
            minutes++
            seconds -= 60
        }

        vibrate(secondsToGo)
        val secsFormatted = seconds.toString().padStart(2, '0')
        txtTime.text = "$minutes:$secsFormatted"
    }

    companion object {

        private const val ARG_EXERCISE = "arg_exercise"

        private const val TIMER_MILLIS = 20L
        private const val TICKS_FOR_SECOND = 50L

        fun newInstance(exercise: TimeExercise): TimeExercisePagerFragment {
            val fragment = TimeExercisePagerFragment()
            val args = Bundle()
            args.putParcelable(ARG_EXERCISE, exercise)
            fragment.arguments = args
            return fragment
        }
    }
}
