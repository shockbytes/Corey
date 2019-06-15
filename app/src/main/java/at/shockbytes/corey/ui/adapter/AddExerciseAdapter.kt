package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.Exercise
import at.shockbytes.util.adapter.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.listitem_add_exercise.*

/**
 * Author:  Martin Macheiner
 * Date:    24.02.2017
 */
class AddExerciseAdapter(
    context: Context,
    data: List<Exercise>,
    filterPredicate: (Exercise, String) -> Boolean
) : FilterableBaseAdapter<Exercise>(context, data.toMutableList(), filterPredicate) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<Exercise>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.listitem_add_exercise, parent, false))
    }

    internal inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter<Exercise>.ViewHolder(containerView), LayoutContainer {

        override fun bindToView(t: Exercise) {
            tv_listitem_add_exercise_title.text = t.getDisplayName(context)
        }
    }
}
