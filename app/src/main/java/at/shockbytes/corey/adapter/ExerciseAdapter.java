package at.shockbytes.corey.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.adapter.BaseAdapter;
import at.shockbytes.corey.common.core.adapter.helper.ItemTouchHelperAdapter;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import butterknife.Bind;

/**
 * @author Martin Macheiner
 *         Date: 02.12.2015.
 */
public class ExerciseAdapter extends BaseAdapter<Exercise>
        implements ItemTouchHelperAdapter {

    private boolean isItemMovable;

    public ExerciseAdapter(Context context, List<Exercise> data) {
        super(context, data);
        isItemMovable = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_exercises, parent, false));
    }

    @Override
    public boolean onItemMove(int from, int to) {

        if (from < to) {
            for (int i = from; i < to; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = from; i > to; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(from, to);
        if (onItemMoveListener != null) {
            onItemMoveListener.onItemMove(data.get(to), from, to);
        }
        return true;
    }

    @Override
    public void onItemMoveFinished() {

    }

    @Override
    public void onItemDismiss(int position) {
        Exercise entry = data.remove(position);
        if (onItemMoveListener != null) {
            onItemMoveListener.onItemDismissed(entry, position);
        }
        notifyItemRemoved(position);
    }

    public void setItemsMovable(boolean isItemMovable) {
        this.isItemMovable = isItemMovable;
        notifyDataSetChanged();
    }

    public void filter(String query) {

        List<Exercise> filtered = filterList(data, query);
        setData(filtered);
    }

    private List<Exercise> filterList(List<Exercise> data, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final List<Exercise> filteredModelList = new ArrayList<>();
        for (Exercise s : data) {
            final String text = s.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(s);
            }
        }
        return filteredModelList;
    }

    class ViewHolder extends BaseAdapter<Exercise>.ViewHolder {

        @Bind(R.id.item_exercise_txt_name)
        protected TextView txtName;

        @Bind(R.id.item_exercise_imgview_move)
        protected ImageView imgViewMove;

        public ViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Exercise item){
            content = item;

            txtName.setText(item.getDisplayName(context));
            imgViewMove.setVisibility(isItemMovable ? View.VISIBLE : View.GONE);
        }
    }

}