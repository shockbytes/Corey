package at.shockbytes.corey.common.core.running

import at.shockbytes.corey.common.core.location.CoreyLocation

/**
 * Author:  Martin Macheiner
 * Date:    02.10.2017
 */
class RunUpdate(
    val currentLocation: CoreyLocation,
    val locations: List<CoreyLocation>,
    val distance: Double,
    val currentPace: String,
    val userWeight: Double
)