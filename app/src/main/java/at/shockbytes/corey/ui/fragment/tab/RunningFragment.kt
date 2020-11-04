package at.shockbytes.corey.ui.fragment.tab

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.location.CoreyLocation
import at.shockbytes.corey.common.core.running.Run
import at.shockbytes.corey.common.core.running.RunUpdate
import at.shockbytes.corey.common.core.running.RunViewAnimations
import at.shockbytes.corey.common.core.util.RunUtils
import at.shockbytes.corey.dagger.AppComponent
import at.shockbytes.corey.ui.viewmodel.RunningViewModel
import at.shockbytes.util.AppUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.fragment_running.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import kotlin.math.abs

class RunningFragment : TabBaseFragment<AppComponent>(),
    OnMapReadyCallback,
    GestureDetector.OnDoubleTapListener {

    private enum class HeaderState {
        NORMAL, TIME_UPFRONT, DISTANCE_UPFRONT
    }

    private enum class SwipeDirection {
        LEFT, RIGHT
    }

    private var map: GoogleMap? = null
    private var trackLine: Polyline? = null
    private var isFirstLocation: Boolean = false

    private var headerState: HeaderState = HeaderState.NORMAL

    private lateinit var gestureDetector: GestureDetectorCompat

    override val layoutId = R.layout.fragment_running

    override val castsActionBarShadow: Boolean = false

    override val snackBarBackgroundColorRes: Int = R.color.sb_background
    override val snackBarForegroundColorRes: Int = R.color.sb_background

    @Inject
    lateinit var vmFactory: ViewModelProvider.Factory

    private lateinit var viewModel: RunningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, vmFactory)[RunningViewModel::class.java]
    }

    override fun bindViewModel() {

        viewModel.onRunStartedEvent()
            .subscribe {
                runStarted()
            }
            .addTo(compositeDisposable)

        viewModel.onRunStoppedEvent()
            .subscribe { run ->
                showFinishedRun(run)
            }
            .addTo(compositeDisposable)

        viewModel.onRunUpdate().observe(this, Observer { runUpdate ->
            updateRun(runUpdate)
        })
    }

    override fun injectToGraph(appComponent: AppComponent?) {
        appComponent?.inject(this)
    }

    override fun unbindViewModel() = Unit

    override fun setupViews() {
        clearViews()
        setupHeaderGestureRecognizer()

        fragment_running_btn_start.setOnClickListener {
            viewModel.startRun()
        }
    }

    override fun onResume() {
        super.onResume()
        showMapFragment()

        /* When running in background
        if (runningManager.isRecording) {
            setResetRunningViews(true)
            chronometer.base = runningManager.r.startTime
            chronometer.start()
        }
        */
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        setupMapAndLocationServices()
    }

    override fun onSingleTapConfirmed(motionEvent: MotionEvent): Boolean {
        return true
    }

    override fun onDoubleTap(motionEvent: MotionEvent): Boolean {
        stopRun()
        return true
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent): Boolean {
        return true
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQ_CODE_PERM_LOCATION)
    private fun setupMapAndLocationServices() {

        if (EasyPermissions.hasPermissions(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            map?.isMyLocationEnabled = true
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.perm_location_rationale),
                REQ_CODE_PERM_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // -------------------------- Setup --------------------------

    private fun setupHeaderGestureRecognizer() {

        gestureDetector = GestureDetectorCompat(context,
            object : GestureDetector.OnGestureListener {

                override fun onDown(motionEvent: MotionEvent): Boolean {
                    return true
                }

                override fun onShowPress(motionEvent: MotionEvent) {}

                override fun onSingleTapUp(motionEvent: MotionEvent): Boolean {
                    return true
                }

                override fun onScroll(motionEvent: MotionEvent, motionEvent1: MotionEvent, v: Float, v1: Float): Boolean {
                    return false
                }

                override fun onLongPress(motionEvent: MotionEvent) {
                    showHelpHeader()
                }

                override fun onFling(downEvent: MotionEvent, upEvent: MotionEvent, vx: Float, vy: Float): Boolean {

                    val directionX = downEvent.x - upEvent.x
                    if (abs(directionX) > 200) {
                        val direction = if (directionX > 0)
                            SwipeDirection.LEFT
                        else
                            SwipeDirection.RIGHT
                        animateTimeDistanceHeader(direction)
                    }
                    return true
                }
            })
        gestureDetector.setOnDoubleTapListener(this)

        fragment_running_header.setOnTouchListener { _, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
        }
    }

    private fun showMapFragment() {
        isFirstLocation = true

        val mapFragment = SupportMapFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragment_running_map_container, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)
    }

    private fun showHelpHeader() {

        fragment_running_stop_help_view.animate()
            .alpha(1f)
            .setStartDelay(0)
            .setDuration(400)
            .withEndAction {
                fragment_running_stop_help_view.animate()
                    .alpha(0f)
                    .setStartDelay(2000L)
                    .start()
            }.start()
        fragment_running_data_view.animate()
            .alpha(0.1f)
            .scaleY(0.8f)
            .scaleX(0.8f)
            .setStartDelay(0)
            .setDuration(400)
            .withEndAction {
                fragment_running_data_view.animate()
                    .alpha(1f)
                    .scaleY(1f)
                    .scaleX(1f)
                    .setStartDelay(2000L)
                    .start()
            }.start()

        RunViewAnimations.animateDoubleTap(fragment_running_stop_help_imgview)
    }

    private fun showFinishedRun(run: Run) {
        fragment_running_txt_time.stop()
        setResetRunningViews(false)
        showDetailFragment(run)
    }

    private fun showDetailFragment(run: Run) {
        // Show detail fragment, old implementation wasn't nice anyway
    }

    private fun animateStartingViews(animateOut: Boolean) {

        if (animateOut) {

            fragment_running_btn_start
                .animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    fragment_running_btn_start.visibility = View.GONE
                }
                .start()

            fragment_running_map_background
                .animate()
                .alpha(0f)
                .setDuration(500)
                .withEndAction {
                    fragment_running_btn_start.visibility = View.GONE
                }
                .start()
        } else {

            fragment_running_btn_start
                .animate()
                .alpha(1f)
                .setDuration(500)
                .withStartAction() {
                    fragment_running_btn_start.visibility = View.VISIBLE
                }
                .start()

            fragment_running_map_background
                .animate()
                .alpha(1f)
                .setDuration(500)
                .withStartAction() {
                    fragment_running_btn_start.visibility = View.VISIBLE
                }
                .start()
        }
    }

    private fun animateTimeDistanceHeader(direction: SwipeDirection) {

        val x = (fragment_running_data_view.width / 2 -
            fragment_running_txt_distance.width / 2 -
            fragment_running_txt_time.x.toInt())

        when (headerState) {

            HeaderState.NORMAL ->

                headerState = if (direction == SwipeDirection.LEFT) {
                    fragment_running_txt_distance
                        .animate()
                        .translationX((-x).toFloat())
                        .start()
                    fragment_running_txt_time
                        .animate()
                        .scaleX(0.5f)
                        .scaleY(0.5f)
                        .alpha(0.5f)
                        .start()

                    HeaderState.DISTANCE_UPFRONT
                } else {
                    fragment_running_txt_time
                        .animate()
                        .translationX(x.toFloat())
                        .start()
                    fragment_running_txt_distance
                        .animate()
                        .scaleX(0.5f)
                        .scaleY(0.5f)
                        .alpha(0.5f)
                        .start()

                    HeaderState.TIME_UPFRONT
                }

            HeaderState.TIME_UPFRONT ->

                if (direction == SwipeDirection.LEFT) {
                    fragment_running_txt_time
                        .animate()
                        .translationX(0f)
                        .start()
                    fragment_running_txt_distance
                        .animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .start()

                    headerState = HeaderState.NORMAL
                }

            HeaderState.DISTANCE_UPFRONT ->

                if (direction == SwipeDirection.RIGHT) {
                    fragment_running_txt_distance
                        .animate()
                        .translationX(0f)
                        .start()
                    fragment_running_txt_time
                        .animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .start()

                    headerState = HeaderState.NORMAL
                }
        }
    }

    private fun runStarted() {

        val startMillis = SystemClock.elapsedRealtime()
        fragment_running_txt_time.base = startMillis
        fragment_running_txt_time.start()
        setResetRunningViews(true)
    }

    private fun stopRun() {
        viewModel.stopRun()
    }

    private fun updateRun(runUpdate: RunUpdate) {

        val location = runUpdate.currentLocation
        if (isFirstLocation) {
            isFirstLocation = false
            map?.moveCamera(CameraUpdateFactory
                .newLatLngZoom(LatLng(location.lat, location.lng), 16f))
        } else {
            map?.animateCamera(CameraUpdateFactory
                .newLatLng(LatLng(location.lat, location.lng)))
        }

        updateViews(runUpdate)
        setTrackOnMap(runUpdate.locations)
    }

    @SuppressLint("SetTextI18n")
    private fun updateViews(update: RunUpdate) {

        val timeInMs = SystemClock.elapsedRealtime() - fragment_running_txt_time.base
        val averagePace = RunUtils.calculatePace(timeInMs, update.distance)
        val calories = RunUtils.calculateCaloriesBurned(timeInMs.toDouble(), update.userWeight)
        fragment_running_txt_distance.text = "${AppUtils.roundDouble(update.distance, 2)}km"
        fragment_running_txt_avg_pace.text = "$averagePace\nmin/km"
        fragment_running_txt_current_pace.text = update.currentPace + "\nmin/km"
        fragment_running_txt_calories.text = "$calories\nkcal"
    }

    private fun setTrackOnMap(runPoints: List<CoreyLocation>) {

        if (trackLine == null) {
            val lineOptions = PolylineOptions()
                .width(15f)
                .color(Color.parseColor("#03A9F4"))
            trackLine = map?.addPolyline(lineOptions)
        }
        trackLine?.points = runPoints.map { LatLng(it.lat, it.lng) }
    }

    @SuppressLint("SetTextI18n")
    private fun clearViews() {

        // Clear the text views
        fragment_running_txt_time.text = "00:00"
        fragment_running_txt_distance.text = "0.0 km"
        fragment_running_txt_current_pace.text = "0:00\nmin/km"
        fragment_running_txt_calories.text = "0\nkcal"
        fragment_running_txt_avg_pace.text = "0:00\nmin/km"

        // Clear map
        if (trackLine != null) {
            trackLine?.remove()
            trackLine = null
        }
    }

    private fun setResetRunningViews(isSetup: Boolean) {
        clearViews()
        animateStartingViews(isSetup)
    }

    companion object {

        private const val REQ_CODE_PERM_LOCATION = 0x7916

        fun newInstance(): RunningFragment {
            val fragment = RunningFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}