package at.shockbytes.corey.data.body

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import at.shockbytes.corey.R
import at.shockbytes.corey.data.body.bmr.Bmr
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.body.info.BodyInfo
import at.shockbytes.corey.data.body.info.WeightPoint
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    private var _bodyInfo: BodyInfo = BodyInfo()

    private var _desiredWeight: Int = -1

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
                ?: context.getString(R.string.default_weight_unit)

    override val bodyInfo: Observable<BodyInfo>
        get() {
            return if (_bodyInfo.isNotEmpty) {
                Observable.just(_bodyInfo)
            } else {
                loadFitnessDataSync() // Request new data if nothing is available
            }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        }

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

    override val currentWeight: Single<Double>
        get() = bodyInfo
            .map { bodyInfo ->
                bodyInfo.latestWeightPoint.weight
            }
            .singleOrError()

    override fun computeBasalMetabolicRate(): Single<Bmr> {
        return retrieveCoreyUser()
                .flatMap(bmrComputation::compute)
    }

    override fun retrieveCoreyUser(): Single<CoreyUser> {
        TODO("Not yet implemented")
    }

    // -----------------------------------------------------------------------------------------

    private fun loadFitnessData() {
        Fitness.HistoryApi.readData(apiClient, buildGoogleFitRequest())
                .setResultCallback { res ->
                    _bodyInfo = BodyInfo(
                            getWeightList(res.getDataSet(DataType.TYPE_WEIGHT)),
                            getHeight(res.getDataSet(DataType.TYPE_HEIGHT)),
                            desiredWeight
                    )
                }
    }

    private fun loadFitnessDataSync(): Observable<BodyInfo> {
        return Observable.fromCallable {
            val res = Fitness.HistoryApi.readData(apiClient, buildGoogleFitRequest()).await()
            _bodyInfo = BodyInfo(getWeightList(res.getDataSet(DataType.TYPE_WEIGHT)),
                    getHeight(res.getDataSet(DataType.TYPE_HEIGHT)),
                    desiredWeight)
            _bodyInfo
        }
    }

    private fun buildGoogleFitRequest(): DataReadRequest {

        val startMillis = 1.toLong()
        val endMillis = System.currentTimeMillis()

        return DataReadRequest.Builder()
                .setTimeRange(startMillis, endMillis, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_HEIGHT)
                .read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .build()
    }

    private fun getWeightList(set: DataSet): List<WeightPoint> {
        return set.dataPoints.mapTo(mutableListOf()) {
            val timeStamp = it.getStartTime(TimeUnit.MILLISECONDS)
            val weight = it.getValue(it.dataType.fields[0]).asFloat().toDouble()
            WeightPoint(timeStamp, AppUtils.roundDouble(weight, 1))
        }
    }

    private fun getHeight(set: DataSet): Double {
        return if (set.dataPoints.isNotEmpty()) {
            val dp = set.dataPoints[set.dataPoints.size - 1]
            AppUtils.roundDouble(dp.getValue(dp.dataType.fields[0]).asFloat().toDouble(), 2)
        } else 0.0
    }

    private fun setupFirebase() {

        firebase.getReference("/body/desired").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.value
                if (data != null) {
                    _desiredWeight = Integer.parseInt(data.toString())
                    preferences.edit().putInt(PREF_DREAM_WEIGHT, _desiredWeight).apply()
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
    }
}
