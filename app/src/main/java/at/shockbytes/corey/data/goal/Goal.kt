package at.shockbytes.corey.data.goal

import at.shockbytes.corey.data.firebase.FirebaseStorable

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2017
 */
data class Goal(
    val message: String = "",
    val done: Boolean = false,
    val id: String = "",
    val category: String = "",
    val dueDate: String = ""
) : FirebaseStorable {

    override fun copyWithNewId(newId: String): FirebaseStorable {
        return copy(id = newId)
    }
}