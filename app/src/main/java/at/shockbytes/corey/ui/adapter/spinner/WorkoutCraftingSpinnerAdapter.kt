package at.shockbytes.corey.ui.adapter.spinner

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.view.model.SpinnerData

/**
 * Author:  Martin Macheiner
 * Date:    24.02.2017
 */
class WorkoutCraftingSpinnerAdapter(
    context: Context,
    objects: List<SpinnerData>
) : ArrayAdapter<SpinnerData>(context, 0, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getDropDownView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

        val v = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_spinner_workout, parent, false)

        val text = v.findViewById<View>(R.id.item_spinner_workout_text) as TextView
        text.text = getItem(position)?.text
        val imgView = v.findViewById<View>(R.id.item_spinner_workout_image) as ImageView
        val iconId = getItem(position)?.iconId
        if (iconId != null && iconId > 0) {
            imgView.setImageResource(iconId)
            imgView.setColorFilter(Color.parseColor("#545454"))
        } else {
            imgView.visibility = View.GONE
        }

        return v
    }
}
