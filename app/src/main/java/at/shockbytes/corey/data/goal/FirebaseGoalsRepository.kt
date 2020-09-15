package at.shockbytes.corey.data.goal

import at.shockbytes.corey.util.insertValue
import at.shockbytes.corey.util.listen
import at.shockbytes.corey.util.removeValue
import at.shockbytes.corey.util.updateValue
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FirebaseGoalsRepository(
    private val firebase: FirebaseDatabase
) : GoalsRepository {

    private val goalsSubject = BehaviorSubject.create<List<Goal>>()

    init {
        setupFirebase()
    }

    override val goals: Observable<List<Goal>> = goalsSubject

    override fun updateBodyGoal(goal: Goal) {
        firebase.updateValue(REF_GOAL, goal.id, goal)
    }

    override fun removeBodyGoal(goal: Goal) {
        firebase.removeValue(REF_GOAL, goal.id)
    }

    override fun storeBodyGoal(goal: Goal) {
        firebase.insertValue(REF_GOAL, goal)
    }

    private fun setupFirebase() {
        firebase.listen(REF_GOAL, goalsSubject, changedChildKeySelector = { it.id })
    }

    companion object {
        private const val REF_GOAL = "/goal"
    }
}