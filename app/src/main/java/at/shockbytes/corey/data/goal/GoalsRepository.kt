package at.shockbytes.corey.data.goal

import io.reactivex.Observable

interface GoalsRepository {

    val goals: Observable<List<Goal>>

    fun updateBodyGoal(goal: Goal)

    fun removeBodyGoal(goal: Goal)

    fun storeBodyGoal(goal: Goal)
}