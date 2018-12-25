package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.util.adapter.BaseAdapter import kotterknife.bindView

/**
 * @author  Martin Macheiner
 * Date:    24.02.2017.
 */
class AddExerciseAdapter(context: Context,
                         data: List<Exercise>,
                         filterPredicate: (Exercise, String) -> Boolean)
    : FilterableBaseAdapter<Exercise>(context, data.toMutableList(), filterPredicate) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<Exercise>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.listitem_add_exercise, parent, false))
    }

    internal inner class ViewHolder(itemView: View) : BaseAdapter<Exercise>.ViewHolder(itemView) {

        private val btnTitle: Button by bindView(R.id.listitem_add_exercise_btn_title)

        override fun bindToView(t: Exercise) {
            btnTitle.text = t.getDisplayName(context)
        }
    }
}
