package at.shockbytes.corey.util

import at.shockbytes.corey.data.FirebaseStorable
import com.google.firebase.database.*
import io.reactivex.subjects.Subject
import timber.log.Timber

inline fun <reified T, K> FirebaseDatabase.listen(
        reference: String,
        relay: Subject<List<T>>,
        crossinline changedChildKeySelector: (T) -> K
) {

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

                val changedValueSelector = changedChildKeySelector(value)
                val index = cache.indexOfFirst { v ->
                    changedValueSelector == changedChildKeySelector(v)
                }

                if (index > -1) {
                    cache[index] = value
                    relay.onNext(cache)
                } else {
                    Timber.e(IndexOutOfBoundsException("Could not find changed index of value $value"))
                }
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


inline fun <reified T> FirebaseDatabase.listenForValue(
        reference: String,
        childReference: String,
        relay: Subject<T>,
) {

    val fullRef = reference.plus(childReference)
    this.getReference(fullRef).addValueEventListener(object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot
                    .getValue(T::class.java)
                    ?.let(relay::onNext)
        }

        override fun onCancelled(p0: DatabaseError) = Unit
    })
}


inline fun <reified T: FirebaseStorable> FirebaseDatabase.insertValue(reference: String, value: T): T {

    val ref = getReference(reference).push()
    val id = ref.key ?: throw IllegalStateException("Cannot insert value $value into firebase!")

    val updatedValue = value.copyWithNewId(newId = id)
    ref.setValue(updatedValue)

    return updatedValue as T
}


fun <T> FirebaseDatabase.updateValue(reference: String, childId: String, value: T) {
    getReference(reference).child(childId).setValue(value)
}

fun FirebaseDatabase.removeValue(reference: String, id: String) {
    getReference(reference).child(id).removeValue()
}