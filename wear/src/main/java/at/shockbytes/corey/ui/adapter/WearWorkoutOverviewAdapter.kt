package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.corey.R

import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.util.AppUtils
import at.shockbytes.util.adapter.BaseAdapter
import kotterknife.bindView

/**
 * Author:  Martin Macheiner
 * Date:    14.03.2017
 */
class WearWorkoutOverviewAdapter(
    cxt: Context,
    data: List<Workout>
) : BaseAdapter<Workout>(cxt, data.toMutableList()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<Workout>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_wear_workout_overview, parent, false))
    }

    internal inner class ViewHolder(itemView: View) : BaseAdapter<Workout>.ViewHolder(itemView) {

        private val txtName: TextView by bindView(R.id.item_wear_workout_overview_txt)
        private val imgView: ImageView by bindView(R.id.item_wear_workout_overview_img)

        override fun bindToView(t: Workout) {
            txtName.text = t.displayableName
            imgView.setImageDrawable(AppUtils.createRoundedBitmapFromResource(context,
                    t.imageResForBodyRegion, t.colorResForIntensity))
        }
    }
}
