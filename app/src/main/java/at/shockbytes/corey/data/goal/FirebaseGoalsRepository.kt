package at.shockbytes.corey.data.goal

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FirebaseGoalsRepository(
        private val firebase: FirebaseDatabase
): GoalsRepository {

    init {
        setupFirebase()
    }

    private val goals = mutableListOf<Goal>()

    private val goalsSubject = BehaviorSubject.create<List<Goal>>()

    override val bodyGoals: Observable<List<Goal>> = goalsSubject

    override fun updateBodyGoal(g: Goal) {
        firebase.getReference("/body/goal").child(g.id).setValue(g)
    }

    override fun removeBodyGoal(g: Goal) {
        firebase.getReference("/body/goal").child(g.id).removeValue()
    }

    override fun storeBodyGoal(g: Goal) {
        val ref = firebase.getReference("/body/goal").push()
        g.id = ref.key ?: ""
        ref.setValue(g)
    }

    private fun setupFirebase() {
        firebase.getReference("/body/goal").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                dataSnapshot.getValue(Goal::class.java)?.let { g ->
                    goals.add(g)
                    goalsSubject.onNext(goals)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                dataSnapshot.getValue(Goal::class.java)?.let { g ->
                    goals[goals.indexOf(g)] = g
                    goalsSubject.onNext(goals)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                dataSnapshot.getValue(Goal::class.java)?.let { g ->
                    goals.remove(g)
                    goalsSubject.onNext(goals)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) = Unit

            override fun onCancelled(databaseError: DatabaseError) = Unit
        })
    }

}