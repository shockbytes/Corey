package at.shockbytes.corey.core

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Workout
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Author:  Martin Macheiner
 * Date:    18.03.2017
 */
class CommunicationManager(
    private val context: Context,
    private val preferences: SharedPreferences,
    private val gson: Gson
) : DataClient.OnDataChangedListener, CapabilityClient.OnCapabilityChangedListener {

    private var workoutListener: ((List<Workout>) -> Unit)? = null

    private var connectedNode: Node? = null

    var cachedWorkouts: List<Workout> = mutableListOf()

    fun connectIfDeviceAvailable(workoutListener: (List<Workout>) -> Unit) {
        this.workoutListener = workoutListener
    }

    fun syncWorkoutInformation(avgPulse: Int, workoutTime: Int) {
        cacheData(avgPulse, workoutTime)
        synchronizeData()
    }

    fun onStart() {
        Wearable.getDataClient(context).addListener(this)
        Wearable.getCapabilityClient(context)
                .addLocalCapability(context.getString(R.string.capability_wear_device))
        Wearable.getCapabilityClient(context)
                .addListener(this, context.getString(R.string.capability_device))
        grabWorkouts()
    }

    fun onPause() {
        Wearable.getDataClient(context).removeListener(this)
        Wearable.getCapabilityClient(context)
                .removeLocalCapability(context.getString(R.string.capability_wear_device))
        Wearable.getCapabilityClient(context)
                .removeListener(this, context.getString(R.string.capability_device))
    }

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {

        for (event in dataEventBuffer) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val item = event.dataItem
                val path = item.uri.path
                val data = String(item.data)
                when (path) {
                    "/workouts" -> {
                        val workouts = gson.fromJson<List<Workout>>(data,
                                object : TypeToken<List<Workout>>() {}.type)

                        cachedWorkouts = workouts
                        workoutListener?.invoke(workouts)
                    }
                }
            }
        }
    }

    override fun onCapabilityChanged(info: CapabilityInfo) {

        if (info.nodes.size > 0) {
            connectedNode = info.nodes.iterator().next() // Assume first node is handheld
            Toast.makeText(context, connectedNode?.displayName, Toast.LENGTH_SHORT).show()
            synchronizeData()
        } else {
            connectedNode = null
        }
    }

    private fun grabWorkouts() {

        Wearable.getDataClient(context).dataItems
                .addOnSuccessListener { dataItems ->
                    dataItems.forEach { item ->
                        val data = String(item.data)
                        if (item.uri.path == "/workouts") {
                            val workouts = gson.fromJson<List<Workout>>(data,
                                    object : TypeToken<List<Workout>>() {}.type)

                            cachedWorkouts = workouts
                            workoutListener?.invoke(workouts)
                            return@forEach
                        }
                    }
                    dataItems.release()
                }
    }

    private fun synchronizeData() {

        val data = parcelSyncData()
        if (connectedNode != null) {
            Wearable.getMessageClient(context)
                    .sendMessage(connectedNode?.id!!, "/wear_information", data)
            clearCache()
        }
    }

    private fun parcelSyncData(): ByteArray {
        return ("${preferences.getInt("pulse", 0)}, ${preferences.getInt("workout_count", 0)}, " +
                "${preferences.getInt("workout_time", 0)}").toByteArray()
    }

    private fun cacheData(avgPulse: Int, time: Int) {

        val pulse = preferences.getInt("pulse", 0) + avgPulse
        val workoutCount = preferences.getInt("workout_count", 0) + 1
        val workoutTime = preferences.getInt("workout_time", 0) + time

        preferences.edit()
                .putInt("pulse", pulse)
                .putInt("workout_count", workoutCount)
                .putInt("workout_time", workoutTime)
                .apply()
    }

    private fun clearCache() {

        preferences.edit()
                .putInt("pulse", 0)
                .putInt("workout_count", 0)
                .putInt("workout_time", 0)
                .apply()
    }
}
