package at.shockbytes.corey.common.core.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import at.shockbytes.corey.common.core.R
import at.shockbytes.corey.common.core.workout.model.Equipment
import org.joda.time.LocalDate
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Author:  Martin Macheiner
 * Date:    27.10.2015
 */
object CoreyUtils {

    @SuppressLint("ConstantLocale")
    private val SDF_DATE = SimpleDateFormat("dd.MM.", Locale.getDefault())
    @SuppressLint("ConstantLocale")
    private val SDF_DATE_W_YEAR = SimpleDateFormat("MMM yy", Locale.getDefault())

    fun getDayOfWeek(): Int {
        return LocalDate.now().dayOfWeek - 1
    }

    fun formatDate(date: Long, yearFormat: Boolean): String {
        return if (yearFormat) SDF_DATE_W_YEAR.format(Date(date)) else SDF_DATE.format(Date(date))
    }

    fun getLocalizedDayOfWeek(context: Context): String {
        return context.resources.getStringArray(R.array.daysFull)[getDayOfWeek()]
    }

    fun getImageByEquipment(equipment: Equipment): Int {
        return when (equipment) {
            Equipment.BODYWEIGHT -> R.drawable.ic_equipment_bodyweight
            Equipment.PULLUP_BAR -> R.drawable.ic_equipment_pullup_bar
            Equipment.BAR_BELL -> R.drawable.ic_equipment_bar_bell
            Equipment.GYM -> R.drawable.ic_equipment_gym
        }
    }

    fun tryShowIconsInPopupMenu(menu: PopupMenu) {

        try {
            val fieldPopup = menu.javaClass.getDeclaredField("mPopup")
            fieldPopup.isAccessible = true
            val popup = fieldPopup.get(menu) as MenuPopupHelper
            popup.setForceShowIcon(true)
        } catch (e: Exception) {
            Timber.d("Cannot force to show icons in popupmenu")
        }
    }

    fun calculateDreamWeightProgress(
        startWeight: Double,
        weight: Double,
        dreamWeight: Double
    ): Int {

        if (weight <= dreamWeight) {
            return 100
        }

        val diff = Math.max(startWeight, weight) - dreamWeight
        val weightAligned = weight - dreamWeight
        return 100 - Math.round(100 / diff * weightAligned).toInt()
    }
}
