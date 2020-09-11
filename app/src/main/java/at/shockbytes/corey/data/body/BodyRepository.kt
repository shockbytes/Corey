package at.shockbytes.corey.data.body

import at.shockbytes.corey.data.body.bmr.Bmr
import at.shockbytes.corey.data.body.info.BodyInfo
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Author:  Martin Macheiner
 * Date:    04.08.2016
 */
interface BodyRepository {

    val bodyInfo: Observable<BodyInfo>

    var desiredWeight: Int

    val weightUnit: String

    val currentWeight: Single<Double>

    fun computeBasalMetabolicRate(): Single<Bmr>

    fun retrieveCoreyUser(): Single<CoreyUser>
}
