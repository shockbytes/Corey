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
    data: List<String>,
    onItemLongClickListener: OnItemLongClickListener<String>
) : BaseAdapter<String>(
    context,
    onItemLongClickListener = onItemLongClickListener
) {

    init {
        this.data.apply {
            clear()
            addAll(data)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter.ViewHolder<String> {
        return ViewHolder(inflater.inflate(R.layout.item_schedule_days, parent, false))
    }

    private inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter.ViewHolder<String>(containerView), LayoutContainer {

        override fun bindToView(content: String, position: Int) {

            val bgColor: Int
            val txtColor: Int
            if (LocalDate.now().dayOfWeek - 1 == position) {
                bgColor = ContextCompat.getColor(context, R.color.colorAccent)
                txtColor = ContextCompat.getColor(context, R.color.colorAccent)
            } else {
                bgColor = ContextCompat.getColor(context, R.color.white)
                txtColor = ContextCompat.getColor(context, R.color.colorPrimaryText)
            }

            card_item_schedule_days.strokeColor = bgColor
            item_schedule_days_txt_name.apply {
                text = content
                setTextColor(txtColor)
            }
        }
    }
}