package at.shockbytes.corey.util

import at.shockbytes.corey.data.firebase.FirebaseStorable
import com.google.firebase.database.*
import io.reactivex.Completable
import io.reactivex.subjects.Subject
import timber.log.Timber

inline fun <reified T, K> Subject<List<T>>.fromFirebase(
        dbRef: DatabaseReference,
        crossinline changedChildKeySelector: (T) -> K,
        noinline cancelHandler: ((DatabaseError) -> Unit)? = null
) {
    dbRef.listen(this, changedChildKeySelector, cancelHandler)
}

inline fun <reified T, K> FirebaseDatabase.listen(
        reference: String,
        relay: Subject<List<T>>,
        crossinline changedChildKeySelector: (T) -> K,
        noinline cancelHandler: ((DatabaseError) -> Unit)? = null
) = this.getReference(reference).listen(relay, changedChildKeySelector, cancelHandler)

inline fun <reified T, K> DatabaseReference.listen(
        relay: Subject<List<T>>,
        crossinline changedChildKeySelector: (T) -> K,
        noinline cancelHandler: ((DatabaseError) -> Unit)? = null
) {

    val cache = mutableListOf<T>()

    this.addChildEventListener(object : ChildEventListener {

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

        override fun onCancelled(databaseError: DatabaseError) {
            cancelHandler?.invoke(databaseError)
        }
    })
}


inline fun <reified T> Subject<T>.fromFirebase(
        dbRef: DatabaseReference,
        noinline errorHandler: ((DatabaseError) -> Unit)? = null
) {
    dbRef.listenForValue(this, errorHandler)
}

inline fun <reified T> FirebaseDatabase.listenForValue(
        reference: String,
        childReference: String,
        relay: Subject<T>,
        noinline errorHandler: ((DatabaseError) -> Unit)? = null
) {

    val fullRef = reference.plus(childReference)
    getReference(fullRef).listenForValue(relay, errorHandler)
}

inline fun <reified T> DatabaseReference.listenForValue(
        relay: Subject<T>,
        noinline errorHandler: ((DatabaseError) -> Unit)? = null
) {

    this.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dataSnapshot
                    .getValue(T::class.java)
                    ?.let(relay::onNext)
        }

        override fun onCancelled(dbError: DatabaseError) {
            errorHandler?.invoke(dbError)
        }
    })
}


inline fun <reified T : FirebaseStorable> FirebaseDatabase.insertValue(
        reference: String,
        value: T
): T {

    val ref = getReference(reference).push()
    val id = ref.key ?: throw IllegalStateException("Cannot insert value $value into firebase!")

    val updatedValue = value.copyWithNewId(newId = id)
    ref.setValue(updatedValue)

    return updatedValue as T
}


fun <T> FirebaseDatabase.updateValue(reference: String, childId: String, value: T) {
    getReference(reference).child(childId).setValue(value)
}

fun FirebaseDatabase.removeChildValue(reference: String, childId: String) {
    getReference(reference).child(childId).removeValue()
}

fun FirebaseDatabase.reactiveRemoveValue(reference: String): Completable {
    return Completable.create { emitter ->
        getReference(reference).removeValue()
                .addOnCompleteListener { emitter.onComplete() }
                .addOnFailureListener { throwable -> emitter.onError(throwable) }
    }
}