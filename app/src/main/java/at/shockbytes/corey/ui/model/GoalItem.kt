package at.shockbytes.corey.ui.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import org.joda.time.DateTime

data class GoalItem(
    val message: String,
    val isCompleted: Boolean,
    val id: String,
    val dueDate: DateTime,
    val dueDateFormatted: String,
    val category: Category,
    @StringRes val categoryString: Int,
    @DrawableRes val categoryIcon: Int
) {

    enum class Category {
        BODY_FAT, WEIGHT, EXERCISE, RUNNING, OTHER
    }

    companion object {

        fun resolveCategory(category: String): Category {
            return when (category) {
                "body_fat" -> Category.BODY_FAT
                "weight" -> Category.WEIGHT
                "exercise" -> Category.EXERCISE
                "running" -> Category.RUNNING
                else -> Category.OTHER
            }
        }

        fun categoryToString(category: Category): String {
            return when (category) {
                Category.BODY_FAT -> "body_fat"
                Category.WEIGHT -> "weight"
                Category.EXERCISE -> "exercise"
                Category.RUNNING -> "running"
                Category.OTHER -> "other"
            }
        }
    }
}