package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.workout.model.LocationType
import at.shockbytes.corey.common.setVisible
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.corey.data.schedule.weather.ScheduleWeatherResolver
import at.shockbytes.corey.util.ScheduleItemDiffUtilCallback
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.util.adapter.ItemTouchHelperAdapter
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_schedule.*
import java.util.Collections

/**
 * Author:  Martin Macheiner
 * Date:    02.12.2015
 */
class ScheduleAdapter(
    context: Context,
    onItemClickListener: OnItemClickListener<ScheduleItem>,
    onItemMoveListener: OnItemMoveListener<ScheduleItem>,
    private val weatherResolver: ScheduleWeatherResolver,
) : BaseAdapter<ScheduleItem>(
        context,
        onItemClickListener = onItemClickListener,
        onItemMoveListener = onItemMoveListener
), ItemTouchHelperAdapter {

    private val compositeDisposable = CompositeDisposable()

    override var data: MutableList<ScheduleItem>
        get() = super.data
        set(value) {
            // TODO Fix this too
            for (i in data.size - 1 downTo 0) {
                deleteEntity(i)
            }
            fillUpScheduleList2(value).forEach { addEntityAtLast(it) }
        }

    init {
        data = mutableListOf()
    }

    // ----------------------------------------------------------------------

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseAdapter.ViewHolder<ScheduleItem> {
        return ViewHolder(inflater.inflate(R.layout.item_schedule, parent, false))
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        compositeDisposable.dispose()
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
        addEntity(position, emptyScheduleItem(position))
    }

    // -----------------------------Data Section-----------------------------

    fun getItemAt(position: Int): ScheduleItem? {
        return data.getOrNull(position)
    }

    fun updateData(items: List<ScheduleItem>) {

        val filledItems = fillUpScheduleList2(items)
        val diffResult = DiffUtil.calculateDiff(ScheduleItemDiffUtilCallback(data, filledItems))

        data.clear()
        data.addAll(filledItems)

        diffResult.dispatchUpdatesTo(this)
    }

    fun reorderAfterMove(): List<ScheduleItem> {
        // Assign the right day indices to the objects
        data.forEachIndexed { index, _ ->
            data[index].day = index
        }
        // Only return the filled ones for syncing
        return data.filter { !it.isEmpty }
    }

    @Deprecated(message = "Use ViewModel implementation instead")
    private fun fillUpScheduleList2(items: List<ScheduleItem>): List<ScheduleItem> {
        val def = Array(MAX_SCHEDULES) { emptyScheduleItem(it) }.toMutableList()
        items.forEach { item ->
            def[item.day] = item
        }
        return def
    }

    @Deprecated(message = "Use ViewModel implementation instead")
    private fun emptyScheduleItem(idx: Int) = ScheduleItem("", idx, locationType = LocationType.NONE)

    private inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter.ViewHolder<ScheduleItem>(containerView), LayoutContainer {

        override fun bindToView(content: ScheduleItem, position: Int) {
            item_schedule_txt_name.text = content.name

            if (content.isOutdoor()) {
                loadWeather(position)
            }

            item_schedule_iv_icon.apply {
                setImageResource((content.workoutIconType.iconRes ?: 0))
                content.workoutIconType.iconTint?.let { tintColor ->
                    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, tintColor))
                }
            }
        }

        private fun loadWeather(index: Int) {
            weatherResolver.resolveWeatherForScheduleIndex(index)
                    .subscribe({ weatherInfo ->
                        item_schedule_weather.apply {
                            setVisible(true)
                            setWeatherInfo(weatherInfo, unit = "°C", animate = true)
                        }
                    }, {
                        // Suppress errors Timber.e(throwable)
                        item_schedule_weather.setVisible(false)
                    })
                    .addTo(compositeDisposable)
        }
    }

    companion object {
        const val MAX_SCHEDULES = 7
    }
}