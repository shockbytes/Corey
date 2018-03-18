package at.shockbytes.corey.body

import at.shockbytes.corey.body.goal.Goal

/**
 * @author Martin Macheiner
 * Date: 27.02.2017.
 */

interface LiveBodyUpdateListener {

    fun onDesiredWeightChanged(changed: Int)

    fun onBodyGoalAdded(g: Goal)

    fun onBodyGoalDeleted(g: Goal)

    fun onBodyGoalChanged(g: Goal)

}
