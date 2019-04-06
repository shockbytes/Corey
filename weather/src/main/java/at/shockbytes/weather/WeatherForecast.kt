package at.shockbytes.weather

data class WeatherForecast(
    val validUntil: Long,
    val forecastItems: List<ForecastItem>,
    val place: String
) {

    operator fun get(idx: Int): ForecastItem = forecastItems[idx]

    data class ForecastItem(
        val timestamp: Long,
        val dayOfMonth: Int,
        val temperature: Int,
        val icon: Int
    )
}