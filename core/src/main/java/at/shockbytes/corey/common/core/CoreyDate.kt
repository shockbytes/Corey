package at.shockbytes.corey.common.core

import at.shockbytes.corey.common.core.util.FindClosestDiffable
import com.google.firebase.database.Exclude
import org.joda.time.DateTime

data class CoreyDate(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val weekOfYear: Int = 0
) : FindClosestDiffable {

    @get:Exclude
    val dateTime: DateTime
        get() = DateTime(year, month, day, 0, 0)

    @get:Exclude
    override val diffValue: Double
        get() = dateTime.millis.toDouble()
}
