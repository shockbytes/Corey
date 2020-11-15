package at.shockbytes.corey.data.goal

import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import at.shockbytes.corey.util.insertValue
import at.shockbytes.corey.util.listen
import at.shockbytes.corey.util.removeChildValue
import at.shockbytes.corey.util.updateValue
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FirebaseGoalsRepository(
    private val firebase: FirebaseDatabaseAccess
) : GoalsRepository {

    private val goalsSubject = BehaviorSubject.create<List<Goal>>()

    init {
        setupFirebase()
    }

    override val goals: Observable<List<Goal>> = goalsSubject

    override fun updateBodyGoal(goal: Goal) {
        firebase.access(REF_GOAL).updateValue(goal.id, goal)
    }

    override fun removeBodyGoal(goal: Goal) {
        firebase.access(REF_GOAL).removeChildValue(goal.id)
    }

    override fun storeBodyGoal(goal: Goal) {
        firebase.access(REF_GOAL).insertValue(goal)
    }

    private fun setupFirebase() {
        firebase.access(REF_GOAL).listen(goalsSubject, changedChildKeySelector = { it.id })
    }

    companion object {
        private const val REF_GOAL = "/goal"
    }
}