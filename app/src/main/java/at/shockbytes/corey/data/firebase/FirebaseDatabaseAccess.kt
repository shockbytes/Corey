package at.shockbytes.corey.data.firebase

import com.google.firebase.database.DatabaseReference

interface FirebaseDatabaseAccess {

    fun access(reference: String): DatabaseReference
}