package at.shockbytes.corey.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.util.adapter.BaseAdapter
import at.shockbytes.util.adapter.ItemTouchHelperAdapter
import kotterknife.bindView
import java.util.*

/**
 * @author Martin Macheiner
 * Date: 02.12.2015.
 */
class ExerciseAdapter(context: Context, data: List<Exercise>)
    : BaseAdapter<Exercise>(context, data.toMutableList()), ItemTouchHelperAdapter {

    private var isItemMovable: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<Exercise>.ViewHolder {
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

    override fun onItemMoveFinished() {
    }

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

    internal inner class ViewHolder(itemView: View) : BaseAdapter<Exercise>.ViewHolder(itemView) {

        private val txtName: TextView by bindView(R.id.item_exercise_txt_name)
        private val imgViewMove: ImageView by bindView(R.id.item_exercise_imgview_move)

        override fun bindToView(t: Exercise) {
            content = t

            txtName.text = t.getDisplayName(context)
            imgViewMove.visibility = if (isItemMovable) View.VISIBLE else View.GONE
        }
    }

}