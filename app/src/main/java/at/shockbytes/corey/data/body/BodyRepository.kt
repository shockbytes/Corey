package at.shockbytes.corey.data.body

import at.shockbytes.corey.data.body.info.BodyInfo
import io.reactivex.Observable

/**
 * Author:  Martin Macheiner
 * Date:    04.08.2016
 */
interface BodyRepository {

    val bodyInfo: Observable<BodyInfo>

    var desiredWeight: Int

    val weightUnit: String
}
