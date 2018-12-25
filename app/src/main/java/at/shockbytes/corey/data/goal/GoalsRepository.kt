package at.shockbytes.corey.data.goal

import io.reactivex.Observable

interface GoalsRepository {

    val bodyGoals: Observable<List<Goal>>

    fun updateBodyGoal(g: Goal)

    fun removeBodyGoal(g: Goal)

    fun storeBodyGoal(g: Goal)
}