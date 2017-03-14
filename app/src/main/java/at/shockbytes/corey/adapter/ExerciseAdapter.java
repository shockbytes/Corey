package at.shockbytes.corey.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.adapter.helper.ItemTouchHelperAdapter;
import at.shockbytes.corey.common.core.workout.model.Exercise;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Martin Macheiner
 *         Date: 02.12.2015.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    public interface OnItemClickListener {

        void onItemClick(Exercise t, View v);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(Exercise t, View v);
    }

    public interface OnItemMoveListener {

        void onItemMove(Exercise exercise, int from, int to);

        void onItemDismissed(Exercise exercise);
    }

    private boolean isItemMovable;

    private Context context;
    private List<Exercise> data;
    private final LayoutInflater inflater;

    private OnItemMoveListener onItemMoveListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    //----------------------------------------------------------------------

    public ExerciseAdapter(Context context, List<Exercise> data) {

        isItemMovable = false;

        this.context = context;
        this.data = new ArrayList<>();
        inflater = LayoutInflater.from(context);

        setData(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_exercises, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
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
            onItemMoveListener.onItemDismissed(entry);
        }
        notifyItemRemoved(position);
    }

    public void setOnItemMoveListener(OnItemMoveListener listener) {
        onItemMoveListener = listener;
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    public void setItemsMovable(boolean isItemMovable) {
        this.isItemMovable = isItemMovable;
        notifyDataSetChanged();
    }

    //-----------------------------Data Section-----------------------------
    public void addEntity(int i, Exercise entity) {
        data.add(i, entity);
        notifyItemInserted(i);
    }

    public void addEntityAtLast(Exercise entity) {
        addEntity(data.size(), entity);
    }

    public void addEntityAtFirst(Exercise entity) {
        addEntity(0, entity);
    }

    public void deleteEntity(Exercise exercise) {
        int location = getLocation(data, exercise);
        if (location > -1) {
            deleteEntity(location);
        }
    }

    public void deleteEntity(int i) {
        data.remove(i);
        notifyItemRemoved(i);
    }

    public void moveEntity(int i, int dest) {
        Exercise temp = data.remove(i);
        data.add(dest, temp);
        notifyItemMoved(i, dest);
    }

    public void setData(List<Exercise> data) {

        if (data == null) {
            return;
        }

        //Remove all deleted items
        for (int i = this.data.size() - 1; i >= 0; --i) {
            //Remove all deleted items
            if (getLocation(data, this.data.get(i)) < 0) {
                deleteEntity(i);
            }
        }

        //Add and move items
        for (int i = 0; i < data.size(); ++i) {
            Exercise entity = data.get(i);
            int location = getLocation(this.data, entity);
            if (location < 0) {
                addEntity(i, entity);
            } else if (location != i) {
                moveEntity(i, location);
            }
        }
    }

    public List<Exercise> getData() {
        return data;
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

    private int getLocation(List<Exercise> data, Exercise searching) {

        for (int j = 0; j < data.size(); ++j) {
            Exercise newEntity = data.get(j);
            if (searching.equals(newEntity)) {
                return j;
            }
        }

        return -1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private Exercise exercise;

        @Bind(R.id.item_exercise_txt_name)
        protected TextView txtName;

        @Bind(R.id.item_exercise_imgview_move)
        protected ImageView imgViewMove;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(exercise, itemView);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(exercise, itemView);
                    }
                    return true;
                }
            });
        }

        public void bind(Exercise item){

            exercise = item;
            txtName.setText(item.getDisplayName(context));

            imgViewMove.setVisibility(isItemMovable ? View.VISIBLE : View.GONE);
        }
    }

}