package at.shockbytes.corey.data.nutrition

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.common.core.CoreyDate
import at.shockbytes.corey.common.isListNotEmpty
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.bmr.Bmr
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.body.model.User
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import at.shockbytes.corey.data.workout.external.ExternalWorkout
import at.shockbytes.corey.data.workout.external.ExternalWorkoutRepository
import at.shockbytes.corey.util.completableEmitterOf
import at.shockbytes.corey.util.findClosest
import at.shockbytes.corey.util.insertValue
import at.shockbytes.corey.util.listen
import at.shockbytes.corey.util.removeChildValue
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.joda.time.Years
import timber.log.Timber

class FirebaseNutritionRepository(
    private val firebase: FirebaseDatabaseAccess,
    private val schedulers: SchedulerFacade,
    private val externalWorkoutRepository: ExternalWorkoutRepository,
    private val bodyRepository: BodyRepository,
    private val bmrComputation: BmrComputation
) : NutritionRepository {

    private val nutritionFirebaseSubject = BehaviorSubject.create<List<NutritionEntry>>()
    private val nutritionCacheSubject = BehaviorSubject.create<List<NutritionPerDay>>()

    init {
        setupFirebase()
    }

    private fun setupFirebase() {
        firebase.access(REF).listen(nutritionFirebaseSubject, changedChildKeySelector = { it.id })
    }

    override fun computeCurrentBmr(): Observable<Bmr> {
        return buildEnergyBalanceObservable()
            .map { (_, _, user) -> computeBmr(user) }
    }

    override fun prefetchNutritionHistory(): Observable<*> {
        return buildEnergyBalanceObservable()
            .map(::energyBalanceToNutritionPerDayItems)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
            .filter(::isListNotEmpty)
            .doOnNext(nutritionCacheSubject::onNext)
            .doOnNext { data ->
                Timber.d("Pre-fetched nutrition history with ${data.size} items.")
            }
    }

    override fun loadNutritionHistory(): Observable<List<NutritionPerDay>> = nutritionCacheSubject

    private data class EnergyBalance(
        val nutritionPerDay: List<NutritionEntry>,
        val externalWorkouts: List<ExternalWorkout>,
        val user: User
    )

    private fun buildEnergyBalanceObservable(): Observable<EnergyBalance> {
        return Observable
            .combineLatest(
                nutritionFirebaseSubject,
                externalWorkoutRepository.loadExternalWorkouts(),
                bodyRepository.user,
                { nutritionPerDay, externalWorkouts, user ->
                    EnergyBalance(nutritionPerDay, externalWorkouts, user)
                }
            )
    }

    private fun energyBalanceToNutritionPerDayItems(
        energyBalance: EnergyBalance
    ): List<NutritionPerDay> {
        val (nutrition, externalWorkouts, user) = energyBalance

        return nutrition
            .groupBy { it.date }
            .map { (date, nutritionEntries) ->
                NutritionPerDay(
                    intake = nutritionEntries,
                    date = date,
                    burned = computePhysicalActivityOfDate(date, user, externalWorkouts)
                )
            }
            .sortedByDescending { it.date.dateTime }
    }

    private fun computePhysicalActivityOfDate(
        date: CoreyDate,
        user: User,
        externalWorkouts: List<ExternalWorkout>
    ): List<PhysicalActivity> {
        return listOf(computeBmrPhysicalActivity(user, date)) + computeExternalActivity(date, externalWorkouts)
    }

    private fun computeBmrPhysicalActivity(
        user: User,
        date: CoreyDate
    ): PhysicalActivity.BasalMetabolicRate {

        val userWeightOfDate = user.retrieveUserWeightAt(date)
        val userAgeOfDate = user.retrieveAgeAt(date)

        return computeBmr(user, userWeightOfDate, userAgeOfDate)
            .let(PhysicalActivity::BasalMetabolicRate)
    }

    private fun computeBmr(
        user: User,
        userWeight: Double = user.currentWeight,
        userAge: Int = user.age
    ): Bmr {
        return bmrComputation
            .compute(
                user.gender,
                userWeight,
                user.height,
                userAge,
                user.activityLevel
            )
    }

    private fun User.retrieveUserWeightAt(date: CoreyDate): Double {

        val currentWeight = latestWeightDataPoint
        return if (currentWeight != null) {
            weightDataPoints.findClosest(date, currentWeight).weight
        } else 0.0
    }

    private fun User.retrieveAgeAt(date: CoreyDate): Int {
        return Years.yearsBetween(birthday.dateTime, date.dateTime).years
    }

    private fun computeExternalActivity(
        date: CoreyDate,
        externalWorkouts: List<ExternalWorkout>
    ): List<PhysicalActivity.Activity> {
        return externalWorkouts
            .filter { it.date == date }
            .map { externalWorkout ->
                PhysicalActivity.Activity(externalWorkout.name, externalWorkout.burnedEnergy)
            }
    }

    override fun addNutritionEntry(entry: NutritionEntry): Completable {
        return completableEmitterOf {
            firebase.access(REF).insertValue(entry)
        }
    }

    override fun deleteNutritionEntry(id: String): Completable {
        return Completable.fromAction {
            firebase.access(REF).removeChildValue(id)
        }
    }

    companion object {
        private const val REF = "/nutrition"
    }
}