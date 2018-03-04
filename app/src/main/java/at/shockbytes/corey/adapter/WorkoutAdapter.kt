package at.shockbytes.corey.adapter

import android.content.Context
import android.support.v7.view.menu.MenuPopupHelper
import android.support.v7.widget.CardView
import android.support.v7.widget.PopupMenu
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.util.AppUtils
import at.shockbytes.util.adapter.BaseAdapter
import kotterknife.bindView

/**
 * @author Martin Macheiner
 * Date: 27.10.2015.
 */
class WorkoutAdapter(cxt: Context,
                     data: List<Workout>,
                     private val onWorkoutPopupItemSelectedListener: OnWorkoutPopupItemSelectedListener?)
    : BaseAdapter<Workout>(cxt, data.toMutableList()) {

    interface OnWorkoutPopupItemSelectedListener {

        fun onDelete(w: Workout?)

        fun onEdit(w: Workout?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<Workout>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_workout, parent, false))
    }

    internal inner class ViewHolder(itemView: View)
        : BaseAdapter<Workout>.ViewHolder(itemView), PopupMenu.OnMenuItemClickListener {

        private val popupMenu: PopupMenu

        private val cardView: CardView by bindView(R.id.item_training_cardview)
        private val txtTitle: TextView by bindView(R.id.item_training_txt_title)
        private val txtDuration: TextView by bindView(R.id.item_training_txt_duration)
        private val txtWorkoutCount: TextView by bindView(R.id.item_training_txt_workouts)
        private val imgViewBodyRegion: ImageView by bindView(R.id.item_training_imgview_body_region)
        private val imgBtnOverflow: ImageButton by bindView(R.id.item_training_imgbtn_overflow)

        init {
            popupMenu = PopupMenu(context, imgBtnOverflow)
            popupMenu.menuInflater.inflate(R.menu.menu_popup_workout, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            tryShowIconsInPopupMenu(popupMenu)
        }

        override fun bind(t: Workout) {
            content = t

            txtTitle.text = t.displayableName
            txtDuration.text = context.getString(R.string.duration_with_minutes, t.duration)
            txtWorkoutCount.text = context.getString(R.string.exercises_with_count, t.exerciseCount)

            imgViewBodyRegion.setImageDrawable(AppUtils.createRoundedBitmapFromResource(context,
                    t.imageResForBodyRegion, t.colorResForIntensity))

            imgBtnOverflow.setOnClickListener { popupMenu.show() }

        }

        override fun onMenuItemClick(item: MenuItem): Boolean {

            if (item.itemId == R.id.menu_popup_workout_edit) {
                onWorkoutPopupItemSelectedListener?.onEdit(content)
            } else if (item.itemId == R.id.menu_popup_workout_delete) {
                onWorkoutPopupItemSelectedListener?.onDelete(content)
            }
            return true
        }

        private fun tryShowIconsInPopupMenu(menu: PopupMenu) {

            try {
                val fieldPopup = menu.javaClass.getDeclaredField("mPopup")
                fieldPopup.isAccessible = true
                val popup = fieldPopup.get(menu) as MenuPopupHelper
                popup.setForceShowIcon(true)
            } catch (e: Exception) {
                Log.d("Corey", "Cannot force to show icons in popupmenu")
            }
        }

    }

}
