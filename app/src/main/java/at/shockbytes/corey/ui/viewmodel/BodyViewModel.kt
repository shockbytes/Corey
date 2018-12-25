package at.shockbytes.corey.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import at.shockbytes.core.model.ShockbytesUser
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.info.BodyInfo
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.data.user.UserRepository
import timber.log.Timber
import javax.inject.Inject

class BodyViewModel @Inject constructor(
        private val bodyRepository: BodyRepository,
        private val userManager: UserRepository
) : BaseViewModel() {

    sealed class BodyInfoState {
        data class SuccessState(val bodyInfo: BodyInfo, val user: ShockbytesUser, val weightUnit: String) : BodyInfoState()
        data class ErrorState(val throwable: Throwable) : BodyInfoState()
    }

    private val bodyInfo = MutableLiveData<BodyInfoState>()

    fun requestBodyInfo() {
        bodyRepository.bodyInfo.subscribe({ info ->
            bodyInfo.postValue(BodyInfoState.SuccessState(info, userManager.user, bodyRepository.weightUnit))
        }) { throwable ->
            Timber.e(throwable)
            bodyInfo.postValue(BodyInfoState.ErrorState(throwable))
        }.addTo(compositeDisposable)
    }

    fun getBodyInfo(): LiveData<BodyInfoState> = bodyInfo

}