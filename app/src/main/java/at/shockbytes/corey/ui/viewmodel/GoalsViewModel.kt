package at.shockbytes.corey.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.data.goal.GoalsRepository
import javax.inject.Inject

class GoalsViewModel @Inject constructor(
        private val goalsRepository: GoalsRepository,
        private val schedulerFacade: SchedulerFacade
) : BaseViewModel() {

    private val bodyGoals = MutableLiveData<List<Goal>>()

    fun requestGoals() {
        goalsRepository.bodyGoals
                .observeOn(schedulerFacade.ui)
                .subscribe { goals ->
                    bodyGoals.postValue(goals)
                }.addTo(compositeDisposable)
    }

    fun getBodyGoals(): LiveData<List<Goal>> = bodyGoals

    fun deleteGoal(goal: Goal) {
        goalsRepository.removeBodyGoal(goal)
    }

    fun setGoalFinished(goal: Goal) {
        goalsRepository.updateBodyGoal(goal.copy(done = true))
    }

}