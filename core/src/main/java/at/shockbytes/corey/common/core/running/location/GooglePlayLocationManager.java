package at.shockbytes.corey.common.core.running.location;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * @author Martin Macheiner
 *         Date: 05.09.2017.
 */

public class GooglePlayLocationManager implements LocationManager {

    private FusedLocationProviderClient fusedLocationClient;
    private SettingsClient settingsClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationSettingsRequest locationSettingsRequest;

    private Location currentLocation;

    private boolean isLocationUpdateRequested;

    private OnLocationUpdateListener listener;

    public GooglePlayLocationManager(Context context) {
        initialize(context);
        isLocationUpdateRequested = false;
    }

    private void initialize(Context context) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        settingsClient = LocationServices.getSettingsClient(context);

        locationCallback = createLocationCallback();
        locationRequest = createLocationRequest();
        locationSettingsRequest = createLocationSettingsRequest();
    }

    @NonNull
    private LocationRequest createLocationRequest() {
        return new LocationRequest()
                .setInterval(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @NonNull
    private LocationCallback createLocationCallback() {
        return new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                currentLocation = locationResult.getLastLocation();
                listener.onLocationUpdate(currentLocation);
            }
        };
    }

    @NonNull
    private LocationSettingsRequest createLocationSettingsRequest() {
        return new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
    }


    @Override
    @SuppressWarnings({"MissingPermission"})
    public void start(@NonNull LocationManager.OnLocationUpdateListener l) {
        this.listener = l;

        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                        isLocationUpdateRequested = true;
                        fusedLocationClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());

                        listener.onConnected();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        isLocationUpdateRequested = false;
                        listener.onError(e);
                    }
                });
    }

    @Override
    public void stop() {

        fusedLocationClient.removeLocationUpdates(locationCallback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        isLocationUpdateRequested = false;
                        listener.onDisconnected();
                        listener = null;
                    }
                });
    }

    @Override
    public boolean isLocationUpdateRequested() {
        return isLocationUpdateRequested;
    }

}
