package at.shockbytes.corey.data.body

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.data.body.bmr.Bmr
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.body.model.User
import at.shockbytes.corey.data.body.model.WeightDataPoint
import at.shockbytes.corey.data.body.model.WeightUnit
import at.shockbytes.util.AppUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.Single
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
        private val bmrComputation: BmrComputation
) : BodyRepository, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val apiClient: GoogleApiClient

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

    // TODO Where to get this information?
    private val age: Int = 27
    private val userGender = Gender.MALE

    private val userBodySubject =  BehaviorSubject.create<User>()

    override var desiredWeight: Int
        get() = preferences.getInt(PREF_DREAM_WEIGHT, 0)

        set(value) {
            preferences.edit().putInt(PREF_DREAM_WEIGHT, value).apply()
            firebase.getReference(REF_DESIRED).setValue(value)
        }

    override val weightUnit: WeightUnit
        get() {
            val acronym = preferences
                    .getString(PREF_WEIGHT_UNIT, context.getString(R.string.default_weight_unit))
                    ?: context.getString(R.string.default_weight_unit)

            return WeightUnit.of(acronym)
        }

    override val user: Observable<User> = userBodySubject

    override fun onConnected(bundle: Bundle?) {
        loadFitnessData()
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


    override fun computeBasalMetabolicRate(): Single<Bmr> {
        return userBodySubject
                .singleOrError()
                .flatMap(bmrComputation::compute)
    }

    // -----------------------------------------------------------------------------------------

    private fun loadFitnessData() {
        Fitness.HistoryApi.readData(apiClient, buildGoogleFitRequest())
                .setResultCallback { result ->
                    userBodySubject.onNext(
                            User(
                                getWeightList(result.getDataSet(DataType.TYPE_WEIGHT)),
                                getHeight(result.getDataSet(DataType.TYPE_HEIGHT)),
                                userGender,
                                age,
                                desiredWeight
                        )
                    )
                }
    }

    private fun buildGoogleFitRequest(): DataReadRequest {

        val startMillis = 1.toLong()
        val endMillis = System.currentTimeMillis()

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
            val extracted = dp.getValue(dp.dataType.fields[0]).asFloat().toDouble()
            AppUtils.roundDouble(extracted, 2).toInt()
        } else 0
    }

    private fun setupFirebase() {

        firebase.getReference(REF_DESIRED).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.value
                if (data != null) {
                    val fbDesiredWeight = Integer.parseInt(data.toString())
                    preferences.edit().putInt(PREF_DREAM_WEIGHT, fbDesiredWeight).apply()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) = Unit
        })
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

        private const val PREF_DREAM_WEIGHT = "dreamweight"
        private const val PREF_WEIGHT_UNIT = "weight_unit"

        private const val REF_DESIRED = "/body/desired"
    }
}
