package at.shockbytes.corey.data.body.bmr

import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.data.body.model.User
import io.reactivex.Single

interface BmrComputation {

    val name: String

    fun compute(gender: Gender, weight: Double, height: Int, age: Int): Bmr
}