package at.shockbytes.corey.data.body.bmr

import at.shockbytes.corey.data.body.model.User
import io.reactivex.Single

interface BmrComputation {

    val name: String

    fun compute(user: User): Single<Bmr>
}