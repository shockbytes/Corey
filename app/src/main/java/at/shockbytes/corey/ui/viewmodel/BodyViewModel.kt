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
    private val userSettings: UserSettings
) : BaseViewModel() {

    sealed class BodyInfoState {

        data class SuccessState(
                val userBody: User,
                val user: ShockbytesUser,
                val weightUnit: String,
                val weightLines: List<WeightHistoryLine>
        ) : BodyInfoState()

        data class ErrorState(val throwable: Throwable) : BodyInfoState()
    }

    private val bodyInfo = MutableLiveData<BodyInfoState>()

    fun requestBodyInfo() {
        buildUserData()
            .subscribeOn(schedulerFacade.io)
            .map { (user, weightUnit) ->

                val weightLines = buildLinesFromUser(user)

                BodyInfoState.SuccessState(
                    user,
                    userManager.user,
                    weightUnit.acronym,
                    weightLines
                )
            }
            .subscribe({ successState ->
                bodyInfo.postValue(successState)
            }) { throwable ->
                Timber.e(throwable)
                bodyInfo.postValue(BodyInfoState.ErrorState(throwable))
            }
            .addTo(compositeDisposable)
    }

    private fun buildUserData(): Observable<Pair<User, WeightUnit>> {
        return Observable.zip(
                bodyRepository.user,
                userSettings.weightUnit,
                { user, weightUnit -> user to weightUnit }
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

    fun getBodyInfo(): LiveData<BodyInfoState> = bodyInfo
}