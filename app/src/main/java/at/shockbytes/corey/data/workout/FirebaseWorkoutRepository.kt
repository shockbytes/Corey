package at.shockbytes.corey.data.workout

import android.content.Context
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.core.workout.model.TimeExercise
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Author:  Martin Macheiner
 * Date:    22.02.2017.
 */
class FirebaseWorkoutRepository(
    private val context: Context,
    private val gson: Gson,
    private val remoteConfig: FirebaseRemoteConfig,
    private val firebase: FirebaseDatabaseAccess
) : WorkoutRepository {

    init {
        setupFirebase()
    }

    private val timeExerciseGson = Gson()

    private val workoutsList = mutableListOf<Workout>()
    private val workoutsSubject = BehaviorSubject.createDefault<List<Workout>>(listOf())

    override val exercises: Observable<List<Exercise>>
        get() = Observable.fromCallable {

            val exercisesAsJson = remoteConfig.getString(context.getString(R.string.remote_config_exercises))
            val timeExercisesAsJson = remoteConfig.getString(context.getString(R.string.remote_config_time_exercises))

            val exercisesAsArray = gson.fromJson(exercisesAsJson, Array<Exercise>::class.java)
            val timeExercisesAsArray = timeExerciseGson.fromJson(timeExercisesAsJson, Array<TimeExercise>::class.java)

            // Concatenate both arrays to one list
            // exercisesAsArray.plus(timeExercisesAsArray).toList()
            val exercises = mutableListOf<Exercise>()
            exercisesAsArray.mapTo(exercises) { it }
            timeExercisesAsArray.mapTo(exercises) { it }
            exercises.toList()
        }.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

    override val workouts: Observable<List<Workout>> = workoutsSubject

    override fun poke() {
        remoteConfig.fetchAndActivate() // Fetch immediately
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Once the config is successfully fetched
                        // it must be activated before newly fetched values are returned.
                        Timber.d("RemoteConfig fetch successful!")
                    } else {
                        Timber.d("RemoteConfig fetch failed!")
                    }
                }
    }

    override fun storeWorkout(workout: Workout) {
        firebase.access("/workout").push().let { ref ->
            workout.id = ref.key ?: ""
            ref.setValue(workout)
        }
    }

    override fun deleteWorkout(workout: Workout) {
        firebase.access("/workout").child(workout.id).removeValue()
    }

    override fun updateWorkout(workout: Workout) {
        firebase.access("/workout").child(workout.id).setValue(workout)
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

    private fun incrementIntegerWorkoutInformation(path: String, increment: Int) {

        firebase.access(path).runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {

                var value = mutableData.getValue(Int::class.java) ?: return Transaction.abort()
                value = value.plus(increment)
                mutableData.value = value
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                b: Boolean,
                dataSnapshot: DataSnapshot?
            ) = Unit
        })
    }

    private fun setupFirebase() {

        firebase.access(REF_WORKOUT).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val w = gson.fromJson(dataSnapshot.value.toString(), Workout::class.java)

                workoutsList.add(w)
                workoutsSubject.onNext(workoutsList)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val changed = gson.fromJson(dataSnapshot.value.toString(), Workout::class.java)

                workoutsList[workoutsList.indexOf(changed)] = changed
                workoutsSubject.onNext(workoutsList)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val removed = gson.fromJson(dataSnapshot.value.toString(), Workout::class.java)

                workoutsList.remove(removed)
                workoutsSubject.onNext(workoutsList)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) = Unit
            override fun onCancelled(databaseError: DatabaseError) = Unit
        })
    }

    companion object {

        private const val REF_WORKOUT = "/workout"
    }
}
