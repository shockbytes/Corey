package at.shockbytes.corey.data.body

import at.shockbytes.corey.data.body.model.User
import at.shockbytes.corey.data.body.model.WeightUnit
import io.reactivex.Observable

/**
 * Author:  Martin Macheiner
 * Date:    04.08.2016
 */
interface BodyRepository {

    val user: Observable<User>

    var desiredWeight: Int

    val weightUnit: WeightUnit

    val currentWeight: Observable<Double>
}
