package at.shockbytes.corey.body

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.Toast
import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.body.info.WeightPoint
import at.shockbytes.corey.storage.StorageManager
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener
import at.shockbytes.util.AppUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


/**
 * @author  Martin Macheiner
 * Date:    04.08.2016.
 */
class GoogleFitBodyManager(private val context: Context,
                           private val storageManager: StorageManager) : BodyManager,
        ResultCallback<DataReadResult>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private var apiClient: GoogleApiClient? = null
    private var _bodyInfo: BodyInfo? = null

    override val bodyInfo: Observable<BodyInfo>
        get() {
            return if (_bodyInfo != null) {
                Observable.just(_bodyInfo!!)
            } else {
                loadFitnessData() // Request new data if nothing is available
                Observable.just(BodyInfo())
            }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
        }


    override var desiredWeight: Int
        get() = storageManager.desiredWeight
        set(desiredWeight) {
            storageManager.desiredWeight = desiredWeight
        }

    override val weightUnit: String
        get() = storageManager.weightUnit

    override val bodyGoals: Observable<List<Goal>>
        get() = storageManager.goals
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())

    override fun poke(activity: FragmentActivity) {

        if (apiClient == null) {
            apiClient = GoogleApiClient.Builder(context)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.RECORDING_API)
                    .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                    .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addConnectionCallbacks(this)
                    .enableAutoManage(activity, 0, this)
                    .build()
        }
    }

    override fun updateBodyGoal(g: Goal) {
        storageManager.updateBodyGoal(g)
    }

    override fun removeBodyGoal(g: Goal) {
        storageManager.removeBodyGoal(g)
    }

    override fun storeBodyGoal(g: Goal) {
        storageManager.storeBodyGoal(g)
    }

    override fun registerLiveBodyUpdates(listener: LiveBodyUpdateListener) {
        storageManager.registerLiveBodyUpdates(listener)
    }

    override fun unregisterLiveBodyUpdates() {
        storageManager.unregisterLiveBodyUpdates()
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

    override fun onResult(res: DataReadResult) {
        _bodyInfo = BodyInfo(getWeightList(res.getDataSet(DataType.TYPE_WEIGHT)),
                getHeight(res.getDataSet(DataType.TYPE_HEIGHT)),
                desiredWeight)
        Log.wtf("Corey", "BodyInfo loaded!")
    }

    // -----------------------------------------------------------------------------------------

    private fun loadFitnessData() {
        Fitness.HistoryApi.readData(apiClient, buildGoogleFitRequest())
                .setResultCallback(this, 1, TimeUnit.MINUTES)
        Log.wtf("Corey", "Request fitness data")
    }

    private fun buildGoogleFitRequest(): DataReadRequest {

        val startMillis = 1.toLong()
        val endMillis = System.currentTimeMillis()

        return DataReadRequest.Builder()
                .setTimeRange(startMillis, endMillis, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_HEIGHT)
                //.read(DataType.TYPE_BODY_FAT_PERCENTAGE)
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

}
