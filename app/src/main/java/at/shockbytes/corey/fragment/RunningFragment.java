package at.shockbytes.corey.fragment;


import android.Manifest;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.running.Run;
import at.shockbytes.corey.common.core.running.RunningManager;
import at.shockbytes.corey.common.core.running.location.LocationManager;
import at.shockbytes.corey.common.core.util.ResourceManager;
import at.shockbytes.corey.common.core.util.RunUtils;
import at.shockbytes.corey.core.CoreyApp;
import at.shockbytes.corey.core.RunningActivity;
import at.shockbytes.corey.storage.StorageManager;
import at.shockbytes.corey.util.AppParams;
import at.shockbytes.corey.util.ViewManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class RunningFragment extends Fragment
        implements OnMapReadyCallback, LocationManager.OnLocationUpdateListener,
        GestureDetector.OnDoubleTapListener {

    private static final int REQ_CODE_PERM_LOCATION = 0x1245;
    private static final int REQUEST_CHECK_SETTINGS = 0x9874;

    public static RunningFragment newInstance() {
        RunningFragment fragment = new RunningFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private GoogleMap map;
    private Polyline trackLine;
    private boolean isFirstLocation;

    private GestureDetectorCompat gestureDetector;

    @Inject
    protected LocationManager locationManager;

    @Inject
    protected RunningManager runningManager;

    @Inject
    protected StorageManager storageManager;

    @Bind(R.id.fragment_running_header)
    protected View headerView;

    @Bind(R.id.fragment_running_stop_help_view)
    protected View stopHelpView;

    @Bind(R.id.fragment_running_stop_help_imgview)
    protected View stopHelpImageView;

    @Bind(R.id.fragment_running_data_view)
    protected View headerDataView;

    @Bind(R.id.fragment_running_map_background)
    protected View mapBackgroundView;

    @Bind(R.id.fragment_running_btn_start)
    protected Button btnStart;

    @Bind(R.id.fragment_running_txt_time)
    protected Chronometer chronometer;

    @Bind(R.id.fragment_running_txt_distance)
    protected TextView txtDistance;

    @Bind(R.id.fragment_running_txt_current_pace)
    protected TextView txtCurrentPace;

    @Bind(R.id.fragment_running_txt_calories)
    protected TextView txtCalories;

    @Bind(R.id.fragment_running_txt_avg_pace)
    protected TextView txtAvgPace;

    public RunningFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CoreyApp) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_running, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTextViews();
        setupHeaderGestureRecognizer();
    }

    @Override
    public void onResume() {
        super.onResume();
        showMapFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationManager.isLocationUpdateRequested()) {
            locationManager.stop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        setupMapAndLocationServices();
    }

    @SuppressWarnings({"MissingPermission"})
    @AfterPermissionGranted(REQ_CODE_PERM_LOCATION)
    private void setupMapAndLocationServices() {

        if (EasyPermissions.hasPermissions(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            map.setMyLocationEnabled(true);
            locationManager.start(this);
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.perm_location_rationale),
                    REQ_CODE_PERM_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void setupTextViews() {
        chronometer.setText("00:00");
        txtDistance.setText("0.0 km");
        txtCurrentPace.setText("0:00\nmin/km");
        txtCalories.setText("0\nkcal");
        txtAvgPace.setText("0:00\nmin/km");
    }

    private void showMapFragment() {
        isFirstLocation = true;

        SupportMapFragment mapFragment = new SupportMapFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_running_map_container, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
    }

    private void setupHeaderGestureRecognizer() {

        gestureDetector = new GestureDetectorCompat(getContext(),
                new GestureDetector.OnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent motionEvent) {
                        return true;
                    }

                    @Override
                    public void onShowPress(MotionEvent motionEvent) {

                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent motionEvent) {
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                        return false;
                    }

                    @Override
                    public void onLongPress(MotionEvent motionEvent) {
                        showHelpHeader();
                    }

                    @Override
                    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                        return false;
                    }
                });
        gestureDetector.setOnDoubleTapListener(this);

        headerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    private void showHelpHeader() {

        ((RunningActivity) getActivity()).animateToolbar();

        stopHelpView.animate()
                .alpha(1)
                .setStartDelay(0)
                .setDuration(400)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        stopHelpView.animate()
                                .alpha(0)
                                .setStartDelay(AppParams.HELP_SHOW_DELAY)
                                .start();
                    }
                }).start();
        headerDataView.animate()
                .alpha(0.1f)
                .scaleY(0.8f)
                .scaleX(0.8f)
                .setStartDelay(0)
                .setDuration(400)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        headerDataView.animate()
                                .alpha(1)
                                .scaleY(1)
                                .scaleX(1)
                                .setStartDelay(AppParams.HELP_SHOW_DELAY)
                                .start();
                    }
                }).start();

        ViewManager.animateDoubleTap(stopHelpImageView);
    }

    private void updateTrackOnMap(Location loc) {

        if (trackLine == null) {
            PolylineOptions lineOptions = new PolylineOptions()
                    .width(15)
                    .color(Color.parseColor("#03A9F4"));
            trackLine = map.addPolyline(lineOptions);
        }

        LatLng current = new LatLng(loc.getLatitude(), loc.getLongitude());
        List<LatLng> pointsSoFar = trackLine.getPoints();
        pointsSoFar.add(current);
        trackLine.setPoints(pointsSoFar);
    }

    private void updateViews(Run run) {

        long timeInMs = (SystemClock.elapsedRealtime() - chronometer.getBase());
        String averagePace = RunUtils.calculatePace(timeInMs, run.getDistance());
        String currentPace = runningManager.getCurrentPace();
        double weight = 80; // TODO Get latest weight from BodyManager
        int calories = RunUtils.calculateCaloriesBurned(run.getDistance(), weight);
        txtDistance.setText(ResourceManager.roundDoubleWithDigits(run.getDistance(), 2) + " km");
        txtAvgPace.setText(averagePace + "\nmin/km");

        txtCurrentPace.setText(currentPace + "\nmin/km");
        txtCalories.setText(calories + "\nkcal");
    }

    private void clearViews() {

        // Clear the text views
        setupTextViews();
        // Clear map
        if (trackLine != null) {
            trackLine.remove();
            trackLine = null;
        }
    }

    private void animateStartingViews(boolean animateOut) {

        int alpha = animateOut ? 0 : 1;

        // Animate button & transparent view with a fade out ;transition and hide it in the end
        btnStart.animate().alpha(alpha).setDuration(500);
        mapBackgroundView.animate().alpha(alpha).setDuration(500);
    }

    private void startRun() {

        clearViews();

        runningManager.startRunRecording();
        animateStartingViews(true);

        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();

    }

    private void stopRun() {

        animateStartingViews(false);
        clearViews();

        chronometer.stop();
        long elapsedMillis = (SystemClock.elapsedRealtime() - chronometer.getBase());

        runningManager.stopRunRecord(elapsedMillis);

        Run run = runningManager.getFinishedRun();
        // TODO Enable StorageManager to handle runs storageManager.storeRun(run);
    }

    @OnClick(R.id.fragment_running_btn_start)
    protected void onClickButtonStart() {

        if (!runningManager.isRecording()) {
            startRun();
        }
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onError(Exception e) {

        int statusCode = ((ApiException) e).getStatusCode();
        switch (statusCode) {

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                try {

                    ResolvableApiException rae = (ResolvableApiException) e;
                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException sie) {
                    sie.printStackTrace();
                }

                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                Toast.makeText(getContext(), "Wrong location settings, fix in settings!",
                        Toast.LENGTH_LONG).show();
                break;

        }
    }

    @Override
    public void onLocationUpdate(Location location) {

        if (isFirstLocation) {
            isFirstLocation = false;
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
        } else {
            map.animateCamera(CameraUpdateFactory
                    .newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }

        if (runningManager.isRecording()) {
            Run run = runningManager.updateCurrentRun(location);
            updateViews(run);
            updateTrackOnMap(location);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {

        if (runningManager.isRecording()) {
            stopRun();
        } else {
            Snackbar.make(getView(), "Run hasn't started", Snackbar.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return true;
    }
}
