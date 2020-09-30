package at.shockbytes.corey.data.google

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import at.shockbytes.corey.common.roundDouble
import at.shockbytes.corey.data.body.model.WeightDataPoint
import at.shockbytes.util.AppUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResult
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

/**
 * Author:  Martin Macheiner
 * Date:    30.09.2020
 */
class CoreyGoogleApiClient(
        private val context: Context
) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private val connectionSubject = BehaviorSubject.createDefault(false)
    fun onConnectionEvent(): Observable<Boolean> = connectionSubject

    private val apiClient: GoogleApiClient = GoogleApiClient.Builder(context)
            .addApi(Fitness.HISTORY_API)
            .addApi(Fitness.RECORDING_API)
            .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
            .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
            .addConnectionCallbacks(this)
            .build()

    init {
        apiClient.connect()
    }

    override fun onConnected(bundle: Bundle?) {
        connectionSubject.onNext(true)
    }

    fun loadGoogleFitUserData(): Observable<GoogleFitUserData> {
        return fitnessApiToObservable(buildGoogleFitRequest()) { result ->

            val weightHistory = retrieveWeightHistory(result.getDataSet(DataType.TYPE_WEIGHT))
            val height = retrieveHeight(result.getDataSet(DataType.TYPE_HEIGHT))

            GoogleFitUserData(weightHistory, height)
        }
    }

    private fun <T> fitnessApiToObservable(
            readRequest: DataReadRequest,
            mapper: (DataReadResult)-> T,
    ): Observable<T> {
        return Observable.create { emitter ->
            Fitness.HistoryApi.readData(apiClient, readRequest)
                    .setResultCallback { result ->
                        val transformed = mapper(result)
                        emitter.onNext(transformed)
                    }
        }

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

    private fun retrieveWeightHistory(set: DataSet): List<WeightDataPoint> {
        return set.dataPoints.mapTo(mutableListOf()) { dp ->
            val timeStamp = dp.getStartTime(TimeUnit.MILLISECONDS)
            val weight = dp.getValue(dp.dataType.fields[0]).asFloat().toDouble()
            WeightDataPoint(timeStamp, AppUtils.roundDouble(weight, 1))
        }
    }

    private fun retrieveHeight(set: DataSet): Int {
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

    override fun onConnectionSuspended(i: Int) {
        Toast.makeText(context, "Connection suspended: $i", Toast.LENGTH_LONG).show()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        connectionSubject.onNext(false)
        Toast.makeText(context,
                "Exception while connecting to Google Play Services: ${connectionResult.errorMessage}",
                Toast.LENGTH_LONG).show()
    }


}