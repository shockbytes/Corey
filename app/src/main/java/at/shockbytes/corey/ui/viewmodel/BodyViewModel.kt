package at.shockbytes.corey.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import at.shockbytes.core.model.ShockbytesUser
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.info.BodyInfo
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.data.user.UserRepository
import at.shockbytes.corey.ui.fragment.body.weight.WeightHistoryLine
import at.shockbytes.corey.ui.fragment.body.weight.filter.WeightLineFilter
import timber.log.Timber
import javax.inject.Inject

class BodyViewModel @Inject constructor(
    private val bodyRepository: BodyRepository,
    private val userManager: UserRepository,
    private val schedulerFacade: SchedulerFacade,
    private val weightLineFilters: Array<WeightLineFilter>
) : BaseViewModel() {

    sealed class BodyInfoState {

        data class SuccessState(
            val bodyInfo: BodyInfo,
            val user: ShockbytesUser,
            val weightUnit: String,
            val weightLines: List<WeightHistoryLine>
        ) : BodyInfoState()

        data class ErrorState(val throwable: Throwable) : BodyInfoState()
    }

    private val bodyInfo = MutableLiveData<BodyInfoState>()

    fun requestBodyInfo() {
        bodyRepository.bodyInfo
            .subscribeOn(schedulerFacade.io)
            .map { info ->

                val weightLines = buildLinesFromBodyInfo(info)

                BodyInfoState.SuccessState(
                    info,
                    userManager.user,
                    bodyRepository.weightUnit,
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

    private fun buildLinesFromBodyInfo(info: BodyInfo): List<WeightHistoryLine> {
        return weightLineFilters.map { filter ->
            WeightHistoryLine(
                filter.filterNameRes,
                filter.map(info.weightPoints),
                filter.lineColor,
                filter.lineThickness
            )
        }
    }

    fun getBodyInfo(): LiveData<BodyInfoState> = bodyInfo
}