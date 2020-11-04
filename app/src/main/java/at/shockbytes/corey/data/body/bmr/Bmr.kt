package at.shockbytes.corey.data.body.bmr

data class Bmr(
    val kcal: Int,
    val kcalWithActivityFactor: Int,
    val computationAlgorithm: String
)