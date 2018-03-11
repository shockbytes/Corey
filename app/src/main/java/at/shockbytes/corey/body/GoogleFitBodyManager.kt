package at.shockbytes.corey.body

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
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
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
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
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private var apiClient: GoogleApiClient? = null
    private var _bodyInfo: BodyInfo = BodyInfo()

    override val bodyInfo: Observable<BodyInfo>
        get() {
            return if (_bodyInfo.isNotEmpty) {
                Observable.just(_bodyInfo)
            } else {
                loadFitnessDataSync() // Request new data if nothing is available
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

    // -----------------------------------------------------------------------------------------

    private fun loadFitnessData() {
        Fitness.HistoryApi.readData(apiClient, buildGoogleFitRequest())
                .setResultCallback { res ->
                    _bodyInfo = BodyInfo(getWeightList(res.getDataSet(DataType.TYPE_WEIGHT)),
                            getHeight(res.getDataSet(DataType.TYPE_HEIGHT)),
                            desiredWeight)
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
