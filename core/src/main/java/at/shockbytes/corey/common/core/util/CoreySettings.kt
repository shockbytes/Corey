package at.shockbytes.corey.common.core.util

import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.DateTime

interface CoreySettings {

    val isWeatherForecastEnabled: Observable<Boolean>

    fun setWeatherForecastEnabled(isEnabled: Boolean): Completable

    val desiredWeight: Observable<Double>

    val gender: Observable<Gender>

    val birthday: Observable<DateTime>

    val activityLevel: Observable<ActivityLevel>
}