package at.shockbytes.corey.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.body.goal.Goal;
import at.shockbytes.corey.common.core.adapter.BaseAdapter;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Martin Macheiner
 *         Date: 08.03.2017.
 */

public class GoalAdapter extends BaseAdapter<Goal> {

    public interface OnGoalActionClickedListener {

        void onDeleteGoalClicked(Goal g);

        void onFinishGoalClicked(Goal g);
    }

    private OnGoalActionClickedListener onGoalActionClickedListener;

    public GoalAdapter(Context cxt, List<Goal> data) {
        super(cxt, data);
    }

    @Override
    public BaseAdapter<Goal>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_goal, parent, false));
    }

    public void setOnGoalActionClickedListener(OnGoalActionClickedListener listener) {
        onGoalActionClickedListener = listener;
    }

    public class ViewHolder extends BaseAdapter<Goal>.ViewHolder {

        @Bind(R.id.item_goal_text)
        TextView txtGoal;

        @Bind(R.id.item_goal_btn_done)
        ImageButton imgBtnDone;

        ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Goal goal) {
            content = goal;

            txtGoal.setText(goal.getMessage());

            if (goal.isDone()) {
                imgBtnDone.setImageResource(R.drawable.ic_cancel);
                txtGoal.setPaintFlags(txtGoal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                imgBtnDone.setImageResource(R.drawable.ic_done);
                txtGoal.setPaintFlags(txtGoal.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }

        @OnClick(R.id.item_goal_btn_done)
        void onClickDone() {

            if (onGoalActionClickedListener != null) {

                if (content.isDone()) {
                    onGoalActionClickedListener.onDeleteGoalClicked(content);
                } else {
                    onGoalActionClickedListener.onFinishGoalClicked(content);
                }

            }
        }

    }

}
