package at.shockbytes.corey.ui.model.mapper

import at.shockbytes.corey.R
import at.shockbytes.corey.common.util.JodaDateTimeUtils
import at.shockbytes.corey.data.goal.Goal
import at.shockbytes.corey.ui.model.GoalItem

class GoalMapper : SortedMapper<Goal, GoalItem>() {

    override val mapToSortFunction: Comparator<GoalItem> = compareBy({ it.dueDate }, { it.isCompleted })
    override val mapFromSortFunction: Comparator<Goal> = compareBy({ it.dueDate }, { it.done })

    override fun mapTo(data: Goal): GoalItem {

        val category = GoalItem.resolveCategory(data.category)

        return GoalItem(
                data.message,
                data.done,
                data.id,
                JodaDateTimeUtils.parseMonthAndYearFormat(data.dueDate),
                JodaDateTimeUtils.formatMonthAndYearString(data.dueDate),
                category,
                resolveCatgoryString(category),
                resolveCategoryIcon(category)
        )
    }

    override fun mapFrom(data: GoalItem): Goal {
        return Goal(
                data.message,
                data.isCompleted,
                data.id,
                GoalItem.categoryToString(data.category),
                JodaDateTimeUtils.toMonthAndYearFormattedString(data.dueDate)
        )
    }

    private fun resolveCatgoryString(category: GoalItem.Category): Int {
        return when (category) {
            GoalItem.Category.BODY_FAT -> R.string.goals_body_fat
            GoalItem.Category.WEIGHT -> R.string.goals_weight
            GoalItem.Category.EXERCISE -> R.string.goals_exercise
            GoalItem.Category.RUNNING -> R.string.goals_running
            GoalItem.Category.OTHER -> R.string.goals_other
        }
    }

    private fun resolveCategoryIcon(category: GoalItem.Category): Int {
        return when (category) {
            GoalItem.Category.BODY_FAT -> R.drawable.ic_goal_body_fat
            GoalItem.Category.WEIGHT -> R.drawable.ic_goal_weight
            GoalItem.Category.EXERCISE -> R.drawable.ic_goal_exercise
            GoalItem.Category.RUNNING -> R.drawable.ic_goal_running
            GoalItem.Category.OTHER -> R.drawable.ic_goal_exercise
        }
    }
}