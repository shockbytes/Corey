package at.shockbytes.corey.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.data.goal.GoalsRepository
import at.shockbytes.corey.ui.model.GoalItem
import at.shockbytes.corey.ui.model.mapper.GoalMapper
import javax.inject.Inject

class GoalsViewModel @Inject constructor(
        private val goalsRepository: GoalsRepository,
        private val schedulerFacade: SchedulerFacade
) : BaseViewModel() {

    private val goalMapper = GoalMapper()

    private val bodyGoals = MutableLiveData<List<GoalItem>>()

    fun requestGoals() {
        goalsRepository.bodyGoals
                .observeOn(schedulerFacade.ui)
                .subscribe { goals ->
                    bodyGoals.postValue(goalMapper.mapTo(goals))
                }.addTo(compositeDisposable)
    }

    fun getBodyGoals(): LiveData<List<GoalItem>> = bodyGoals

    fun deleteGoal(goal: GoalItem) {
        goalsRepository.removeBodyGoal(goalMapper.mapFrom(goal))
    }

    fun setGoalFinished(goal: GoalItem) {
        goalsRepository.updateBodyGoal(goalMapper.mapFrom(goal).copy(done = true))
    }

}