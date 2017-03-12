package at.shockbytes.corey.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import at.shockbytes.corey.BuildConfig;
import at.shockbytes.corey.R;
import at.shockbytes.corey.body.BodyInfo;
import at.shockbytes.corey.body.goal.Goal;
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener;
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener;
import at.shockbytes.corey.storage.live.LiveWorkoutUpdateListener;
import at.shockbytes.corey.util.schedule.ScheduleItem;
import at.shockbytes.corey.workout.model.Exercise;
import at.shockbytes.corey.workout.model.TimeExercise;
import at.shockbytes.corey.workout.model.Workout;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Martin Macheiner
 *         Date: 23.02.2017.
 */

public class FirebaseRealmStorageManager implements StorageManager {

    private final String PREF_DREAMWEIGHT = "dreamweight";
    private final String PREF_LAST_BODY_PULL = "last_body_pull";
    private final String PREF_WEIGHT_UNIT = "weight_unit";

    private Gson gson;
    private Context context;
    private SharedPreferences preferences;

    private Realm realm;
    private FirebaseDatabase firebase;
    private FirebaseRemoteConfig fireRemoteConfig;

    private int desiredWeight;

    private List<Goal> goals;
    private List<Workout> workouts;
    private List<ScheduleItem> scheduleItems;

    private List<LiveBodyUpdateListener> bodyListener;
    private List<LiveWorkoutUpdateListener> workoutListener;
    private List<LiveScheduleUpdateListener> scheduleListener;

    @Inject
    public FirebaseRealmStorageManager(Context context, Gson gson, SharedPreferences preferences) {
        this.gson = gson;
        this.context = context;
        this.preferences = preferences;

        desiredWeight = -1;

        goals = new ArrayList<>();
        workouts = new ArrayList<>();
        scheduleItems = new ArrayList<>();
        bodyListener = new ArrayList<>();
        workoutListener = new ArrayList<>();
        scheduleListener = new ArrayList<>();

        setupRealm();
        setupFirebase();
    }

    @Override
    public Observable<List<Workout>> getWorkouts() {
        return Observable.just(workouts);
    }

    @Override
    public void registerLiveWorkoutUpdates(LiveWorkoutUpdateListener listener) {
        if (!workoutListener.contains(listener)) {
            workoutListener.add(listener);
        }
    }

    @Override
    public void unregisterLiveWorkoutUpdates(LiveWorkoutUpdateListener listener) {
        workoutListener.remove(listener);
    }

    @Override
    public void registerLiveScheduleUpdates(LiveScheduleUpdateListener listener) {
        if (!scheduleListener.contains(listener)) {
            scheduleListener.add(listener);
        }
    }

    @Override
    public void unregisterLiveScheduleUpdates(LiveScheduleUpdateListener listener) {
        scheduleListener.remove(listener);
    }

    @Override
    public void registerLiveBodyUpdates(LiveBodyUpdateListener listener) {
        if (!bodyListener.contains(listener)) {
            bodyListener.add(listener);
        }
    }

    @Override
    public void unregisterLiveBodyUpdates(LiveBodyUpdateListener listener) {
        bodyListener.remove(listener);
    }

    @Override
    public void storeWorkout(Workout workout) {
        Log.wtf("Corey", workout.toString());
        DatabaseReference ref = firebase.getReference("/workout").push();
        workout.setId(ref.getKey());
        ref.setValue(workout);
    }

    @Override
    public void deleteWorkout(Workout workout) {
        firebase.getReference("/workout").child(workout.getId()).removeValue();
    }

    @Override
    public void updateWorkout(Workout workout) {
        firebase.getReference("/workout").child(workout.getId()).setValue(workout);
    }

    @Override
    public void pokeExercisesAndSchedulingItems() {

        // Fetch the exercises
        fireRemoteConfig.fetch()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Corey", "Fetch suceeded");

                            // Once the config is successfully fetched
                            // it must be activated before newly fetched values are returned.
                            fireRemoteConfig.activateFetched();
                        } else {
                            Log.d("Corey", "Fetch failed!");

                        }
                    }
                });
    }

    @Override
    public Observable<List<Exercise>> getExercises() {

        List<Exercise> exercises = new ArrayList<>();

        String exercisesAsJson = fireRemoteConfig
                .getString(context.getString(R.string.remote_config_exercises));
        String timeExercisesAsJson = fireRemoteConfig
                .getString(context.getString(R.string.remote_config_time_exercises));

        Gson gson = new Gson();
        String[] exercisesAsArray = gson.fromJson(exercisesAsJson, String[].class);
        String[] timeExercisesAsArray = gson.fromJson(timeExercisesAsJson, String[].class);

        for (String e : exercisesAsArray) {
            exercises.add(new Exercise(e));
        }
        for (String te : timeExercisesAsArray) {
            exercises.add(new TimeExercise(te));
        }

        return Observable.just(exercises);
    }

    @Override
    public Observable<List<ScheduleItem>> getSchedule() {
        return Observable.just(scheduleItems);
    }

    @Override
    public Observable<List<String>> getItemsForScheduling() {

        List<String> items = new ArrayList<>();
        for (Workout w : workouts) {
            items.add(w.getName());
        }

        String schedulingItemsAsJson = fireRemoteConfig
                .getString(context.getString(R.string.remote_config_scheduling_items));
        String[] remoteConfigItems = gson.fromJson(schedulingItemsAsJson, String[].class);
        Collections.addAll(items, remoteConfigItems);

        return Observable.just(items);
    }

    @Override
    public ScheduleItem insertScheduleItem(ScheduleItem item) {
        DatabaseReference ref = firebase.getReference("/schedule").push();
        item.setId(ref.getKey());
        ref.setValue(item);
        return item;
    }

    @Override
    public void updateScheduleItem(ScheduleItem item) {
        firebase.getReference("/schedule").child(item.getId()).setValue(item);
    }

    @Override
    public void deleteScheduleItem(ScheduleItem item) {
        firebase.getReference("/schedule").child(item.getId()).removeValue();
    }

    @Override
    public Observable<BodyInfo> getLocalBodyInfo() {

        BodyInfo info = realm.where(BodyInfo.class).findFirst();
        if (info != null) {
            return Observable.just(info)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
        } else {
            return Observable.just(new BodyInfo())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());
        }
    }

    @Override
    public void appendToLocalBodyInfo(final BodyInfo info) {

        getLocalBodyInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<BodyInfo>() {
                    @Override
                    public void call(BodyInfo bodyInfo) {
                        appendAndStoreLocalBodyInfo(bodyInfo, info);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.wtf("Corey", throwable.toString());
                    }
                });
    }

    @Override
    public void appendAndStoreLocalBodyInfo(final BodyInfo storedInfo, final BodyInfo appendedInfo) {

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(storedInfo.appendAndUpdate(appendedInfo));
            }
        });
    }

    @Override
    public int getDesiredWeight() {
        // No sync with firebase, use local cached value
        if (desiredWeight < 0) {
            return preferences.getInt(PREF_DREAMWEIGHT, 0);
        }
        return desiredWeight;
    }

    @Override
    public void setDesiredWeight(int desiredWeight) {
        preferences.edit().putInt(PREF_DREAMWEIGHT, desiredWeight).apply();
        firebase.getReference("/body/desired").setValue(desiredWeight);
    }

    @Override
    public long getLastBodyInfoPull() {
        return preferences.getLong(PREF_LAST_BODY_PULL, 1);
    }

    @Override
    public void setLatestBodyInfoPull(long time) {
        preferences.edit().putLong(PREF_LAST_BODY_PULL, time).apply();
    }

    @Override
    public String getWeightUnit() {
        return preferences.getString(PREF_WEIGHT_UNIT, context.getString(R.string.default_weight_unit));
    }

    @Override
    public void setWeightUnit(String unit) {
        preferences.edit().putString(PREF_WEIGHT_UNIT, unit).apply();
    }

    @Override
    public Observable<List<Goal>> getBodyGoals() {
        return Observable.just(goals);
    }

    @Override
    public void updateBodyGoal(Goal g) {
        firebase.getReference("/body/goal").child(g.getId()).setValue(g);
    }

    @Override
    public void removeBodyGoal(Goal g) {
        firebase.getReference("/body/goal").child(g.getId()).removeValue();
    }

    @Override
    public void storeBodyGoal(Goal g) {
        DatabaseReference ref = firebase.getReference("/body/goal").push();
        g.setId(ref.getKey());
        ref.setValue(g);
    }

    private void setupFirebase() {

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebase = FirebaseDatabase.getInstance().getReference().getDatabase();

        firebase.getReference("/workout").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Log.wtf("Corey", String.valueOf(dataSnapshot.toString()));
                Workout w = gson.fromJson(String.valueOf(dataSnapshot.getValue()), Workout.class);
                //Log.wtf("Corey", "Added: " + w.toString());
                workouts.add(w);
                for (LiveWorkoutUpdateListener l : workoutListener) {
                    l.onWorkoutAdded(w);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Workout changed = gson.fromJson(String.valueOf(dataSnapshot.getValue()), Workout.class);
                //Log.wtf("Corey", "changed: " + changed.toString());
                workouts.set(workouts.indexOf(changed), changed);
                for (LiveWorkoutUpdateListener l : workoutListener) {
                    l.onWorkoutChanged(changed);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Workout removed = gson.fromJson(String.valueOf(dataSnapshot.getValue()), Workout.class);
                //Log.wtf("Corey", "removed: " + removed);
                workouts.remove(removed);
                for (LiveWorkoutUpdateListener l : workoutListener) {
                    l.onWorkoutDeleted(removed);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.wtf("Corey", "moved: " + dataSnapshot.toString() + " / " + s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("Corey", "cancelled: " + databaseError.getMessage());
            }
        });

        firebase.getReference("/body/desired").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object data = dataSnapshot.getValue();
                if (data != null) {
                    desiredWeight = Integer.parseInt(String.valueOf(data));
                    preferences.edit().putInt(PREF_DREAMWEIGHT, desiredWeight).apply();
                    for (LiveBodyUpdateListener l : bodyListener) {
                        l.onDesiredWeightChanged(desiredWeight);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("Corey", "Desired weight cancelled: " + databaseError.getMessage());
            }
        });

        firebase.getReference("/schedule").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ScheduleItem item = dataSnapshot.getValue(ScheduleItem.class);
                //Log.wtf("Corey", "Firebase - Schedule added: " + item.toString());
                scheduleItems.add(item);
                for (LiveScheduleUpdateListener l : scheduleListener) {
                    l.onScheduleItemAdded(item);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                ScheduleItem changed = dataSnapshot.getValue(ScheduleItem.class);
                //Log.wtf("Corey", "Firebase - ScheduleItem changed: " + changed.toString());
                scheduleItems.set(scheduleItems.indexOf(changed), changed);
                for (LiveScheduleUpdateListener l : scheduleListener) {
                    l.onScheduleItemChanged(changed);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                ScheduleItem removed = dataSnapshot.getValue(ScheduleItem.class);
                //Log.wtf("Corey", "Firebase - ScheduleItem removed: " + removed.toString());
                scheduleItems.remove(removed);
                for (LiveScheduleUpdateListener l : scheduleListener) {
                    l.onScheduleItemDeleted(removed);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.wtf("Corey", "ScheduleItem moved: " + dataSnapshot.toString() + " / " + s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("Corey", "ScheduleItem cancelled: " + databaseError.getMessage());
            }
        });

        firebase.getReference("body/goal").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Goal g = dataSnapshot.getValue(Goal.class);
                Log.wtf("Corey", "Firebase - Goal added: " + g.toString());
                goals.add(g);
                for (LiveBodyUpdateListener l : bodyListener) {
                    l.onBodyGoalAdded(g);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                Goal g = dataSnapshot.getValue(Goal.class);
                Log.wtf("Corey", "Firebase - Goal changed: " + g.toString());
                goals.set(goals.indexOf(g), g);
                for (LiveBodyUpdateListener l : bodyListener) {
                    l.onBodyGoalChanged(g);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Goal g = dataSnapshot.getValue(Goal.class);
                Log.wtf("Corey", "Firebase - Goal deleted: " + g.toString());
                goals.remove(g);
                for (LiveBodyUpdateListener l : bodyListener) {
                    l.onBodyGoalDeleted(g);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.wtf("Corey", "BodyGoal moved: " + dataSnapshot.toString() + " / " + s);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.wtf("Corey", "BodyGoal cancelled: " + databaseError.getMessage());
            }
        });

        fireRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        fireRemoteConfig.setConfigSettings(configSettings);
        fireRemoteConfig.setDefaults(R.xml.remote_config_defaults);
    }

    private void setupRealm() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                //.schemaVersion(AppParams.REALM_SCHEMA_VERSION)
                //.migration(new DanteRealmMigration())
                .build();
        realm = Realm.getInstance(config);
    }

}
