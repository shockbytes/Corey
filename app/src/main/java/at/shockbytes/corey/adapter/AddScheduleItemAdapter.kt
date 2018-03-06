package at.shockbytes.corey.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button


import at.shockbytes.corey.R
import at.shockbytes.util.adapter.BaseAdapter
import io.reactivex.functions.BiPredicate
import kotterknife.bindView

/**
 * @author  Martin Macheiner
 * Date:    24.02.2017.
 */

class AddScheduleItemAdapter(context: Context,
                             data: List<String>,
                             filterPredicate: BiPredicate<String, String>)
    : FilterableBaseAdapter<String>(context, data.toMutableList(), filterPredicate) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<String>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.listitem_add_exercise, parent, false))
    }

    internal inner class ViewHolder(itemView: View) : BaseAdapter<String>.ViewHolder(itemView) {

        private val btnTitle: Button by bindView(R.id.listitem_add_exercise_btn_title)

        override fun bind(t: String) {
            this.content = t
            btnTitle.text = t
        }
    }
}
