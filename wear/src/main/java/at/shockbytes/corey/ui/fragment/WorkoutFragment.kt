package at.shockbytes.corey.ui.fragment

import android.Manifest
import android.content.Context.SENSOR_SERVICE
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.os.Vibrator
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Chronometer
import android.widget.ProgressBar
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.adapter.WearExercisePagerAdapter
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.core.CommunicationManager
import at.shockbytes.corey.dagger.WearAppComponent
import at.shockbytes.corey.ui.activity.WorkoutActivity
import at.shockbytes.corey.data.workout.PulseLogger
import at.shockbytes.util.view.NonSwipeableViewPager
import kotterknife.bindView
import javax.inject.Inject

class WorkoutFragment : WearableBaseFragment(),
        SensorEventListener, WorkoutActivity.OnWorkoutNavigationListener {

    private val viewPager: NonSwipeableViewPager by bindView(R.id.fragment_workout_viewpager)
    private val chronometer: Chronometer by bindView(R.id.fragment_workout_chronometer)
    private val txtPulse: TextView by bindView(R.id.fragment_workout_txt_pulse)
    private val progressBar: ProgressBar by bindView(R.id.fragment_workout_progress)

    @Inject
    protected lateinit var vibrator: Vibrator

    @Inject
    protected lateinit var communicationManager: CommunicationManager

    private lateinit var workout: Workout
    private var pulseLogger: PulseLogger = PulseLogger()

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workout = arguments?.getParcelable(ARG_WORKOUT)!!
    }

    override val layoutId = R.layout.fragment_workout

    override fun injectToGraph(appComponent: WearAppComponent) {
        appComponent.inject(this)
    }

    override fun bindViewModel() {
    }

    override fun setupViews() {
        context?.let {
            progressBar.progressTintList = ColorStateList
                    .valueOf(ContextCompat.getColor(it, workout.colorResForIntensity))
        }
    }

    override fun onStart() {
        super.onStart()
        startWorkout()
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SENSOR_REQUEST_CODE &&
                permissions[0] == Manifest.permission.BODY_SENSORS &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeHeartRate()
        }
    }

    override fun moveToNext() {

        val item = viewPager.currentItem + 1
        val isLast = item >= workout.exerciseCount
        if (!isLast) {
            vibrator.vibrate(150)
            viewPager.setCurrentItem(item, true)
            progressBar.progress = item + 1
        } else {
            vibrator.vibrate(longArrayOf(0, 300, 150, 300), -1)
            stopWorkout()
        }
    }

    override fun moveToPrevious() {

        val item = viewPager.currentItem - 1
        val isFirst = item < 0
        if (!isFirst) {
            vibrator.vibrate(150)
            viewPager.setCurrentItem(item, true)
            progressBar.progress = item + 1
        } else {
            vibrator.vibrate(longArrayOf(0, 200, 100, 200), -1)
        }
    }

    override fun onEnterAmbient() {
    }

    override fun onUpdateAmbient() {
    }

    override fun onExitAmbient() {
    }

    override fun onSensorChanged(event: SensorEvent) {
        val pulse = event.values[0].toInt()
        pulseLogger.logPulse(pulse)
        val text = if (pulse > 0) pulse.toString() else "---"
        txtPulse.text = text
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    private fun startWorkout() {

        initializeHeartRate()

        fragmentManager?.let {
            viewPager.adapter = WearExercisePagerAdapter(it, workout)
            viewPager.pageMargin = 32
            viewPager.makeFancyPageTransformation()
        }

        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()

        progressBar.max = workout.exerciseCount
        progressBar.progress = 1
    }

    private fun stopWorkout() {

        chronometer.stop()

        val elapsedSeconds = (SystemClock.elapsedRealtime() - chronometer.base) / 60000
        val avgPulse = pulseLogger.getAveragePulse(true)

        communicationManager
                .syncWorkoutInformation(avgPulse, Math.ceil((elapsedSeconds / 60).toDouble()).toInt())

        activity?.finishAfterTransition()
    }

    private fun initializeHeartRate() {

        activity?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
                sensorManager = context?.getSystemService(SENSOR_SERVICE) as SensorManager
                sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
                if (sensor != null) {
                    val interval = 1000000
                    sensorManager?.registerListener(this, sensor, interval)
                }
            } else {
                requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), SENSOR_REQUEST_CODE)
            }
        }
    }

    companion object {

        private const val ARG_WORKOUT = "arg_workout"
        private const val SENSOR_REQUEST_CODE = 0x4103

        fun newInstance(w: Workout): WorkoutFragment {
            val fragment = WorkoutFragment()
            val args = Bundle()
            args.putParcelable(ARG_WORKOUT, w)
            fragment.arguments = args
            return fragment
        }
    }
}
