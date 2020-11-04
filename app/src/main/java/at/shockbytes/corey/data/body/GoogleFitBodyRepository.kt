package at.shockbytes.corey.data.body

import android.content.SharedPreferences
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.ActivityLevel
import at.shockbytes.corey.common.core.Gender
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.common.core.CoreyDate
import at.shockbytes.corey.data.body.model.User
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import at.shockbytes.corey.data.google.CoreyGoogleApiClient
import at.shockbytes.corey.util.fromFirebase
import at.shockbytes.corey.util.updateValue
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Author:  Martin Macheiner
 * Date:    04.08.2016
 */
class GoogleFitBodyRepository(
    private val coreyGoogleApiClient: CoreyGoogleApiClient,
    private val preferences: SharedPreferences,
    private val firebase: FirebaseDatabaseAccess,
    private val userSettings: UserSettings
) : BodyRepository {

    private val compositeDisposable = CompositeDisposable()

    private val userBodySubject = BehaviorSubject.create<User>()

    private val desiredWeightSubject: BehaviorSubject<Int> = BehaviorSubject
        .createDefault(preferences.getInt(PREF_DREAM_WEIGHT, 0))

    init {
        setupFirebase()
        setupApiClientCallback()
    }

    private fun setupFirebase() {
        desiredWeightSubject.fromFirebase(firebase.access(REF_USER.plus(REF_DESIRED)))
    }

    override val desiredWeight: Observable<Int>
        get() = desiredWeightSubject
            .doOnNext { fbDesiredWeight ->
                preferences.edit().putInt(PREF_DREAM_WEIGHT, fbDesiredWeight).apply()
            }

    override fun setDesiredWeight(desiredWeight: Int) {
        firebase.access(REF_USER).updateValue(REF_DESIRED, desiredWeight)
    }

    override fun cleanUp() {
        compositeDisposable.clear()
    }

    override val user: Observable<User> = userBodySubject

    override val currentWeight: Observable<Double>
        get() = user
            .map { bodyInfo ->
                bodyInfo.latestWeightDataPoint?.weight ?: 0.0
            }

    private data class UserMetadata(
        val gender: Gender,
        val birthday: CoreyDate,
        val desiredWeight: Int,
        val activityLevel: ActivityLevel
    )

    private fun setupApiClientCallback() {
        coreyGoogleApiClient.onConnectionEvent()
            .filter { isConnected -> isConnected }
            .flatMap { loadUserFromGoogleFit() }
            .subscribe(userBodySubject::onNext, Timber::e)
            .addTo(compositeDisposable)
    }

    private fun loadUserFromGoogleFit(): Observable<User> {
        return Observable
            .combineLatest(
                coreyGoogleApiClient.loadGoogleFitUserData(),
                gatherUserMetadata(),
                { weightHeightPair, userMetadata ->
                    val (weightDataPoints, height) = weightHeightPair
                    val (gender, age, dw, activityLevel) = userMetadata

                    User(
                        weightDataPoints,
                        height,
                        gender,
                        age,
                        dw,
                        activityLevel
                    )
                }
            )
    }

    private fun gatherUserMetadata(): Observable<UserMetadata> {
        return Observable
            .combineLatest(
                userSettings.gender,
                userSettings.birthday,
                desiredWeight,
                userSettings.activityLevel,
                { gender, birthDay, desiredWeight, activityLevel ->
                    UserMetadata(gender, birthDay, desiredWeight, activityLevel)
                }
            )
    }

    companion object {

        private const val PREF_DREAM_WEIGHT = "desired_weight"

        private const val REF_USER = "/user"
        private const val REF_DESIRED = "/desired_weight"
    }
}
