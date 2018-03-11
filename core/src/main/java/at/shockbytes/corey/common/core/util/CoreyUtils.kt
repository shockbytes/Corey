package at.shockbytes.corey.common.core.util

import org.joda.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Martin Macheiner
 * Date: 27.10.2015.
 */
object CoreyUtils {

    private val SDF_DATE = SimpleDateFormat("dd.MM.", Locale.getDefault())
    private val SDF_DATE_W_YEAR = SimpleDateFormat("MMM yy", Locale.getDefault())


    fun getDayOfWeek(): Int {
        return LocalDate.now().dayOfWeek - 1
    }

    fun formatDate(date: Long, yearFormat: Boolean): String {
        return if (yearFormat) SDF_DATE_W_YEAR.format(Date(date)) else SDF_DATE.format(Date(date))
    }


    fun calculateDreamWeightProgress(startWeight: Double,
                                     weight: Double,
                                     dreamWeight: Double): Int {

        if (weight <= dreamWeight) {
            return 100
        }

        val diff = Math.max(startWeight, weight) - dreamWeight
        val weightAligned = weight - dreamWeight
        return 100 - Math.round(100 / diff * weightAligned).toInt()
    }
}


