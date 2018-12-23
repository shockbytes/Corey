package at.shockbytes.corey.ui.fragment.workoutpager


import android.content.SharedPreferences
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.dagger.WearAppComponent
import at.shockbytes.corey.ui.fragment.WearableBaseFragment
import at.shockbytes.corey.ui.fragment.dialog.WearTimeExerciseCountdownDialogFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Suppress("DEPRECATION")
class WearTimeExercisePagerFragment : WearableBaseFragment() {

    @Inject
    protected lateinit var vibrator: Vibrator

    @Inject
    protected lateinit var preferences: SharedPreferences

    private lateinit var exercise: TimeExercise
    private lateinit var timerObservable: Observable<Long>

    private var isVibrationEnabled: Boolean = false
    private var secondsUntilFinish: Int = 0
    private var timerDisposable: Disposable? = null

    private val txtExercise: TextView by bindView(R.id.fragment_wear_pageritem_time_exercise_txt_exercise)
    private val btnTime: TextView by bindView(R.id.fragment_wear_pageritem_time_exercise_btn_time)

    override val layoutId = R.layout.fragment_wear_pageritem_time_exercise

    override fun setupViews() {

        btnTime.isEnabled = true
        btnTime.setOnClickListener {
            onClickButtonStart()
            btnTime.isEnabled = false // Avoid multiple clicks
        }

        secondsUntilFinish = exercise.workoutDurationInSeconds

        btnTime.text = calculateDisplayString(secondsUntilFinish)
        context?.let {
            txtExercise.text = exercise.getDisplayName(it)
        }
        txtExercise.isSelected = true

        timerObservable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun injectToGraph(appComponent: WearAppComponent) {
        appComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exercise = arguments?.getParcelable(ARG_EXERCISE) ?: return
        isVibrationEnabled = preferences.getBoolean(getString(R.string.wear_pref_vibration_key), true)
    }

    private fun onClickButtonStart() {

        val countdown = preferences.getString(getString(R.string.wear_pref_countdown_key),
                getString(R.string.wear_pref_countdown_default_value)).toIntOrNull() ?: 0

        WearTimeExerciseCountdownDialogFragment.newInstance(countdown)
                .setCountdownCompleteListener {
                    startExerciseTimer()
                }
                .show(fragmentManager, "countdown-start")
    }

    override fun onDestroy() {
        super.onDestroy()
        timerDisposable?.dispose()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!isVisibleToUser) {
            timerDisposable?.dispose()
        }
    }

    private fun startExerciseTimer() {

        var seconds: Long = 0
        timerDisposable = timerObservable.subscribe({

            val toGo = secondsUntilFinish - seconds
            displayTime(toGo)
            seconds++

            if (toGo <= 0) {
                timerDisposable?.dispose()
            }
        }, { throwable -> Log.wtf("Corey", throwable.toString()) })
    }

    private fun calculateDisplayString(s: Int): String {
        var seconds = s
        var mins = 0
        while (seconds >= 60) {
            mins++
            seconds -= 60
        }
        return mins.toString() + ":" + if (seconds >= 10) seconds else "0$seconds"
    }

    private fun vibrate(secondsToGo: Long) {

        if (isVibrationEnabled) {
            val vibrationIntensity = when {
                secondsToGo == 0L -> 800L
                secondsToGo % (exercise.restDuration + exercise.workDuration) == 0L -> 500L // Full round
                secondsToGo % exercise.workDuration == 0L -> 300L // Work period done
                else -> -1
            }
            if (vibrationIntensity > 0) {
                vibrator.vibrate(vibrationIntensity)
            }
        }
    }

    private fun displayTime(secondsToGo: Long) {

        //Calculate displayable string
        var seconds = secondsToGo
        var minutes: Long = 0
        while (seconds >= 60) {
            minutes++
            seconds -= 60
        }
        vibrate(secondsToGo)
        val formattedSecs: String = if (seconds >= 10) seconds.toString() else "0$seconds"
        btnTime.text = "$minutes:$formattedSecs"
    }

    companion object {

        private const val ARG_EXERCISE = "arg_exercise"

        fun newInstance(exercise: TimeExercise): WearTimeExercisePagerFragment {
            val fragment = WearTimeExercisePagerFragment()
            val args = Bundle()
            args.putParcelable(ARG_EXERCISE, exercise)
            fragment.arguments = args
            return fragment
        }
    }

}
