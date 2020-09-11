package at.shockbytes.corey.data.body.bmr

import at.shockbytes.corey.data.body.CoreyUser
import io.reactivex.Single

interface BmrComputation {

    val name: String

    fun compute(user: CoreyUser): Single<Bmr>
}