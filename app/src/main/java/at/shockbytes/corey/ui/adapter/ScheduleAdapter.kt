package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import android.view.View
import android.view.ViewGroup
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.core.util.UserSettings
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
import timber.log.Timber
import java.util.Collections

/**
 * Author:  Martin Macheiner
 * Date:    02.12.2015
 */
class ScheduleAdapter(
    context: Context,
    private val onItemClickedListener: ((item: ScheduleItem, v: View, position: Int) -> Unit),
    private val onItemDismissedListener: ((item: ScheduleItem, position: Int) -> Unit),
    private val weatherResolver: ScheduleWeatherResolver,
    private val schedulers: SchedulerFacade,
    private val userSettings: UserSettings
) : BaseAdapter<ScheduleItem>(context), ItemTouchHelperAdapter {

    private val compositeDisposable = CompositeDisposable()

    override var data: MutableList<ScheduleItem>
        get() = super.data
        set(value) {
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
    ): BaseAdapter<ScheduleItem>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_schedule, parent, false))
    }

    override fun onBindViewHolder(holder: BaseAdapter<ScheduleItem>.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? ViewHolder)?.bind(data[position], position)
    }

    override fun onDetachedFromRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
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

    private fun fillUpScheduleList2(items: List<ScheduleItem>): List<ScheduleItem> {
        val def = Array(MAX_SCHEDULES) { emptyScheduleItem(it) }.toMutableList()
        items.forEach { item ->
            def[item.day] = item
        }
        return def
    }

    private fun emptyScheduleItem(idx: Int) = ScheduleItem("", idx, locationType = LocationType.NONE)

    private inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter<ScheduleItem>.ViewHolder(containerView), LayoutContainer {

        private lateinit var item: ScheduleItem
        private var itemPosition: Int = 0

        init {
            item_schedule_txt_name.setOnClickListener {
                onItemClickedListener.invoke(item, itemView, itemPosition)
            }
            item_schedule_btn_clear.setOnClickListener {
                onItemDismissedListener.invoke(item, itemPosition)
            }
        }

        override fun bindToView(t: ScheduleItem) = Unit

        fun bind(item: ScheduleItem, position: Int) {
            this.item = item
            itemPosition = position
            item_schedule_txt_name.text = item.name

            if (isOutdoor(item)) {
                loadWeather(position)
            }

            item_schedule_iv_icon.apply {
                setImageResource((item.workoutIconType.iconRes ?: 0))
                item.workoutIconType.iconTint?.let { tintColor ->
                    imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, tintColor))
                }
            }
        }

        private fun isOutdoor(item: ScheduleItem): Boolean {
            return (item.locationType == LocationType.OUTDOOR)
        }

        private fun loadWeather(index: Int) {
            userSettings.isWeatherForecastEnabled
                    .flatMapSingle { weatherResolver.resolveWeatherForScheduleIndex(index) }
                    .subscribeOn(schedulers.io)
                    .observeOn(schedulers.ui)
                    .subscribe({ weatherInfo ->
                        item_schedule_weather.apply {
                            setVisible(true)
                            setWeatherInfo(weatherInfo, unit = "°C", animate = true)
                        }
                    }, {
                        // Suppress errors
                        // Timber.e(throwable)
                        item_schedule_weather.setVisible(false)
                    })
                    .addTo(compositeDisposable)
        }
    }

    companion object {
        const val MAX_SCHEDULES = 7
    }
}