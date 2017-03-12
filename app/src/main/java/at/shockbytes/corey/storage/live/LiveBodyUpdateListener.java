package at.shockbytes.corey.storage.live;

import at.shockbytes.corey.body.goal.Goal;

/**
 * @author Martin Macheiner
 *         Date: 27.02.2017.
 */

public interface LiveBodyUpdateListener {

    void onDesiredWeightChanged(int changed);

    void onBodyGoalAdded(Goal g);

    void onBodyGoalDeleted(Goal g);

    void onBodyGoalChanged(Goal g);

}
