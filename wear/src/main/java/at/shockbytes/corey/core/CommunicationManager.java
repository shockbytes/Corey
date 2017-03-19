package at.shockbytes.corey.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.workout.model.Workout;

/**
 * @author Martin Macheiner
 *         Date: 18.03.2017.
 */

public class CommunicationManager implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, DataApi.DataListener {

    interface OnHandheldDataListener {

        void onWorkoutsAvailable(List<Workout> workouts);

        void onCachedWorkoutsAvailable(List<Workout> workouts);

    }

    private Context context;
    private GoogleApiClient apiClient;
    private OnHandheldDataListener handheldDataListener;

    private Gson gson;
    private SharedPreferences preferences;

    @Inject
    public CommunicationManager(Context context, SharedPreferences preferences, Gson gson) {
        this.context = context;
        this.preferences = preferences;
        this.gson = gson;
    }

    void connectIfDeviceAvailable(OnHandheldDataListener handheldDataListener) {

        this.handheldDataListener = handheldDataListener;

        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    public void synchronizeWorkoutInformation(final int additionalPulse,
                                              final int additionalWorkoutCount,
                                              final int additionalWorkoutTime) {

        final PutDataRequest pulseRequest = PutDataRequest.create("/avg_pulse");
        final PutDataRequest workoutCountRequest = PutDataRequest.create("/workout_count");
        final PutDataRequest workoutTimeRequest = PutDataRequest.create("/workout_time");
        Wearable.DataApi.getDataItems(apiClient)
                .setResultCallback(new ResultCallback<DataItemBuffer>() {
                    @Override
                    public void onResult(@NonNull DataItemBuffer dataItems) {

                        boolean pulseSet = false, countSet = false, timeSet = false;
                        for (DataItem item : dataItems) {
                            String data = new String(item.getData());
                            if (item.getUri().getPath().equals("/avg_pulse")) {

                                int avgPulse = Integer.parseInt(data);
                                avgPulse += additionalPulse;
                                pulseRequest.setData(String.valueOf(avgPulse).getBytes());
                                pulseSet = true;
                            } else if(item.getUri().getPath().equals("/workout_count")) {

                                int workoutCount = Integer.parseInt(data);
                                workoutCount += additionalWorkoutCount;
                                workoutCountRequest.setData(String.valueOf(workoutCount).getBytes());
                                countSet = true;
                            } else if(item.getUri().getPath().equals("/workout_time")) {

                                int workoutTime = Integer.parseInt(data);
                                workoutTime += additionalWorkoutTime;
                                workoutTimeRequest.setData(String.valueOf(workoutTime).getBytes());
                                timeSet = true;
                            }
                        }

                        if (!pulseSet) {
                            pulseRequest.setData(String.valueOf(additionalPulse).getBytes());
                        }
                        if (!countSet) {
                            workoutCountRequest.setData(String.valueOf(additionalWorkoutCount).getBytes());
                        }
                        if (!timeSet) {
                            workoutTimeRequest.setData(String.valueOf(additionalWorkoutTime).getBytes());
                        }

                        Wearable.DataApi.putDataItem(apiClient, pulseRequest);
                        Wearable.DataApi.putDataItem(apiClient, workoutCountRequest);
                        Wearable.DataApi.putDataItem(apiClient, workoutTimeRequest);
                        dataItems.release();
                    }
                });
    }

    void onStart() {
        apiClient.connect();
    }

    void onPause() {
        Wearable.DataApi.removeListener(apiClient, this);
        apiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(context,
                "Cannot connect to handheld: " + connectionResult.getErrorMessage(),
                Toast.LENGTH_LONG).show();

        grabWorkouts(true);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.DataApi.addListener(apiClient, this);
        grabWorkouts(false);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        int countdown = 0;
        boolean isVibrationEnabled = false;
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                String path = item.getUri().getPath();
                String data = new String(item.getData());
                switch (path) {
                    case "/countdown":
                        countdown = Integer.parseInt(data);
                        break;
                    case "/vibration":
                        isVibrationEnabled = Boolean.parseBoolean(data);
                        break;
                    case "/workouts":
                        List<Workout> workouts = gson.fromJson(data,
                                new TypeToken<List<Workout>>() {
                                }.getType());

                        if (handheldDataListener != null) {
                            handheldDataListener.onWorkoutsAvailable(workouts);
                        }
                        break;
                }
            }
        }

        preferences.edit()
                .putInt(context.getString(R.string.prefs_time_countdown_key), countdown)
                .putBoolean(context.getString(R.string.prefs_vibrations_key), isVibrationEnabled)
                .apply();
    }

    private void grabWorkouts(final boolean isOffline) {

        Wearable.DataApi.getDataItems(apiClient)
                .setResultCallback(new ResultCallback<DataItemBuffer>() {
                    @Override
                    public void onResult(@NonNull DataItemBuffer dataItems) {

                        for (DataItem item : dataItems) {
                            String data = new String(item.getData());
                            if (item.getUri().getPath().equals("/workouts")) {
                                List<Workout> workouts = gson.fromJson(data,
                                        new TypeToken<List<Workout>>() {
                                        }.getType());

                                if (handheldDataListener != null) {

                                    if (!isOffline) {
                                        handheldDataListener.onWorkoutsAvailable(workouts);
                                    } else {
                                        handheldDataListener.onCachedWorkoutsAvailable(workouts);
                                    }

                                }
                                break;
                            }
                        }
                        dataItems.release();
                    }
                });

    }


}
