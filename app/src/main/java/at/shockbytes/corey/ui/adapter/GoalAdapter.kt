package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import at.shockbytes.corey.R
import at.shockbytes.corey.ui.model.GoalItem
import at.shockbytes.util.adapter.BaseAdapter
import kotterknife.bindView

/**
 * Author:  Martin Macheiner
 * Date:    08.03.2017
 */
class GoalAdapter(cxt: Context, data: List<GoalItem>) : BaseAdapter<GoalItem>(cxt, data.toMutableList()) {

    private var onGoalActionClickedListener: OnGoalActionClickedListener? = null

    interface OnGoalActionClickedListener {

        fun onDeleteGoalClicked(g: GoalItem)

        fun onFinishGoalClicked(g: GoalItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter<GoalItem>.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_goal, parent, false))
    }

    fun setOnGoalActionClickedListener(listener: OnGoalActionClickedListener) {
        onGoalActionClickedListener = listener
    }

    inner class ViewHolder(itemView: View) : BaseAdapter<GoalItem>.ViewHolder(itemView) {

        private val txtGoal: TextView by bindView(R.id.item_goal_text)
        private val imgBtnDone: ImageButton by bindView(R.id.item_goal_btn_done)

        private val ivCategory: ImageView by bindView(R.id.item_goal_iv_category)
        private val tvCategory: TextView by bindView(R.id.item_goal_txt_category)
        private val tvDueDate: TextView by bindView(R.id.item_goal_txt_due_date)

        override fun bindToView(t: GoalItem) {

            txtGoal.text = t.message

            if (t.isCompleted) {
                imgBtnDone.setImageResource(R.drawable.ic_cancel)
                txtGoal.paintFlags = txtGoal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                imgBtnDone.setImageResource(R.drawable.ic_done)
                txtGoal.paintFlags = txtGoal.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            imgBtnDone.setOnClickListener{
                onClickDone()
            }

            ivCategory.setImageResource(t.categoryIcon)
            tvCategory.setText(t.categoryString)
            tvDueDate.text = t.dueDateFormatted
        }

        private fun onClickDone() {
            if (content.isCompleted) {
                onGoalActionClickedListener?.onDeleteGoalClicked(content)
            } else {
                onGoalActionClickedListener?.onFinishGoalClicked(content)
            }
        }

    }

}
