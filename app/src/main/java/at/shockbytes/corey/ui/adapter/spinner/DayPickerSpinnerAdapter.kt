package at.shockbytes.corey.ui.adapter.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import at.shockbytes.corey.R

class DayPickerSpinnerAdapter(
    context: Context,
    content: Array<String>
) : ArrayAdapter<String>(context, 0, content) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getDropDownView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val v = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_spinner_day_picker, parent, false)

        val textView = v.findViewById(R.id.tv_item_spinner_day_picker) as TextView

        getItem(position)?.let { item ->
            textView.text = item
        }

        return v
    }
}