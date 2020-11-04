package at.shockbytes.corey.data.firebase

interface FirebaseStorable {

    fun copyWithNewId(newId: String): FirebaseStorable
}