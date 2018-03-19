package at.shockbytes.corey.common.core.util

import android.support.v7.view.menu.MenuPopupHelper
import android.support.v7.widget.PopupMenu
import android.util.Log
import at.shockbytes.corey.common.core.R
import at.shockbytes.corey.common.core.workout.model.Equipment
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

    fun getImageByEquipment(equipment: Equipment): Int {
        return when (equipment) {
            Equipment.BODYWEIGHT -> R.drawable.ic_equipment_bodyweight
            Equipment.PULLUP_BAR -> R.drawable.ic_equipment_pullup_bar
            Equipment.BAR_BELL -> R.drawable.ic_equipment_bar_bell
            Equipment.GYM -> R.drawable.ic_equipment_gym
        }
    }

    fun tryShowIconsInPopupMenu(menu: PopupMenu, logTag: String = "Corey") {

        try {
            val fieldPopup = menu.javaClass.getDeclaredField("mPopup")
            fieldPopup.isAccessible = true
            val popup = fieldPopup.get(menu) as MenuPopupHelper
            popup.setForceShowIcon(true)
        } catch (e: Exception) {
            Log.d(logTag, "Cannot force to show icons in popupmenu")
        }
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


