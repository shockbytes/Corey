package at.shockbytes.corey.ui.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import at.shockbytes.corey.R
import at.shockbytes.util.adapter.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_schedule_days.*
import org.joda.time.LocalDate

/**
 * Author:  Martin Macheiner
 * Date:    02.12.2015
 */
class DaysScheduleAdapter(
    context: Context,
    data: List<String>
) : BaseAdapter<String>(context) {

    init {
        this.data = data.toMutableList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter.ViewHolder<String> {
        return ViewHolder(inflater.inflate(R.layout.item_schedule_days, parent, false))
    }

    override fun onBindViewHolder(holder: BaseAdapter.ViewHolder<String>, position: Int) {
        val s = data[position] + "_" + position
        holder.bind(s)
    }

    private inner class ViewHolder(
            override val containerView: View
    ) : BaseAdapter.ViewHolder<String>(containerView), LayoutContainer {

        override fun bindToView(t: String, position: Int) {
            val split = t.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val item = split[0]
            val position = Integer.parseInt(split[1])

            val bgColor: Int
            val txtColor: Int
            if (LocalDate.now().dayOfWeek - 1 == position) {
                bgColor = ContextCompat.getColor(context, R.color.colorAccent)
                txtColor = ContextCompat.getColor(context, R.color.colorAccent)
            } else {
                bgColor = ContextCompat.getColor(context, android.R.color.transparent)
                txtColor = ContextCompat.getColor(context, R.color.coreyBlack)
            }

            card_item_schedule_days.strokeColor = bgColor
            item_schedule_days_txt_name.apply {
                text = item
                setTextColor(txtColor)
            }
        }
    }
}