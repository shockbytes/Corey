package at.shockbytes.corey.ui.viewmodel

import at.shockbytes.core.viewmodel.BaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class WearRunningViewModel @Inject constructor(

): BaseViewModel() {

    private val onStartEventSubject = PublishSubject.create<Unit>()
    val onStartEvent: Observable<Unit> = onStartEventSubject

    fun startRun() {
        onStartEventSubject.onNext(Unit)
    }
}