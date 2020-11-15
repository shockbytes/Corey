package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup

import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.Sortable
import at.shockbytes.corey.data.schedule.SchedulableItem
import at.shockbytes.util.adapter.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.listitem_add_exercise.*

/**
 * Author:  Martin Macheiner
 * Date:    24.02.2017
 */
class AddScheduleItemAdapter(
    context: Context,
    data: List<ScheduleDisplayItem> = listOf(),
    onItemClickListener: OnItemClickListener<ScheduleDisplayItem>,
    filterPredicate: (ScheduleDisplayItem, String) -> Boolean
) : FilterableBaseAdapter<AddScheduleItemAdapter.ScheduleDisplayItem>(context, data.toMutableList(), onItemClickListener, filterPredicate) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter.ViewHolder<ScheduleDisplayItem> {
        return ViewHolder(inflater.inflate(R.layout.listitem_add_exercise, parent, false))
    }

    internal inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter.ViewHolder<ScheduleDisplayItem>(containerView), LayoutContainer {

        override fun bindToView(t: ScheduleDisplayItem, position: Int) {
            with(t.item) {
                tv_listitem_add_exercise_title.text = title
                iv_listitem_add_exercise_title.apply {
                    workoutType.iconRes?.let { iconRes ->
                        setImageResource(iconRes)
                    }
                    workoutType.iconTint?.let { tintColor ->
                        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, tintColor))
                    }
                }
            }
        }
    }

    data class ScheduleDisplayItem(val item: SchedulableItem) : Sortable
}
