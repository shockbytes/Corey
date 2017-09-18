package at.shockbytes.corey.body.wearable;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
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
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private Context context;
    private GoogleApiClient apiClient;

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
    public void connect(FragmentActivity activity) {

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
    public void onPause() {
        Wearable.MessageApi.removeListener(apiClient, this);
        Wearable.CapabilityApi.removeLocalCapability(apiClient, context.getString(R.string.capability));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(context,
                "Cannot connect to wearable: " + connectionResult.getErrorMessage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Wearable.MessageApi.addListener(apiClient, this);
        Wearable.CapabilityApi.addLocalCapability(apiClient, context.getString(R.string.capability));
        synchronize();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.wtf("Corey", "information received! + " + messageEvent.toString());
        if (messageEvent.getPath().equals("/wear_information")) {
            Toast.makeText(context, new String(messageEvent.getData()), Toast.LENGTH_SHORT).show();
            grabDataFromMessage(messageEvent);
        }

    }

    private void grabDataFromMessage(MessageEvent messageEvent) {

        String data = new String(messageEvent.getData());
        String[] s = data.split(",");

        try {

            int pulse = Integer.parseInt(s[0]);
            int workouts = Integer.parseInt(s[1]);
            int time = Integer.parseInt(s[2]);

            storageManager.updateWearWorkoutInformation(pulse, workouts, time);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void synchronize() {

        workoutManager.getWorkouts().subscribe(new Action1<List<Workout>>() {
            @Override
            public void call(List<Workout> workouts) {
                synchronizeWorkouts(workouts);
            }
        });
    }

}
