package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import android.view.View
import android.view.ViewGroup
import at.shockbytes.corey.R
import at.shockbytes.corey.common.addTo
import at.shockbytes.corey.common.setVisible
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.corey.data.schedule.weather.ScheduleWeatherResolver
import at.shockbytes.corey.util.ScheduleItemDiffUtilCallback
import at.shockbytes.corey.util.isOutdoor
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
    private val emptyScheduleItemFactory: (position: Int) -> ScheduleItem,
    private val disposableBag: CompositeDisposable
) : BaseAdapter<ScheduleItem>(
    context = context,
    onItemClickListener = onItemClickListener,
    onItemMoveListener = onItemMoveListener
), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseAdapter.ViewHolder<ScheduleItem> {
        return ViewHolder(inflater.inflate(R.layout.item_schedule, parent, false))
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
        addEntity(position, emptyScheduleItemFactory(position))
    }

    // -----------------------------Data Section-----------------------------

    fun getItemAt(position: Int): ScheduleItem? {
        return data.getOrNull(position)
    }

    fun updateData(items: List<ScheduleItem>) {

        val diffResult = DiffUtil.calculateDiff(ScheduleItemDiffUtilCallback(data, items))

        data.clear()
        data.addAll(items)

        diffResult.dispatchUpdatesTo(this)
    }

    fun reorderAfterMove(): List<ScheduleItem> {
        return data.mapIndexed { index, scheduleItem ->
            scheduleItem.copy(day = index)
        }
    }

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
                        setWeatherInfo(weatherInfo, weatherInfo.temperatureUnit.unit, animate = true)
                    }
                }, {
                    // Suppress errors Timber.e(throwable)
                    item_schedule_weather.setVisible(false)
                })
                .addTo(disposableBag)
        }
    }
}