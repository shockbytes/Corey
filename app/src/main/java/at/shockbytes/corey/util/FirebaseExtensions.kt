package at.shockbytes.corey.util

import at.shockbytes.corey.data.FirebaseStorable
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.subjects.Subject

inline fun <reified T> FirebaseDatabase.listen(reference: String, relay: Subject<List<T>>) {

    val cache = mutableListOf<T>()

    this.getReference(reference).addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            dataSnapshot.getValue(T::class.java)?.let { value ->
                cache.add(value)
                relay.onNext(cache)
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            dataSnapshot.getValue(T::class.java)?.let { value ->
                cache[cache.indexOf(value)] = value
                relay.onNext(cache)
            }
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            dataSnapshot.getValue(T::class.java)?.let { value ->
                cache.remove(value)
                relay.onNext(cache)
            }
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) = Unit

        override fun onCancelled(databaseError: DatabaseError) = Unit
    })
}

inline fun <reified T: FirebaseStorable> FirebaseDatabase.insertValue(reference: String, value: T) {

    val ref = getReference(reference).push()
    val id = ref.key ?: throw IllegalStateException("Cannot insert value $value into firebase!")
    ref.setValue(value.copyWithNewId(newId = id))
}

fun FirebaseDatabase.removeValue(reference: String, id: String) {
    getReference(reference).child(id).removeValue()
}