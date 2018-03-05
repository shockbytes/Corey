package at.shockbytes.corey.ui.fragment.workoutpager


import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.ui.fragment.dialog.WearTimeExerciseCountdownDialogFragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class WearTimeExercisePagerFragment : Fragment() {

    private lateinit var exercise: TimeExercise
    private lateinit var vibrator: Vibrator
    private lateinit var timerObservable: Observable<Long>

    private var isVibrationEnabled: Boolean = false
    private var secondsUntilFinish: Int = 0
    private var timerDisposable: Disposable? = null

    private val txtExercise: TextView by bindView(R.id.fragment_wear_pageritem_time_exercise_txt_exercise)
    private val btnTime: TextView by bindView(R.id.fragment_wear_pageritem_time_exercise_btn_time)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exercise = arguments.getParcelable(ARG_EXERCISE)
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        isVibrationEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(getString(R.string.wear_pref_vibration_key), true)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wear_pageritem_time_exercise, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun onClickButtonStart() {

        val countdown = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                .getString(getString(R.string.wear_pref_countdown_key), getString(R.string.wear_pref_countdown_default_value)))

        WearTimeExerciseCountdownDialogFragment
                .newInstance(countdown).setCountdownCompleteListener {

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
        timerDisposable?.dispose()
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

    private fun initialize() {

        btnTime.setOnClickListener { onClickButtonStart() }

        secondsUntilFinish = exercise.workoutDurationInSeconds

        btnTime.text = calculateDisplayString(secondsUntilFinish)
        txtExercise.text = exercise.getDisplayName(context)
        txtExercise.isSelected = true

        timerObservable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
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
            var vibrationIntensity = 0
            when {
                secondsToGo == 0L -> vibrationIntensity = 800
                secondsToGo % (exercise.restDuration + exercise.workDuration) == 0L -> vibrationIntensity = 500 // Full round
                secondsToGo % exercise.workDuration == 0L -> vibrationIntensity = 300 // Work period done
            }
            vibrator.vibrate(vibrationIntensity.toLong())
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
