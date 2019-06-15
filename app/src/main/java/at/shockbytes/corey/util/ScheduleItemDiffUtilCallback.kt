package at.shockbytes.corey.util

import androidx.recyclerview.widget.DiffUtil
import at.shockbytes.corey.data.schedule.ScheduleItem

/**
 * Author:  Martin Macheiner
 * Date:    12.06.2018
 */
class ScheduleItemDiffUtilCallback(
    private val oldList: List<ScheduleItem>,
    private val newList: List<ScheduleItem>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}