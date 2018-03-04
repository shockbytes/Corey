package at.shockbytes.corey.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import at.shockbytes.corey.BuildConfig
import at.shockbytes.corey.R
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener
import at.shockbytes.corey.util.schedule.ScheduleItem
import com.google.firebase.database.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import io.reactivex.Observable

/**
 * @author Martin Macheiner
 * Date: 23.02.2017.
 */

class FirebaseStorageManager(private val context: Context,
                             private val gson: Gson,
                             private val preferences: SharedPreferences) : StorageManager {

    private lateinit var firebase: FirebaseDatabase
    private lateinit var remoteConfig: FirebaseRemoteConfig

    private var _desiredWeight: Int = -1
    private var _goals: MutableList<Goal> = mutableListOf()
    private var _workouts: MutableList<Workout> = mutableListOf()
    private var scheduleItems: MutableList<ScheduleItem> = mutableListOf()

    private var bodyListener: LiveBodyUpdateListener? = null
    private var workoutListener: LiveWorkoutUpdateListener? = null
    private var scheduleListener: LiveScheduleUpdateListener? = null

    override val exercises: Observable<List<Exercise>>
        get() {

            val exercisesAsJson = remoteConfig.getString(context.getString(R.string.remote_config_exercises))
            val timeExercisesAsJson = remoteConfig.getString(context.getString(R.string.remote_config_time_exercises))

            val exercisesAsArray = gson.fromJson(exercisesAsJson, Array<String>::class.java)
            val timeExercisesAsArray = gson.fromJson(timeExercisesAsJson, Array<String>::class.java)

            val exercises = mutableListOf<Exercise>()
            exercisesAsArray.mapTo(exercises) { Exercise(it) }
            timeExercisesAsArray.mapTo(exercises) { TimeExercise(it) }

            return Observable.just(exercises)
        }

    override val workouts: Observable<List<Workout>>
        get() = Observable.just(_workouts)

    override val schedule: Observable<List<ScheduleItem>>
        get() = Observable.just(scheduleItems)

    override val itemsForScheduling: Observable<List<String>>
        get() = Observable.defer {
            val items = mutableListOf<String>()
            _workouts.mapTo(items) { it.displayableName }

            val schedulingItemsAsJson = remoteConfig.getString(context.getString(R.string.remote_config_scheduling_items))
            val remoteConfigItems = gson.fromJson(schedulingItemsAsJson, Array<String>::class.java)
            items.addAll(remoteConfigItems)

            Observable.just(items)
        }

    override var desiredWeight: Int
        get() {
            // No sync with firebase, use local cached value
            return if (_desiredWeight < 0) {
                preferences.getInt(PREF_DREAM_WEIGHT, 0)
            } else _desiredWeight
        }
        set(value) {
            preferences.edit().putInt(PREF_DREAM_WEIGHT, value).apply()
            firebase.getReference("/body/desired").setValue(value)
        }

    override val weightUnit: String
        get() = preferences.getString(PREF_WEIGHT_UNIT, context.getString(R.string.default_weight_unit))

    override val goals: Observable<List<Goal>>
        get() = Observable.just(_goals)

    init {
        setupFirebase()
    }

    override fun registerLiveWorkoutUpdates(listener: LiveWorkoutUpdateListener) {
        this.workoutListener = listener
    }

    override fun unregisterLiveWorkoutUpdates() {
        workoutListener = null
    }

    override fun registerLiveScheduleUpdates(listener: LiveScheduleUpdateListener) {
        this.scheduleListener = listener
    }

    override fun unregisterLiveScheduleUpdates() {
        scheduleListener = null
    }

    override fun registerLiveBodyUpdates(listener: LiveBodyUpdateListener) {
        this.bodyListener = listener
    }

    override fun unregisterLiveBodyUpdates() {
        bodyListener = null
    }

    override fun storeWorkout(workout: Workout) {
        val ref = firebase.getReference("/workout").push()
        workout.id = ref.key
        ref.setValue(workout)
    }

    override fun deleteWorkout(workout: Workout) {
        firebase.getReference("/workout").child(workout.id).removeValue()
    }

    override fun updateWorkout(workout: Workout) {
        firebase.getReference("/workout").child(workout.id).setValue(workout)
    }

    override fun pokeExercisesAndSchedulingItems() {
        remoteConfig.fetch()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Once the config is successfully fetched
                        // it must be activated before newly fetched values are returned.
                        remoteConfig.activateFetched()
                    } else {
                        Log.d("Corey", "RemoteConfig fetch failed!")

                    }
                }
    }

    override fun updatePhoneWorkoutInformation(workouts: Int, workoutTime: Int) {
        incrementIntegerWorkoutInformation("/body/workoutinfo/count", workouts)
        incrementIntegerWorkoutInformation("/body/workoutinfo/timeStamp", workoutTime)
    }

    override fun updateWearWorkoutInformation(avgPulse: Int, workoutsWithPulse: Int, workoutTime: Int) {
        incrementIntegerWorkoutInformation("/body/workoutinfo/pulse", avgPulse)
        incrementIntegerWorkoutInformation("/body/workoutinfo/count_with_pulse", workoutsWithPulse)
        incrementIntegerWorkoutInformation("/body/workoutinfo/count", workoutsWithPulse)
        incrementIntegerWorkoutInformation("/body/workoutinfo/timeStamp", workoutTime)
    }

    override fun insertScheduleItem(item: ScheduleItem): ScheduleItem {
        val ref = firebase.getReference("/schedule").push()
        item.id = ref.key
        ref.setValue(item)
        return item
    }

    override fun updateScheduleItem(item: ScheduleItem) {
        firebase.getReference("/schedule").child(item.id).setValue(item)
    }

    override fun deleteScheduleItem(item: ScheduleItem) {
        firebase.getReference("/schedule").child(item.id).removeValue()
    }

    override fun updateBodyGoal(g: Goal) {
        firebase.getReference("/body/goal").child(g.id).setValue(g)
    }

    override fun removeBodyGoal(g: Goal) {
        firebase.getReference("/body/goal").child(g.id).removeValue()
    }

    override fun storeBodyGoal(g: Goal) {
        val ref = firebase.getReference("/body/goal").push()
        g.id = ref.key
        ref.setValue(g)
    }

    private fun setupFirebase() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        firebase = FirebaseDatabase.getInstance().reference.database

        firebase.getReference("/workout").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val w = gson.fromJson(dataSnapshot.value.toString(), Workout::class.java)

                _workouts.add(w)
                workoutListener?.onWorkoutAdded(w)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {
                val changed = gson.fromJson(dataSnapshot.value.toString(), Workout::class.java)

                _workouts[_workouts.indexOf(changed)] = changed
                workoutListener?.onWorkoutChanged(changed)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = gson.fromJson(dataSnapshot.value.toString(), Workout::class.java)

                _workouts.remove(removed)
                workoutListener?.onWorkoutDeleted(removed)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {
                Log.wtf("Corey", "moved: " + dataSnapshot.toString() + " / " + s)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.wtf("Corey", "cancelled: " + databaseError.message)
            }
        })

        firebase.getReference("/body/desired").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.value
                if (data != null) {
                    _desiredWeight = Integer.parseInt(data.toString())

                    preferences.edit().putInt(PREF_DREAM_WEIGHT, _desiredWeight).apply()
                    bodyListener?.onDesiredWeightChanged(_desiredWeight)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.wtf("Corey", "Desired weight cancelled: " + databaseError.message)
            }
        })

        firebase.getReference("/schedule").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                val item = dataSnapshot.getValue(ScheduleItem::class.java)
                if (item != null) {
                    scheduleItems.add(item)
                    scheduleListener?.onScheduleItemAdded(item)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

                val changed = dataSnapshot.getValue(ScheduleItem::class.java)
                if (changed != null) {
                    scheduleItems[scheduleItems.indexOf(changed)] = changed
                    scheduleListener?.onScheduleItemChanged(changed)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                val removed = dataSnapshot.getValue(ScheduleItem::class.java)
                if (removed != null) {
                    scheduleItems.remove(removed)
                    scheduleListener?.onScheduleItemDeleted(removed)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {
                Log.wtf("Corey", "ScheduleItem moved: " + dataSnapshot.toString() + " / " + s)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.wtf("Corey", "ScheduleItem cancelled: " + databaseError.message)
            }
        })

        firebase.getReference("body/goal").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

                val g = dataSnapshot.getValue(Goal::class.java)
                if (g != null) {
                    _goals.add(g)
                    bodyListener?.onBodyGoalAdded(g)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String) {

                val g = dataSnapshot.getValue(Goal::class.java)
                if (g != null) {
                    _goals[_goals.indexOf(g)] = g
                    bodyListener?.onBodyGoalChanged(g)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                val g = dataSnapshot.getValue(Goal::class.java)
                if (g != null) {
                    _goals.remove(g)
                    bodyListener?.onBodyGoalDeleted(g)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {
                Log.wtf("Corey", "BodyGoal moved: " + dataSnapshot.toString() + " / " + s)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.wtf("Corey", "BodyGoal cancelled: " + databaseError.message)
            }
        })

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        remoteConfig.setConfigSettings(configSettings)
        remoteConfig.setDefaults(R.xml.remote_config_defaults)
    }

    private fun incrementIntegerWorkoutInformation(path: String, increment: Int) {

        firebase.getReference(path).runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {

                var value = mutableData.getValue(Int::class.java)
                        ?: return Transaction.abort()
                value = value.plus(increment)
                mutableData.value = value
                return Transaction.success(mutableData)
            }

            override fun onComplete(databaseError: DatabaseError, b: Boolean,
                                    dataSnapshot: DataSnapshot) {
            }
        })
    }


    companion object {

        private const val PREF_DREAM_WEIGHT = "dreamweight"
        private const val PREF_WEIGHT_UNIT = "weight_unit"

    }

}
