package at.shockbytes.corey.util

import android.content.Context
import android.content.SharedPreferences
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.common.core.util.CoreySettings
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.DateTime

/**
 * Firebase online backend with local shared preferences storage to integrate with settings screen.
 *
 * // TODO Synchronize changes with Firebase!
 */
class ReactiveFirebaseCoreySettings(
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val firebase: FirebaseDatabase
) : CoreySettings {

    private val birthdaySubject = BehaviorSubject.create<String>()
    private val genderSubject = BehaviorSubject.create<String>()
    private val activityLevelSubject = BehaviorSubject.create<Int>()

    init {
        firebase.listenForValue(REF_SETTINGS, BIRTHDAY, birthdaySubject)
        firebase.listenForValue(REF_SETTINGS, GENDER, genderSubject)
        firebase.listenForValue(REF_SETTINGS, ACTIVITY_LEVEL, activityLevelSubject)
    }

    override val isWeatherForecastEnabled: Observable<Boolean>
        get() = Observable.just(sharedPreferences.getBoolean(context.getString(R.string.prefs_schedule_weather_forecast_key), true))

    override fun setWeatherForecastEnabled(isEnabled: Boolean): Completable {
        return Completable.merge(
                listOf(
                        completableOf {
                            firebase.updateValue(REF_SETTINGS, WEATHER, isEnabled)
                        },
                        completableOf {
                            sharedPreferences.edit()
                                    .putBoolean(
                                            context.getString(R.string.prefs_schedule_weather_forecast_key),
                                            isEnabled
                                    )
                                    .apply()
                        },
                )
        )
    }

    override val desiredWeight: Observable<Double>
        get() = TODO("Not yet implemented")

    override val gender: Observable<Gender>
        get() = genderSubject.map(Gender.Companion::of)

    override val birthday: Observable<DateTime>
        get() = birthdaySubject.map { birthdayAsString ->
            DateTime.now() // TODO
        }

    override val activityLevel: Observable<ActivityLevel>
        get() = activityLevelSubject.map(ActivityLevel.Companion::ofLevel)

    companion object {

        private const val REF_SETTINGS = "/settings"
        private const val WEATHER = "/weather_sync"
        private const val BIRTHDAY = "/birthday"
        private const val GENDER = "/gender"
        private const val ACTIVITY_LEVEL = "/activity_level"
    }
}