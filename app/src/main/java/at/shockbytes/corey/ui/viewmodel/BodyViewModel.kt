package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.model.ShockbytesUser
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.model.User
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.WeightUnit
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.data.body.bmr.Bmr
import at.shockbytes.corey.data.nutrition.NutritionRepository
import at.shockbytes.corey.data.user.UserRepository
import at.shockbytes.corey.ui.fragment.body.weight.WeightHistoryLine
import at.shockbytes.corey.ui.fragment.body.weight.filter.WeightLineFilter
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class BodyViewModel @Inject constructor(
    private val bodyRepository: BodyRepository,
    private val userManager: UserRepository,
    private val schedulerFacade: SchedulerFacade,
    private val weightLineFilters: Array<WeightLineFilter>,
    private val userSettings: UserSettings,
    private val nutritionRepository: NutritionRepository
) : BaseViewModel() {

    sealed class BodyState {

        data class SuccessState(
                val userBody: User,
                val user: ShockbytesUser,
                val weightUnit: String,
                val weightLines: List<WeightHistoryLine>,
                val bmr: Bmr,
        ) : BodyState()

        data class ErrorState(val throwable: Throwable) : BodyState()
    }

    private val body = MutableLiveData<BodyState>()

    fun requestBodyInfo() {
        buildUserData()
            .subscribeOn(schedulerFacade.io)
            .map { (user, weightUnit, bmr) ->

                val weightLines = buildLinesFromUser(user)

                BodyState.SuccessState(
                    user,
                    userManager.user,
                    weightUnit.acronym,
                    weightLines,
                    bmr
                )
            }
            .subscribe({ successState ->
                body.postValue(successState)
            }) { throwable ->
                Timber.e(throwable)
                body.postValue(BodyState.ErrorState(throwable))
            }
            .addTo(compositeDisposable)
    }

    private fun buildUserData(): Observable<Triple<User, WeightUnit, Bmr>> {
        return Observable.zip(
                bodyRepository.user,
                userSettings.weightUnit,
                nutritionRepository.computeCurrentBmr(),
                { user, weightUnit, bmr ->
                    Triple(user, weightUnit, bmr)
                }
        )
    }

    private fun buildLinesFromUser(user: User): List<WeightHistoryLine> {
        return weightLineFilters.map { filter ->
            WeightHistoryLine(
                filter.filterNameRes,
                filter.map(user.weightDataPoints),
                filter.lineColor,
                filter.lineThickness
            )
        }
    }

    fun getBodyInfo(): LiveData<BodyState> = body
}