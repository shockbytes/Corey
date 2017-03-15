package at.shockbytes.corey.body.wearable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.workout.model.Workout;
import at.shockbytes.corey.storage.StorageManager;
import at.shockbytes.corey.workout.WorkoutManager;
import rx.functions.Action1;

/**
 * @author Martin Macheiner
 *         Date: 18.03.2017.
 */

public class AndroidWearManager implements WearableManager,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        DataApi.DataListener {

    private Context context;
    private GoogleApiClient apiClient;
    private OnWearableDataListener wearableDataListener;

    private Gson gson;
    private WorkoutManager workoutManager;
    private SharedPreferences preferences;
    private StorageManager storageManager;

    @Inject
    public AndroidWearManager(Context context, WorkoutManager workoutManager,
                              StorageManager storageManager, SharedPreferences preferences,
                              Gson gson) {
        this.context = context;
        this.workoutManager = workoutManager;
        this.storageManager = storageManager;
        this.preferences = preferences;
        this.gson = gson;
    }

    @Override
    public void connectIfDeviceAvailable(FragmentActivity activity,
                                         OnWearableDataListener wearableDataListener) {

        this.wearableDataListener = wearableDataListener;

        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .enableAutoManage(activity, 1, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    public void synchronizeWorkouts(List<Workout> workouts) {

        PutDataRequest request = PutDataRequest.create("/workouts");
        String serializedWorkouts = gson.toJson(workouts);
        request.setData(serializedWorkouts.getBytes());

        Wearable.DataApi.putDataItem(apiClient, request);
    }

    @Override
    public void synchronizeCountdownAndVibration(int countdown, boolean isVibrationEnabled) {

        PutDataRequest countdownRequest = PutDataRequest.create("/countdown");
        countdownRequest.setData(String.valueOf(countdown).getBytes());
        PutDataRequest vibrationRequest = PutDataRequest.create("/vibration");
        vibrationRequest.setData(String.valueOf(isVibrationEnabled).getBytes());

        Wearable.DataApi.putDataItem(apiClient, countdownRequest);
        Wearable.DataApi.putDataItem(apiClient, vibrationRequest);
    }

    @Override
    public void onPause() {
        Wearable.DataApi.removeListener(apiClient, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(context,
                "Cannot connect to wearable: " + connectionResult.getErrorMessage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(apiClient, this);
        synchronize();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        int avgPulse = 0;
        int workouts = 0;
        int workoutTime = 0;
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                String path = item.getUri().getPath();
                String data = new String(item.getData());
                switch (path) {
                    case "/avg_pulse":
                        avgPulse = Integer.parseInt(data);
                        break;
                    case "/workout_count":
                        workouts = Integer.parseInt(data);
                        break;
                    case "/workout_time":
                        workoutTime = Integer.parseInt(data);
                        break;
                }
            }
        }

        storageManager.updateWorkoutInformation(avgPulse, workouts, workouts, workoutTime);

        if (wearableDataListener != null && (avgPulse != 0 || workouts != 0 || workoutTime != 0)) {
            wearableDataListener.onWearableDataAvailable(avgPulse, workouts, workoutTime);
            resetSharedObjects();
        }
    }

    private void resetSharedObjects() {

        PutDataRequest pulseRequest = PutDataRequest.create("/avg_pulse");
        pulseRequest.setData(String.valueOf(0).getBytes());
        Wearable.DataApi.putDataItem(apiClient, pulseRequest);
        PutDataRequest workoutCountRequest = PutDataRequest.create("/workout_count");
        workoutCountRequest.setData(String.valueOf(0).getBytes());
        Wearable.DataApi.putDataItem(apiClient, workoutCountRequest);
        PutDataRequest workoutTimeRequest = PutDataRequest.create("/workout_time");
        workoutTimeRequest.setData(String.valueOf(0).getBytes());
        Wearable.DataApi.putDataItem(apiClient, workoutTimeRequest);
    }

    private void synchronize() {

        workoutManager.getWorkouts().subscribe(new Action1<List<Workout>>() {
            @Override
            public void call(List<Workout> workouts) {
                synchronizeWorkouts(workouts);
            }
        });

        boolean isVibrationEnabled = preferences.getBoolean(context
                .getString(R.string.prefs_vibrations_key), false);
        int countdown = preferences.getInt(context
                .getString(R.string.prefs_time_countdown_key), 5);
        synchronizeCountdownAndVibration(countdown, isVibrationEnabled);
    }
}
