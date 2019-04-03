package at.shockbytes.weather

data class WeatherForecast(
    val validUntil: Long,
    val forecastItems: List<ForecastItem>,
    val place: String
) {

    operator fun get(idx: Int): ForecastItem = forecastItems[idx]

    data class ForecastItem(
        val temperature: Int,
        val icon: Int
    )
}