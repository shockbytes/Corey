package at.shockbytes.corey.ui.adapter

import android.content.Context
import androidx.appcompat.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.common.core.workout.model.Workout
import at.shockbytes.util.AppUtils
import at.shockbytes.util.adapter.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_workout.*

/**
 * Author:  Martin Macheiner
 * Date:    27.10.2015
 */
class WorkoutAdapter(
    cxt: Context,
    private val onWorkoutPopupItemSelectedListener: OnWorkoutPopupItemSelectedListener?,
    onItemClickListener: OnItemClickListener<Workout>
) : BaseAdapter<Workout>(cxt, onItemClickListener) {

    interface OnWorkoutPopupItemSelectedListener {

        fun onDelete(w: Workout?)

        fun onEdit(w: Workout?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter.ViewHolder<Workout> {
        return ViewHolder(inflater.inflate(R.layout.item_workout, parent, false))
    }

    inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter.ViewHolder<Workout>(containerView), PopupMenu.OnMenuItemClickListener, LayoutContainer {

        private val popupMenu: PopupMenu = PopupMenu(context, item_training_imgbtn_overflow)

        private lateinit var content: Workout

        init {
            setupPopupMenu()
        }

        override fun bindToView(content: Workout, position: Int) {
            this.content = content
            with(content) {
                item_training_txt_title.text = displayableName
                item_training_txt_duration.text = context.getString(R.string.duration_with_minutes, duration)
                item_training_txt_workouts.text = context.getString(R.string.exercises_with_count, exerciseCount)

                item_training_imgview_body_region
                    .setImageDrawable(
                        AppUtils.createRoundedBitmapFromResource(
                            context,
                            imageResForBodyRegion,
                            colorResForIntensity
                        )
                    )

                item_training_imgbtn_overflow.setOnClickListener { popupMenu.show() }

                item_training_imgview_equipment
                    .setImageDrawable(
                        AppUtils.createRoundedBitmapFromResource(
                            context,
                            CoreyUtils.getImageByEquipment(equipment),
                            R.color.equipmentBackground
                        )
                    )
            }
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {

            when (item.itemId) {
                R.id.menu_popup_workout_edit -> onWorkoutPopupItemSelectedListener?.onEdit(content)
                R.id.menu_popup_workout_delete -> onWorkoutPopupItemSelectedListener?.onDelete(content)
            }
            return true
        }

        private fun setupPopupMenu() {
            popupMenu.menuInflater.inflate(R.menu.menu_popup_workout, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            CoreyUtils.tryShowIconsInPopupMenu(popupMenu)
        }
    }
}
