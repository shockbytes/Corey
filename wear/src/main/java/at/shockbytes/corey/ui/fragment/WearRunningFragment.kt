package at.shockbytes.corey.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.dagger.WearAppComponent
import at.shockbytes.corey.ui.viewmodel.WearRunningViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_running.*
import timber.log.Timber
import javax.inject.Inject

class WearRunningFragment : WearableBaseFragment(), SensorEventListener {

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: WearRunningViewModel

    override val layoutId = R.layout.fragment_running

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[WearRunningViewModel::class.java]
    }

    override fun setupViews() {

        btn_fragment_running_start.setOnClickListener { view ->
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            viewModel.startRun()
        }
    }

    override fun bindViewModel() {

        viewModel.onStartEvent
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                removeStartButton()
                showRunningViewRootLayout()
                startChronometer()
            }, { throwable ->
                Timber.e(throwable)
            })
            .addTo(compositeDisposable)

        viewModel.getFormattedHeartRate().observe(this, Observer { formattedHeartRate ->
            tv_fragment_running_pulse.text = formattedHeartRate
        })
    }

    private fun startChronometer() {
        chronometer_fragment_running.apply {
            base = SystemClock.elapsedRealtime()
            start()
        }
    }

    private fun showRunningViewRootLayout() {

        layout_fragment_running_run_views
            .animate()
            .withStartAction {
                layout_fragment_running_run_views.apply {
                    visibility = View.VISIBLE
                }
            }
            .alpha(1f)
            .setDuration(500)
            .start()
    }

    private fun removeStartButton() {
        btn_fragment_running_start
            .animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
                btn_fragment_running_start.visibility = View.GONE
            }
            .start()
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

    private fun initializeHeartRate() {

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
            if (sensor != null) {
                val interval = 1000000
                sensorManager?.registerListener(this, sensor, interval)
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), SENSOR_REQUEST_CODE)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val pulse = event.values[0].toInt()
        viewModel.onHeartRateAvailable(pulse)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    override fun injectToGraph(appComponent: WearAppComponent) {
        appComponent.inject(this)
    }

    companion object {

        private const val SENSOR_REQUEST_CODE = 0x4104

        fun newInstance(): WearRunningFragment {
            return WearRunningFragment().apply {
                arguments = Bundle().apply {
                }
            }
        }
    }
}
