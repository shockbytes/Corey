package at.shockbytes.corey.workout

import android.content.Context
import android.util.Log
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.common.core.workout.model.Workout
import com.google.firebase.database.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @author  Martin Macheiner
 * Date:    22.02.2017.
 */

class FirebaseWorkoutManager(private val context: Context,
                             private val gson: Gson,
                             private val remoteConfig: FirebaseRemoteConfig,
                             private val firebase: FirebaseDatabase) : WorkoutManager {

    init {
        setupFirebase()
    }

    private var workoutListener: LiveWorkoutUpdateListener? = null
    private var _workouts: MutableList<Workout> = mutableListOf()

    override val exercises: Observable<List<Exercise>>
        get() {

            val exercisesAsJson = remoteConfig.getString(context.getString(R.string.remote_config_exercises))
            val timeExercisesAsJson = remoteConfig.getString(context.getString(R.string.remote_config_time_exercises))

            val exercisesAsArray = gson.fromJson(exercisesAsJson, Array<String>::class.java)
            val timeExercisesAsArray = gson.fromJson(timeExercisesAsJson, Array<String>::class.java)

            val exercises = mutableListOf<Exercise>()
            exercisesAsArray.mapTo(exercises) { Exercise(it) }
            timeExercisesAsArray.mapTo(exercises) { TimeExercise(name = it) }

            return Observable.just(exercises.toList()).observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
        }

    override val workouts: Observable<List<Workout>>
        get() = Observable.just(_workouts.toList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())

    override fun poke() {
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

    override fun registerLiveWorkoutUpdates(listener: LiveWorkoutUpdateListener) {
        this.workoutListener = listener
    }

    override fun unregisterLiveWorkoutUpdates() {
        workoutListener = null
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

            override fun onComplete(databaseError: DatabaseError?, b: Boolean,
                                    dataSnapshot: DataSnapshot) {
            }
        })
    }

    private fun setupFirebase() {

        firebase.getReference("/workout").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val w = gson.fromJson(dataSnapshot.value.toString(), Workout::class.java)

                _workouts.add(w)
                workoutListener?.onWorkoutAdded(w)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
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
            }

            override fun onCancelled(databaseError: DatabaseError?) {
            }
        })

    }

}
