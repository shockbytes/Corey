package at.shockbytes.corey.data.schedule

import android.content.Context
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.WorkoutIconType
import at.shockbytes.corey.data.workout.WorkoutRepository
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import io.reactivex.Observable

class FirebaseRemoteConfigSchedulableItemResolver(
    private val context: Context,
    private val gson: Gson,
    private val workoutManager: WorkoutRepository,
    private val remoteConfig: FirebaseRemoteConfig
) : SchedulableItemResolver {

    override fun resolveSchedulableItems(): Observable<List<SchedulableItem>> {
        return workoutManager.workouts
            .map { workouts ->
                val workoutItems = workouts
                    .map { w ->
                        SchedulableItem(
                            w.displayableName,
                            w.locationType,
                            WorkoutIconType.fromBodyRegion(w.bodyRegion)
                        )
                    }
                    .toMutableList()

                val schedulingItemsAsJson = remoteConfig
                    .getString(context.getString(R.string.remote_config_scheduling_items))
                val remoteConfigItems = gson.fromJson(schedulingItemsAsJson, Array<SchedulableItem>::class.java)
                workoutItems
                    .apply {
                        addAll(remoteConfigItems)
                    }
                    .toList()
            }
    }
}