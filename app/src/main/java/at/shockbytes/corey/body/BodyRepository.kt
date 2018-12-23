package at.shockbytes.corey.body

import at.shockbytes.corey.body.goal.Goal
import at.shockbytes.corey.body.info.BodyInfo
import at.shockbytes.corey.common.core.util.Pokeable
import io.reactivex.Observable

/**
 * Author:  Martin Macheiner
 * Date:    04.08.2016
 */
interface BodyRepository {

    val bodyInfo: Observable<BodyInfo>

    var desiredWeight: Int

    val weightUnit: String

    val bodyGoals: Observable<List<Goal>>

    fun updateBodyGoal(g: Goal)

    fun removeBodyGoal(g: Goal)

    fun storeBodyGoal(g: Goal)

}
