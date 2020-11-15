package at.shockbytes.corey.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserScopedFirebaseDatabaseAccess(
    private val firebase: FirebaseDatabase,
    private val auth: FirebaseAuth
) : FirebaseDatabaseAccess {

    override fun access(reference: String): DatabaseReference {

        val uid = auth.currentUser?.uid
            ?: error("User is currently not logged into Firebase! Trying to access $reference")

        val checkedReference = checkReference(reference)
        val childRef = "$ROOT_REF/$uid/$checkedReference"

        return firebase.getReference(childRef)
    }

    private fun checkReference(reference: String): String {
        return if (reference.startsWith("/")) {
            reference.substring(1, reference.length)
        } else {
            reference
        }
    }

    companion object {

        private const val ROOT_REF = "/data"
    }
}