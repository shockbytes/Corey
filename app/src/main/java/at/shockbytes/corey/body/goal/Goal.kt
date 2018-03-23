package at.shockbytes.corey.body.goal

/**
 * @author  Martin Macheiner
 * Date:    05.03.2017.
 */

data class Goal(var message: String = "",
                var isDone: Boolean = false,
                var id: String = "") {

    override fun equals(other: Any?): Boolean {
        return (other as? Goal)?.id?.equals(id) ?: false
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + isDone.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

}