package at.shockbytes.corey.body;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import at.shockbytes.corey.body.goal.Goal;
import at.shockbytes.corey.storage.StorageManager;
import at.shockbytes.corey.body.points.BodyFatPoint;
import at.shockbytes.corey.body.points.WeightPoint;
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener;
import io.realm.RealmList;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static at.shockbytes.corey.common.core.util.ResourceManager.roundDoubleWithDigits;

/**
 * @author Martin Macheiner
 *         Date: 04.08.2016.
 */
public class GoogleFitBodyManager implements BodyManager, ResultCallback<DataReadResult>,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient apiClient;

    private StorageManager storageManager;

    @Inject
    public GoogleFitBodyManager(Context context, StorageManager storageManager) {
        this.context = context;
        this.storageManager = storageManager;
    }

    @Override
    public void onResult(@NonNull DataReadResult res) {

        BodyInfo bodyInfo = new BodyInfo();
        bodyInfo.setDreamWeight(getDesiredWeight());
        bodyInfo.setWeightPoints(getWeightAsList(res.getDataSet(DataType.TYPE_WEIGHT)));
        bodyInfo.setHeight(getHeight(res.getDataSet(DataType.TYPE_HEIGHT)));
        bodyInfo.setBodyFatPoints(getBodyFatAsList(res.getDataSet(DataType.TYPE_BODY_FAT_PERCENTAGE)));

        // Store loaded bodyInfo, this will overwrite old value and #getBodyInfo() will always read
        // from local storage
        storageManager.appendToLocalBodyInfo(bodyInfo);
    }

    @Override
    public void poke(FragmentActivity activity) {

        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(context)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.RECORDING_API)
                    .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addConnectionCallbacks(this)
                    .enableAutoManage(activity, 0, this)
                    .build();
        }
    }

    @Override
    public Observable<BodyInfo> getBodyInfo() {
        return storageManager.getLocalBodyInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    @Override
    public int getDesiredWeight() {
        return storageManager.getDesiredWeight();
    }

    @Override
    public void setDesiredWeight(int desiredWeight) {
        storageManager.setDesiredWeight(desiredWeight);
    }

    @Override
    public String getWeightUnit() {
        return storageManager.getWeightUnit();
    }

    @Override
    public void setWeightUnit(String unit) {
        storageManager.setWeightUnit(unit);
    }

    @Override
    public Observable<List<Goal>> getBodyGoals() {
        return storageManager.getBodyGoals()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public void updateBodyGoal(Goal g) {
        storageManager.updateBodyGoal(g);
    }

    @Override
    public void removeBodyGoal(Goal g) {
        storageManager.removeBodyGoal(g);
    }

    @Override
    public void storeBodyGoal(Goal g) {
        storageManager.storeBodyGoal(g);
    }

    @Override
    public void registerLiveBodyUpdates(LiveBodyUpdateListener listener) {
        storageManager.registerLiveBodyUpdates(listener);
    }

    @Override
    public void unregisterLiveBodyUpdates(LiveBodyUpdateListener listener) {
        storageManager.unregisterLiveBodyUpdates(listener);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        loadFitnessData();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(context, "Connection suspended: " + i,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context,
                "Exception while connecting to Google Play Services: "
                        + connectionResult.getErrorMessage(),
                Toast.LENGTH_LONG).show();
    }

    private RealmList<WeightPoint> getWeightAsList(DataSet set) {

        RealmList<WeightPoint> weightPoints = new RealmList<>();
        for (DataPoint dp : set.getDataPoints()) {
            long time = dp.getStartTime(TimeUnit.MILLISECONDS);
            double weight = 0;
            for (Field field : dp.getDataType().getFields()) {
                weight = dp.getValue(field).asFloat();
            }
            weightPoints.add(new WeightPoint(time, roundDoubleWithDigits(weight, 1)));
        }
        return weightPoints;
    }

    private RealmList<BodyFatPoint> getBodyFatAsList(DataSet set) {

        RealmList<BodyFatPoint> bodyFatPoints = new RealmList<>();
        for (DataPoint dp : set.getDataPoints()) {
            long time = dp.getStartTime(TimeUnit.MILLISECONDS);
            double bodyFat = 0;
            for (Field field : dp.getDataType().getFields()) {
                bodyFat = dp.getValue(field).asFloat();
            }
            bodyFatPoints.add(new BodyFatPoint(time, roundDoubleWithDigits(bodyFat, 1)));
        }
        return bodyFatPoints;
    }

    private double getHeight(DataSet set) {

        double height = 0;
        if (set.getDataPoints() != null && set.getDataPoints().size() > 0) {
            int size = set.getDataPoints().size();
            DataPoint dp = set.getDataPoints().get(size - 1);
            for (Field field : dp.getDataType().getFields()) {
                height = roundDoubleWithDigits(dp.getValue(field).asFloat(), 2);
            }
        }
        return height;
    }

    private DataReadRequest buildGoogleFitRequest() {

        long startMillis = storageManager.getLastBodyInfoPull();
        //long startMillis = 1;
        long endMillis = System.currentTimeMillis();

        DataReadRequest request = new DataReadRequest.Builder()
                .setTimeRange(startMillis, endMillis, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_WEIGHT)
                .read(DataType.TYPE_HEIGHT)
                //.read(DataType.TYPE_BODY_FAT_PERCENTAGE)
                .build();

        // Store the latest pull timestamp
        storageManager.setLatestBodyInfoPull(endMillis);

        return request;
    }

    private void loadFitnessData() {

        DataReadRequest request = buildGoogleFitRequest();
        Fitness.HistoryApi.readData(apiClient, request).setResultCallback(this, 1, TimeUnit.MINUTES);
    }

}
