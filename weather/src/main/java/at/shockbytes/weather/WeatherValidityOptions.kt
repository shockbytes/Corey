package at.shockbytes.weather

data class WeatherValidityOptions(
    private val currentWeatherValidity: Long,
    private val forecastValidity: Long
) {

    fun getCurrentWeatherValidityDate(): Long = System.currentTimeMillis() + currentWeatherValidity

    fun getForecastValidityDate(): Long = System.currentTimeMillis() + forecastValidity
}