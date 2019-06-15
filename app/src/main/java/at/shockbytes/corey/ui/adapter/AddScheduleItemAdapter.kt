package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup

import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.Sortable
import at.shockbytes.corey.data.schedule.SchedulableItem
import at.shockbytes.util.adapter.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.listitem_add_exercise.*
import timber.log.Timber

/**
 * Author:  Martin Macheiner
 * Date:    24.02.2017
 */
class AddScheduleItemAdapter(
    context: Context,
    data: List<ScheduleDisplayItem> = listOf(),
    filterPredicate: (ScheduleDisplayItem, String) -> Boolean
) : FilterableBaseAdapter<AddScheduleItemAdapter.ScheduleDisplayItem>(context, data.toMutableList(), filterPredicate) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<ScheduleDisplayItem>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.listitem_add_exercise, parent, false))
    }

    internal inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter<ScheduleDisplayItem>.ViewHolder(containerView), LayoutContainer {

        override fun bindToView(t: ScheduleDisplayItem) {
            with(t.item) {
                tv_listitem_add_exercise_title.text = title
                iv_listitem_add_exercise_title.apply {
                    Timber.d("$title - $workoutType")
                    setImageResource(workoutType.iconRes)
                    workoutType.iconTint?.let { tintColor ->
                        imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, tintColor))
                    }
                }
            }
        }
    }

    data class ScheduleDisplayItem(val item: SchedulableItem) : Sortable
}
