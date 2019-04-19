package at.shockbytes.weather

data class CurrentWeather(
    val validUntil: Long,
    val iconRes: Int,
    val temperature: Int,
    val locality: String? = null
)