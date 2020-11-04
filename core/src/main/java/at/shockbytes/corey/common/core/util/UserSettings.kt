package at.shockbytes.corey.common.core.util

import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.CoreyDate
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.common.core.WeightUnit
import io.reactivex.Completable
import io.reactivex.Observable

interface UserSettings {

    val isWeatherForecastEnabled: Observable<Boolean>

    fun setWeatherForecastEnabled(isEnabled: Boolean): Completable

    val gender: Observable<Gender>
    fun synchronizeGender(gender: Gender): Completable

    val birthday: Observable<CoreyDate>
    fun synchronizeBirthdayFromString(birthdayString: String): Completable

    val activityLevel: Observable<ActivityLevel>
    fun synchronizeActivityLevel(level: ActivityLevel): Completable

    val weightUnit: Observable<WeightUnit>
    fun synchronizeWeightUnit(unit: WeightUnit): Completable
}