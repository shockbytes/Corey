package at.shockbytes.corey.data.body

import at.shockbytes.corey.common.core.Gender

data class CoreyUser(
    val heightInCm: Int,
    val weight: Double,
    val gender: Gender,
    val age: Int
)