package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.util.adapter.ItemTouchHelperAdapter
import kotterknife.bindView
import java.util.Collections

/**
 * Author:  Martin Macheiner
 * Date:    02.12.2015
 */
class ScheduleAdapter(
    context: Context,
    data: List<ScheduleItem>
) : BaseAdapter<ScheduleItem>(context, data.toMutableList()), ItemTouchHelperAdapter {

    private var onScheduleItemSelectedListener: ((item: ScheduleItem, v: View, position: Int) -> Unit)? = null
    private var onScheduleItemDismissedListener: ((item: ScheduleItem, position: Int) -> Unit)? = null

    override var data: MutableList<ScheduleItem>
        get() = super.data
        set(value) {
            for (i in data.size - 1 downTo 0) {
                deleteEntity(i)
            }
            fillUpScheduleList(value).forEach { addEntityAtLast(it) }
        }

    // ----------------------------------------------------------------------
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseAdapter<ScheduleItem>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_schedule, parent, false))
    }

    override fun onBindViewHolder(holder: BaseAdapter<ScheduleItem>.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? ViewHolder)?.bind(data[position], position)
    }

    override fun onItemMove(from: Int, to: Int): Boolean {
        if (from < to) {
            (from until to).forEach { Collections.swap(data, it, it + 1) }
        } else {
            (from downTo to + 1).forEach { Collections.swap(data, it, it - 1) }
        }
        notifyItemMoved(from, to)
        onItemMoveListener?.onItemMove(data[from], from, to)
        return true
    }

    override fun onItemMoveFinished() {
        onItemMoveListener?.onItemMoveFinished()
    }

    override fun onItemDismiss(position: Int) {
        val entry = data.removeAt(position)
        if (!entry.isEmpty) {
            onItemMoveListener?.onItemDismissed(entry, entry.day)
        }
        notifyItemRemoved(position)
        addEntity(position, ScheduleItem("", position))
    }

    fun setOnScheduleItemSelectedListener(listener: (item: ScheduleItem, v: View, position: Int) -> Unit) {
        onScheduleItemSelectedListener = listener
    }

    fun setOnScheduleItemDismissedListener(listener: (item: ScheduleItem, position: Int) -> Unit) {
        onScheduleItemDismissedListener = listener
    }

    // -----------------------------Data Section-----------------------------
    fun insertScheduleItem(item: ScheduleItem) {
        val location = item.day
        if (location >= 0) {
            data[location] = item
            notifyItemChanged(location)
        }
    }

    fun updateScheduleItem(item: ScheduleItem) {

        val oldLocation = getLocation(item)
        val newLocation = item.day
        if (newLocation >= 0 && oldLocation != newLocation) {
            val newLocationItem = data[newLocation]
            data[newLocation] = item
            data[oldLocation] = newLocationItem
            notifyItemChanged(newLocation)
            notifyItemChanged(oldLocation)
        }
    }

    fun resetEntity(item: ScheduleItem) {
        val location = item.day
        if (location >= 0) {
            data[location] = ScheduleItem("", location)
            notifyItemChanged(location)
        }
    }

    fun reorderAfterMove(): List<ScheduleItem> {
        // Assign the right day indices to the objects
        data.forEachIndexed { index, _ -> data[index].day = index }
        // Only return the filled ones for syncing
        return data.filter { !it.isEmpty }
    }

    private fun fillUpScheduleList(items: MutableList<ScheduleItem>): MutableList<ScheduleItem> {

        val array = arrayOfNulls<ScheduleItem>(MAX_SCHEDULES)
        // Populate array with all given items
        items.forEach { array[it.day] = it }
        // Now add placeholder objects for empty spots
        (0 until MAX_SCHEDULES).forEach { idx ->
            if (array[idx] == null) {
                array[idx] = ScheduleItem("", idx)
            }
        }
        // Safe to do so, because all nulls are already replaced
        return array.mapTo(mutableListOf()) { it!! }
    }

    internal inner class ViewHolder(itemView: View) : BaseAdapter<ScheduleItem>.ViewHolder(itemView) {

        private lateinit var item: ScheduleItem
        private var itemPosition: Int = 0

        private val txtName: TextView by bindView(R.id.item_schedule_txt_name)
        private val btnClear: ImageButton by bindView(R.id.item_schedule_btn_clear)

        init {
            itemView.setOnClickListener {
                onScheduleItemSelectedListener?.invoke(item, itemView, itemPosition)
            }
            btnClear.setOnClickListener {
                onScheduleItemDismissedListener?.invoke(item, itemPosition)
            }
        }

        override fun bindToView(t: ScheduleItem) { // Not needed in this case
        }

        fun bind(item: ScheduleItem, position: Int) {
            this.item = item
            itemPosition = position
            txtName.text = item.name
        }
    }

    companion object {
        const val MAX_SCHEDULES = 7
    }
}