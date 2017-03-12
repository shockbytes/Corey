package at.shockbytes.corey.body;

import android.support.v4.app.FragmentActivity;

import java.util.List;

import at.shockbytes.corey.body.goal.Goal;
import at.shockbytes.corey.storage.live.LiveBodyUpdateListener;
import rx.Observable;

/**
 * @author Martin Macheiner
 *         Date: 04.08.2016.
 */
public interface BodyManager {

    void poke(FragmentActivity activity);

    Observable<BodyInfo> getBodyInfo();

    int getDesiredWeight();

    void setDesiredWeight(int desiredWeight);

    String getWeightUnit();

    void setWeightUnit(String unit);

    Observable<List<Goal>> getBodyGoals();

    void updateBodyGoal(Goal g);

    void removeBodyGoal(Goal g);

    void storeBodyGoal(Goal g);

    void registerLiveBodyUpdates(LiveBodyUpdateListener listener);

    void unregisterLiveBodyUpdates(LiveBodyUpdateListener listener);

}
