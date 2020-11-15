package at.shockbytes.corey.data.google

import android.content.Context
import at.shockbytes.corey.common.roundDouble
import at.shockbytes.corey.data.body.model.WeightDataPoint
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.HistoryClient
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.HealthDataTypes
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import io.reactivex.Observable
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

/**
 * Author:  Martin Macheiner
 * Date:    30.09.2020
 */
class CoreyGoogleApi(private val context: Context) {

    private val signInAccount: GoogleSignInAccount by lazy {
        GoogleSignIn.getAccountForExtension(
            context,
            FitnessOptions.builder()
                .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                .addDataType(DataType.TYPE_WEIGHT)
                .addDataType(DataType.TYPE_HEIGHT)
                .addDataType(HealthDataTypes.TYPE_BLOOD_PRESSURE)
                .build()
        )
    }

    private val fitnessHistoryClient: HistoryClient by lazy {
        Fitness.getHistoryClient(context, signInAccount)
    }

    private data class IntermediateCalories(
        val timestamp: Long,
        val calories: Float
    )

    fun loadGoogleFitWorkouts(): Observable<GoogleFitWorkouts> {
        return fitnessHistoryToObservable(buildCaloriesReadRequest()) { result ->

            val temp = result.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
            val a = temp.dataPoints
                .map { dp ->
                    val timeStamp = dp.getStartTime(TimeUnit.MILLISECONDS)
                    val calories = dp.getValue(dp.dataType.fields[0]).asFloat()
                    IntermediateCalories(timeStamp, calories)
                }
                .groupBy { a ->
                    DateTime(a.timestamp).withTimeAtStartOfDay()
                }
                .mapValues { (_, a) ->
                    a.sumByDouble { it.calories.toDouble() }
                }
                .filter { (_, a) ->
                    a < 5000 // TODO Extract threshold
                }

            a.forEach {
                println(it)
            }

            GoogleFitWorkouts()
        }
    }

    private fun buildCaloriesReadRequest(
        startTime: Long = 1L,
        endTime: Long = System.currentTimeMillis()
    ): DataReadRequest {
        return DataReadRequest.Builder()
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .read(DataType.TYPE_CALORIES_EXPENDED)
            .build()
    }

    fun loadGoogleFitUserData(
        defaultValue: GoogleFitUserData = GoogleFitUserData(listOf(), 0)
    ): Observable<GoogleFitUserData> {
        return fitnessHistoryToObservable(buildGoogleFitRequest()) { result ->

            val weightHistory = retrieveWeightHistory(result.getDataSet(DataType.TYPE_WEIGHT))
            val height = retrieveHeight(result.getDataSet(DataType.TYPE_HEIGHT))

            GoogleFitUserData(weightHistory, height)
        }.onErrorReturnItem(defaultValue)
    }

    private fun <T> fitnessHistoryToObservable(
        readRequest: DataReadRequest,
        mapper: (DataReadResponse) -> T
    ): Observable<T> {
        return Observable.create { emitter ->
            fitnessHistoryClient.readData(readRequest)
                .addOnSuccessListener { response ->
                    val transformed = mapper(response)
                    emitter.onNext(transformed)
                }
                .addOnFailureListener { exception ->
                    emitter.tryOnError(exception)
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
            .build()
    }

    private fun retrieveWeightHistory(set: DataSet): List<WeightDataPoint> {
        return set.dataPoints.mapTo(mutableListOf()) { dp ->

            val timeStamp = dp.getStartTime(TimeUnit.MILLISECONDS)
            val weight = dp.getValue(dp.dataType.fields[0])
                .asFloat()
                .toDouble()
                .roundDouble(1)

            WeightDataPoint(timeStamp, weight)
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
}