package at.shockbytes.weather.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import at.shockbytes.weather.CurrentWeather
import at.shockbytes.weather.R
import kotlinx.android.synthetic.main.layout_condensed_weather_view.view.*

class CondensedWeatherView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private data class AnimationProperties(
        val fromAlpha: Float,
        val toAlpha: Float,
        val fromTranslationX: Float,
        val toTranslationX: Float,
        val duration: Long,
        val interpolator: Interpolator
    )

    private val animationProps = AnimationProperties(0f, 1f, 25f, 0f, 600, DecelerateInterpolator())

    init {
        inflate(context, R.layout.layout_condensed_weather_view, this)
    }

    @SuppressLint("SetTextI18n")
    fun setWeatherInfo(
        weather: CurrentWeather,
        unit: String,
        animate: Boolean,
        animationDelay: Long = 500L
    ) {

        if (animate) {
            tv_condensed_weather_view_temperature.alpha = animationProps.fromAlpha
            iv_condensed_weather_view.alpha = animationProps.fromAlpha

            tv_condensed_weather_view_temperature.translationX = -animationProps.fromTranslationX
            iv_condensed_weather_view.translationX = animationProps.fromTranslationX
        }

        tv_condensed_weather_view_temperature.text = "${weather.temperature}$unit"
        iv_condensed_weather_view.setImageResource(weather.iconRes)

        if (animate) {
            tv_condensed_weather_view_temperature.animate()
                .alpha(animationProps.toAlpha)
                .setStartDelay(animationDelay)
                .translationX(animationProps.toTranslationX)
                .setDuration(animationProps.duration)
                .setInterpolator(animationProps.interpolator)
                .start()
            iv_condensed_weather_view.animate()
                .alpha(animationProps.toAlpha)
                .setStartDelay(animationDelay)
                .translationX(animationProps.toTranslationX)
                .setDuration(animationProps.duration)
                .setInterpolator(animationProps.interpolator)
                .start()
        }
    }
}