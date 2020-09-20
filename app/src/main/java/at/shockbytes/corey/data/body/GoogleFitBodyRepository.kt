package at.shockbytes.corey.data.body

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.common.roundDouble
import at.shockbytes.corey.common.core.CoreyDate
import at.shockbytes.corey.data.body.model.User
import at.shockbytes.corey.data.body.model.WeightDataPoint
import at.shockbytes.corey.util.listenForValue
import at.shockbytes.corey.util.updateValue
import at.shockbytes.util.AppUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * Author:  Martin Macheiner
 * Date:    04.08.2016
 */
class GoogleFitBodyRepository(
        private val context: Context,
        private val preferences: SharedPreferences,
        private val firebase: FirebaseDatabase,
        private val userSettings: UserSettings
) : BodyRepository, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val apiClient: GoogleApiClient

    private val compositeDisposable = CompositeDisposable()

    private val userBodySubject = BehaviorSubject.create<User>()

    private val desiredWeightSubject: BehaviorSubject<Int> = BehaviorSubject
            .createDefault(preferences.getInt(PREF_DREAM_WEIGHT, 0))

    init {
        setupFirebase()

        apiClient = GoogleApiClient.Builder(context)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.RECORDING_API)
                .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .build()

        apiClient.connect()
    }

    private fun setupFirebase() {
        firebase.listenForValue(REF_USER, REF_DESIRED, desiredWeightSubject)
    }

    override val desiredWeight: Observable<Int>
        get() = desiredWeightSubject
                .doOnNext { fbDesiredWeight ->
                    preferences.edit().putInt(PREF_DREAM_WEIGHT, fbDesiredWeight).apply()
                }

    override fun setDesiredWeight(desiredWeight: Int) {
        firebase.updateValue(REF_USER, REF_DESIRED, desiredWeight)
    }

    override fun cleanUp() {
        compositeDisposable.clear()
    }

    override val user: Observable<User> = userBodySubject

    override fun onConnected(bundle: Bundle?) {
        loadUserFromGoogleFit()
    }

    override fun onConnectionSuspended(i: Int) {
        Toast.makeText(context, "Connection suspended: $i", Toast.LENGTH_LONG).show()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Toast.makeText(context,
                "Exception while connecting to Google Play Services: ${connectionResult.errorMessage}",
                Toast.LENGTH_LONG).show()
    }

    override val currentWeight: Observable<Double>
        get() = user
                .map { bodyInfo ->
                    bodyInfo.latestWeightDataPoint?.weight ?: 0.0
                }

    private fun loadUserFromGoogleFit() {

        Observable
                .combineLatest(
                        loadGoogleFitUserData(),
                        gatherUserMetadata(),
                        { weightHeightPair, userMetadata ->
                            val (weightDataPoints, height) = weightHeightPair
                            val (gender, age, dw, activityLevel) = userMetadata

                            User(
                                    weightDataPoints,
                                    height,
                                    gender,
                                    age,
                                    dw,
                                    activityLevel
                            )
                        }
                )
                .subscribe(userBodySubject::onNext)
                .addTo(compositeDisposable)
    }

    private fun loadGoogleFitUserData(): Observable<Pair<List<WeightDataPoint>, Int>> {
        return Observable.create { emitter ->

            Fitness.HistoryApi.readData(apiClient, buildGoogleFitRequest())
                    .setResultCallback { result ->

                        val weightList = getWeightList(result.getDataSet(DataType.TYPE_WEIGHT))
                        val height = getHeight(result.getDataSet(DataType.TYPE_HEIGHT))

                        if (weightList.isNotEmpty() && height > 0) {
                            emitter.onNext(weightList to height)
                        }
                    }
        }
    }

    private data class UserMetadata(
            val gender: Gender,
            val birthday: CoreyDate,
            val desiredWeight: Int,
            val activityLevel: ActivityLevel
    )

    private fun gatherUserMetadata(): Observable<UserMetadata> {
        return Observable
                .combineLatest(
                        userSettings.gender,
                        userSettings.birthday,
                        desiredWeight,
                        userSettings.activityLevel,
                        { gender, birthDay, desiredWeight, activityLevel ->
                            UserMetadata(gender, birthDay, desiredWeight, activityLevel)
                        }
                )
    }

    private fun buildGoogleFitRequest(
            startMillis: Long = 1.toLong(),
            endMillis: Long = System.currentTimeMillis()
    ): DataReadRequest {
        return DataReadRequest.Builder()
                .setTimeRange(startMillis, endMillis, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_HEIGHT)
                // .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .build()
    }

    private fun getWeightList(set: DataSet): List<WeightDataPoint> {
        return set.dataPoints.mapTo(mutableListOf()) { dp ->
            val timeStamp = dp.getStartTime(TimeUnit.MILLISECONDS)
            val weight = dp.getValue(dp.dataType.fields[0]).asFloat().toDouble()
            WeightDataPoint(timeStamp, AppUtils.roundDouble(weight, 1))
        }
    }

    private fun getHeight(set: DataSet): Int {
        return if (set.dataPoints.isNotEmpty()) {
            val dp = set.dataPoints[set.dataPoints.size - 1]
            dp.getValue(dp.dataType.fields[0])
                    .asFloat()
                    .toDouble()
                    .roundDouble(2)
                    .times(100)
                    .toInt()
        } else 0
    }

    private fun buildCaloriesReadRequest(
            startTime: Long = 1L,
            endTime: Long = System.currentTimeMillis()
    ): DataReadRequest {
        return DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByActivityType(1, TimeUnit.SECONDS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
    }


    companion object {

        private const val PREF_DREAM_WEIGHT = "desired_weight"

        private const val REF_USER = "/user"
        private const val REF_DESIRED = "/desired_weight"
    }
}
