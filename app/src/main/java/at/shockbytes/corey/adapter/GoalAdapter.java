package at.shockbytes.corey.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.body.goal.Goal;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Martin Macheiner
 *         Date: 08.03.2017.
 */

public class GoalAdapter extends BaseAdapter<Goal> {

    public GoalAdapter(Context cxt, List<Goal> data) {
        super(cxt, data);
    }

    @Override
    public BaseAdapter<Goal>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_goal, parent, false));
    }

    class ViewHolder extends BaseAdapter<Goal>.ViewHolder {

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
            imgBtnDone.setVisibility(goal.isDone() ? View.GONE : View.VISIBLE);
        }

        @OnClick(R.id.item_goal_btn_done)
        protected void onClickDone() {
            // TODO
        }

    }

}
