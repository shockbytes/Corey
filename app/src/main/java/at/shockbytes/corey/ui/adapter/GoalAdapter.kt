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
class GoalAdapter(cxt: Context) : BaseAdapter<GoalItem>(cxt) {

    private var onGoalActionClickedListener: OnGoalActionClickedListener? = null

    interface OnGoalActionClickedListener {

        fun onDeleteGoalClicked(g: GoalItem)

        fun onFinishGoalClicked(g: GoalItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseAdapter.ViewHolder<GoalItem> {
        return ViewHolder(inflater.inflate(R.layout.item_goal, parent, false))
    }

    fun setOnGoalActionClickedListener(listener: OnGoalActionClickedListener) {
        onGoalActionClickedListener = listener
    }

    inner class ViewHolder(itemView: View) : BaseAdapter.ViewHolder<GoalItem>(itemView) {

        private val txtGoal: TextView by bindView(R.id.item_goal_text)
        private val imgBtnDone: ImageButton by bindView(R.id.item_goal_btn_done)

        private val ivCategory: ImageView by bindView(R.id.item_goal_iv_category)
        private val tvCategory: TextView by bindView(R.id.item_goal_txt_category)
        private val tvDueDate: TextView by bindView(R.id.item_goal_txt_due_date)

        override fun bindToView(content: GoalItem, position: Int) {

            txtGoal.text = content.message

            if (content.isCompleted) {
                imgBtnDone.setImageResource(R.drawable.ic_cancel)
                txtGoal.paintFlags = txtGoal.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                imgBtnDone.setImageResource(R.drawable.ic_done)
                txtGoal.paintFlags = txtGoal.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            imgBtnDone.setOnClickListener {
                onClickDone(content)
            }

            ivCategory.setImageResource(content.categoryIcon)
            tvCategory.setText(content.categoryString)
            tvDueDate.text = content.dueDateFormatted
        }

        private fun onClickDone(content: GoalItem) {
            if (content.isCompleted) {
                onGoalActionClickedListener?.onDeleteGoalClicked(content)
            } else {
                onGoalActionClickedListener?.onFinishGoalClicked(content)
            }
        }
    }
}
