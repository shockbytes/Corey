package at.shockbytes.corey.data

interface FirebaseStorable {

    fun copyWithNewId(newId: String): FirebaseStorable
}