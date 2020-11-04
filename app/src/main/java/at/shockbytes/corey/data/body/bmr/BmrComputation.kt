package at.shockbytes.corey.data.body.bmr

import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender

interface BmrComputation {

    val name: String

    fun compute(
        gender: Gender,
        weight: Double,
        height: Int,
        age: Int,
        activityLevel: ActivityLevel
    ): Bmr
}