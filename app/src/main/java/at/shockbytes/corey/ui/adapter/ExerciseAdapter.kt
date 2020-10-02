package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.corey.common.setVisible
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.util.adapter.ItemTouchHelperAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_exercises.*
import java.util.Collections

/**
 * Author:  Martin Macheiner
 * Date:    02.12.2015
 */
class ExerciseAdapter(
    context: Context
) : BaseAdapter<Exercise>(context), ItemTouchHelperAdapter {

    private var isItemMovable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter.ViewHolder<Exercise> {
        return ViewHolder(inflater.inflate(R.layout.item_exercises, parent, false))
    }

    override fun onItemMove(from: Int, to: Int): Boolean {

        if (from < to) {
            for (i in from until to) {
                Collections.swap(data, i, i + 1)
            }
        } else {
            for (i in from downTo to + 1) {
                Collections.swap(data, i, i - 1)
            }
        }
        notifyItemMoved(from, to)
        onItemMoveListener?.onItemMove(data[to], from, to)

        return true
    }

    override fun onItemMoveFinished() = Unit

    override fun onItemDismiss(position: Int) {
        val entry = data.removeAt(position)
        onItemMoveListener?.onItemDismissed(entry, position)

        notifyItemRemoved(position)
    }

    fun setItemsMovable(isItemMovable: Boolean) {
        this.isItemMovable = isItemMovable
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        data = filterList(data, query)
    }

    private fun filterList(data: List<Exercise>, query: String): MutableList<Exercise> {
        val lowerCaseQuery = query.toLowerCase()

        val filteredModelList = ArrayList<Exercise>()
        for (s in data) {
            val text = s.name.toLowerCase()
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(s)
            }
        }
        return filteredModelList
    }

    inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter.ViewHolder<Exercise>(containerView), LayoutContainer {

        override fun bindToView(t: Exercise, position: Int) {
            item_exercise_txt_name.text = t.getDisplayName(context)
            item_exercise_imgview_move.setVisible(isItemMovable)
        }
    }
}