package at.shockbytes.corey.data.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DefaultFirebaseAccess(
    private val firebase: FirebaseDatabase
) : FirebaseDatabaseAccess {

    override fun access(reference: String): DatabaseReference {
        return firebase.getReference(reference)
    }
}