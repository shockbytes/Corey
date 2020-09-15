package at.shockbytes.corey.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.core.viewmodel.BaseViewModel
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.data.goal.GoalsRepository
import at.shockbytes.corey.storage.KeyValueStorage
import at.shockbytes.corey.storage.StorageConstants
import at.shockbytes.corey.ui.model.GoalItem
import at.shockbytes.corey.ui.model.mapper.GoalMapper
import javax.inject.Inject

class GoalsViewModel @Inject constructor(
    private val goalsRepository: GoalsRepository,
    private val schedulerFacade: SchedulerFacade,
    private val localStorage: KeyValueStorage
) : BaseViewModel() {

    private val goalMapper = GoalMapper()

    private var hideFinished: Boolean = false
    private val goals = mutableListOf<GoalItem>()

    private val bodyGoals = MutableLiveData<List<GoalItem>>()
    private val hideFinishedGoals = MutableLiveData<Boolean>()

    fun requestGoals() {

        hideFinished = localStorage.getBoolean(StorageConstants.KEY_HIDE_FINISHED_GOALS)
        hideFinishedGoals.postValue(hideFinished)

        goalsRepository.goals
                .subscribeOn(schedulerFacade.io)
                .observeOn(schedulerFacade.ui)
                .subscribe { g ->

                    goals.clear()
                    goals.addAll(goalMapper.mapTo(g))

                    postGoals()
                }.addTo(compositeDisposable)
    }

    fun getBodyGoals(): LiveData<List<GoalItem>> = bodyGoals

    fun selectHideFinishedGoals(): LiveData<Boolean> = hideFinishedGoals

    fun deleteGoal(goal: GoalItem) {
        goalsRepository.removeBodyGoal(goalMapper.mapFrom(goal))
    }

    fun setGoalFinished(goal: GoalItem) {
        goalsRepository.updateBodyGoal(goalMapper.mapFrom(goal).copy(done = true))
    }

    fun showFinishedGoals(hideFinishedGoals: Boolean) {
        this.hideFinished = hideFinishedGoals
        localStorage.putBoolean(hideFinishedGoals, StorageConstants.KEY_HIDE_FINISHED_GOALS)

        postGoals()
    }

    private fun postGoals() {
        val postGoals = if (hideFinished) { goals.filter { !it.isCompleted } } else goals
        bodyGoals.postValue(postGoals)
    }

    fun storeBodyGoal(goal: Goal) {
        goalsRepository.storeBodyGoal(goal)
    }
}