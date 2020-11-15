package at.shockbytes.corey.util

import android.content.Context
import android.content.SharedPreferences
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.common.core.WeightUnit
import at.shockbytes.corey.common.core.CoreyDate
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.DateTime

/**
 * Firebase online backend with local shared preferences storage to integrate with settings screen.
 */
class ReactiveFirebaseUserSettings(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val firebase: FirebaseDatabaseAccess
) : UserSettings {

    private val birthdaySubject = BehaviorSubject.create<String>()
    private val genderSubject = BehaviorSubject.create<String>()
    private val activityLevelSubject = BehaviorSubject.create<Int>()
    private val weightUnitSubject = BehaviorSubject.create<String>()

    init {
        firebase.access(REF_USER + BIRTHDAY).listenForValue(birthdaySubject)
        firebase.access(REF_USER + GENDER).listenForValue(genderSubject)
        firebase.access(REF_USER + ACTIVITY_LEVEL).listenForValue(activityLevelSubject)
        firebase.access(REF_SETTINGS + WEIGHT_UNIT).listenForValue(weightUnitSubject)
    }

    override val isWeatherForecastEnabled: Observable<Boolean>
        get() = Observable.just(sharedPreferences.getBoolean(context.getString(R.string.prefs_schedule_weather_forecast_key), true))

    override fun setWeatherForecastEnabled(isEnabled: Boolean): Completable {
        return Completable.merge(
                listOf(
                        completableOf {
                            firebase.access(REF_SETTINGS).updateValue(WEATHER, isEnabled)
                        },
                        completableOf {
                            sharedPreferences.edit()
                                    .putBoolean(
                                            context.getString(R.string.prefs_schedule_weather_forecast_key),
                                            isEnabled
                                    )
                                    .apply()
                        }
                )
        )
    }

    override val gender: Observable<Gender>
        get() = genderSubject.map(Gender.Companion::of)

    override fun synchronizeGender(gender: Gender): Completable {
        return completableOf {
            firebase.access(REF_USER).updateValue(GENDER, gender.acronym)
        }
    }

    override val birthday: Observable<CoreyDate>
        get() = birthdaySubject.map { birthdayAsString ->

            val (day, month, year) = birthdayAsString
                    .split(".")
                    .map { it.toInt() }

            DateTime(year, day, month, 0, 0).toCoreyDate()
        }

    override fun synchronizeBirthdayFromString(birthdayString: String): Completable {
        return completableOf {
            firebase.access(REF_USER).updateValue(BIRTHDAY, birthdayString)
        }
    }

    override val activityLevel: Observable<ActivityLevel>
        get() = activityLevelSubject.map(ActivityLevel.Companion::ofLevel)

    override fun synchronizeActivityLevel(level: ActivityLevel): Completable {
        return completableOf {
            firebase.access(REF_USER).updateValue(ACTIVITY_LEVEL, level.level)
        }
    }

    override val weightUnit: Observable<WeightUnit>
        get() = weightUnitSubject.map(WeightUnit.Companion::of)

    override fun synchronizeWeightUnit(unit: WeightUnit): Completable {
        return completableOf {
            firebase.access(REF_SETTINGS).updateValue(WEIGHT_UNIT, unit.acronym)
        }
    }

    companion object {

        private const val REF_SETTINGS = "/settings"
        private const val WEATHER = "/weather_sync"
        private const val WEIGHT_UNIT = "/weight_unit"

        private const val REF_USER = "/user"

        private const val BIRTHDAY = "/birthday"
        private const val GENDER = "/gender"
        private const val ACTIVITY_LEVEL = "/activity_level"
    }
}