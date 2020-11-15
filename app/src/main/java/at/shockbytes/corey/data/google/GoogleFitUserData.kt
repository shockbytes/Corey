package at.shockbytes.corey.data.google

import at.shockbytes.corey.data.body.model.WeightDataPoint

data class GoogleFitUserData(
    val weightHistory: List<WeightDataPoint>,
    val height: Int
)