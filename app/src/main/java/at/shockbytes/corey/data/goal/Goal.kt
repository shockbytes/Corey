package at.shockbytes.corey.data.goal

/**
 * Author:  Martin Macheiner
 * Date:    05.03.2017
 */
data class Goal(
    val message: String = "",
    val done: Boolean = false,
    var id: String = "",
    val category: String = "",
    val dueDate: String = ""
) {

    override fun equals(other: Any?): Boolean {
        return ((other as? Goal)?.id == id)
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + done.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + dueDate.hashCode()
        return result
    }
}