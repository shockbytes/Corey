package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.util.adapter.BaseAdapter
import kotterknife.bindView
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<String>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_schedule_days, parent, false))
    }

    override fun onBindViewHolder(holder: BaseAdapter<String>.ViewHolder, position: Int) {
        val s = data[position] + "_" + position
        holder.bind(s)
    }

    internal inner class ViewHolder(itemView: View) : BaseAdapter<String>.ViewHolder(itemView) {

        private val txtName: TextView by bindView(R.id.item_schedule_days_txt_name)

        override fun bindToView(t: String) {
            val split = t.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val item = split[0]
            val position = Integer.parseInt(split[1])

            txtName.text = item

            val bgColor: Int
            val txtColor: Int
            if (LocalDate.now().dayOfWeek - 1 == position) {
                bgColor = android.R.color.white
                txtColor = ContextCompat.getColor(context, R.color.colorAccent)
            } else {
                bgColor = R.color.colorAccent
                txtColor = ContextCompat.getColor(context, android.R.color.white)
            }
            txtName.setBackgroundResource(bgColor)
            txtName.setTextColor(txtColor)
        }
    }
}