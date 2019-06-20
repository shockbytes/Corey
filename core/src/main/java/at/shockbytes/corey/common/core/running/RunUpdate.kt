package at.shockbytes.corey.common.core.running

/**
 * Author:  Martin Macheiner
 * Date:    02.10.2017
 */
class RunUpdate(
    val currentLocation: CoreyLatLng,
    val locations: List<CoreyLatLng>,
    val distance: Double,
    val currentPace: String,
    val isRunInfoAvailable: Boolean,
    val userWeight: Double
)