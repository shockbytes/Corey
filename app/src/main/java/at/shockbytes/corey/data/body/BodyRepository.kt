package at.shockbytes.corey.data.body

import at.shockbytes.corey.data.body.model.User
import io.reactivex.Observable

/**
 * Author:  Martin Macheiner
 * Date:    04.08.2016
 */
interface BodyRepository {

    val user: Observable<User>

    val desiredWeight: Observable<Int>

    val currentWeight: Observable<Double>

    fun setDesiredWeight(desiredWeight: Int)

    fun cleanUp()
}
