package at.shockbytes.weather

// TODO Write this class
data class DailyWeatherForecast(val validUntil: Long) {

    operator fun get(index: Int): CurrentWeather {
        // TODO Replace this
        return CurrentWeather(0, 0, 0, null)
    }
}