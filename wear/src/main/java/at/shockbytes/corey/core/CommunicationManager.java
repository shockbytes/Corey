package at.shockbytes.corey.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.workout.model.Workout;

/**
 * @author Martin Macheiner
 *         Date: 18.03.2017.
 */

public class CommunicationManager implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, DataApi.DataListener, CapabilityApi.CapabilityListener {

    interface OnHandheldDataListener {

        void onWorkoutsAvailable(List<Workout> workouts);

        void onCachedWorkoutsAvailable(List<Workout> workouts);

    }

    private Context context;
    private GoogleApiClient apiClient;
    private OnHandheldDataListener handheldDataListener;

    private Gson gson;
    private SharedPreferences preferences;

    private Node connectedNode;

    private List<Workout> cachedWorkouts;

    @Inject
    public CommunicationManager(Context context, SharedPreferences preferences, Gson gson) {
        this.context = context;
        this.preferences = preferences;
        this.gson = gson;
        connectedNode = null;
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

    public void syncWorkoutInformation(int avgPulse, int workoutTime) {

        cacheData(avgPulse, workoutTime);
        if (connectedNode != null) {
            synchronizeData();
        }
    }

    void onStart() {
        apiClient.connect();
    }

    void onPause() {
        Wearable.DataApi.removeListener(apiClient, this);
        Wearable.CapabilityApi.removeCapabilityListener(apiClient, this,
                context.getString(R.string.capability));
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
        Wearable.CapabilityApi.addCapabilityListener(apiClient, this,
                context.getString(R.string.capability));
        grabWorkouts(false);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                String path = item.getUri().getPath();
                String data = new String(item.getData());
                switch (path) {
                    case "/workouts":
                        List<Workout> workouts = gson.fromJson(data,
                                new TypeToken<List<Workout>>() {
                                }.getType());

                        cachedWorkouts = workouts;
                        if (handheldDataListener != null) {
                            handheldDataListener.onWorkoutsAvailable(workouts);
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo info) {

        if (info.getNodes().size() > 0) {
            connectedNode = info.getNodes().iterator().next(); // Assume first node is handheld
            Toast.makeText(context, connectedNode.getDisplayName(), Toast.LENGTH_SHORT).show();
            synchronizeData();
        } else {
            connectedNode = null;
        }
    }

    public ArrayList<Workout> getCachedWorkouts() {

        if (cachedWorkouts == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(cachedWorkouts);
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

                                cachedWorkouts = workouts;
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

    private void synchronizeData() {

        byte[] data = parcelSyncData();
        Log.wtf("Corey", new String(data));
        Log.wtf("Corey", connectedNode.toString());
        Wearable.MessageApi.sendMessage(apiClient, connectedNode.getId(),
                "/wear_information", data);
        clearCache();
    }

    private byte[] parcelSyncData() {

        String sb = String.valueOf(preferences.getInt("pulse", 0)) +
                "," +
                preferences.getInt("workout_count", 0) +
                "," +
                preferences.getInt("workout_time", 0);
        return sb.getBytes();
    }

    private void cacheData(int avgPulse, int time) {

        int pulse = preferences.getInt("pulse", 0) + avgPulse;
        int workoutCount = preferences.getInt("workout_count", 0) + 1;
        int workoutTime = preferences.getInt("workout_time", 0) + time;

        preferences.edit()
                .putInt("pulse", pulse)
                .putInt("workout_count", workoutCount)
                .putInt("workout_time", workoutTime)
                .apply();
    }

    private void clearCache() {

        preferences.edit()
                .putInt("pulse", 0)
                .putInt("workout_count", 0)
                .putInt("workout_time", 0)
                .apply();
    }

}
