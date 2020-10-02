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
    onItemClickListener: OnItemClickListener<Exercise>,
    filterPredicate: (Exercise, String) -> Boolean
) : FilterableBaseAdapter<Exercise>(context, data.toMutableList(), onItemClickListener, filterPredicate) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter.ViewHolder<Exercise> {
        return ViewHolder(inflater.inflate(R.layout.listitem_add_exercise, parent, false))
    }

    internal inner class ViewHolder(
        override val containerView: View
    ) : BaseAdapter.ViewHolder<Exercise>(containerView), LayoutContainer {

        override fun bindToView(t: Exercise, position: Int) {
            tv_listitem_add_exercise_title.text = t.getDisplayName(context)
        }
    }
}
