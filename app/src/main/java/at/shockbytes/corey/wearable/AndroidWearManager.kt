package at.shockbytes.corey.wearable

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.Toast
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.storage.StorageManager
import at.shockbytes.corey.workout.WorkoutManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson

/**
 * @author Martin Macheiner
 * Date: 18.03.2017.
 */

class AndroidWearManager(private val context: Context,
                         private val workoutManager: WorkoutManager,
                         private val storageManager: StorageManager,
                         private val preferences: SharedPreferences,
                         private val gson: Gson)
    : WearableManager, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, MessageApi.MessageListener {

    private var apiClient: GoogleApiClient? = null

    override fun connect(activity: FragmentActivity) {

        if (apiClient == null) {
            apiClient = GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .enableAutoManage(activity, 1, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build()
        }
    }

    override fun synchronizeWorkouts(workouts: List<Workout>) {

        val request = PutDataRequest.create("/workouts")
        val serializedWorkouts = gson.toJson(workouts)
        request.data = serializedWorkouts.toByteArray()

        Wearable.DataApi.putDataItem(apiClient, request)
    }

    override fun onPause() {
        Wearable.MessageApi.removeListener(apiClient, this)
        Wearable.CapabilityApi.removeLocalCapability(apiClient, context.getString(R.string.capability))
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

        Toast.makeText(context,
                "Cannot connect to wearable: " + connectionResult.errorMessage!!,
                Toast.LENGTH_LONG).show()
    }

    override fun onConnected(bundle: Bundle?) {
        Wearable.MessageApi.addListener(apiClient, this)
        Wearable.CapabilityApi.addLocalCapability(apiClient, context.getString(R.string.capability))
        synchronize()
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onMessageReceived(messageEvent: MessageEvent) {

        Log.wtf("Corey", "information received! + " + messageEvent.toString())
        if (messageEvent.path == "/wear_information") {
            Toast.makeText(context, String(messageEvent.data), Toast.LENGTH_SHORT).show()
            grabDataFromMessage(messageEvent)
        }
    }

    private fun grabDataFromMessage(messageEvent: MessageEvent) {

        val data = String(messageEvent.data)
        val s = data.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        try {

            val pulse = Integer.parseInt(s[0])
            val workouts = Integer.parseInt(s[1])
            val time = Integer.parseInt(s[2])

            storageManager.updateWearWorkoutInformation(pulse, workouts, time)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun synchronize() {
        workoutManager.workouts.subscribe { workouts -> synchronizeWorkouts(workouts) }
    }

}
