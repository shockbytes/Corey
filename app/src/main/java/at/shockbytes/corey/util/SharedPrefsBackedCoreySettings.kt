package at.shockbytes.corey.util

import android.content.Context
import android.content.SharedPreferences
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.CoreySettings

class SharedPrefsBackedCoreySettings(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : CoreySettings {
    override var isWeatherForecastEnabled: Boolean
        get() = sharedPreferences.getBoolean(context.getString(R.string.prefs_schedule_weather_forecast_key), true)
        set(value) = sharedPreferences.edit().putBoolean(context.getString(R.string.prefs_schedule_weather_forecast_key), value).apply()
}