package at.shockbytes.corey.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.workout.model.Exercise;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Martin Macheiner
 *         Date: 24.02.2017.
 */

public class AddExerciseAdapter extends RecyclerView.Adapter<AddExerciseAdapter.ViewHolder> {

    public interface OnItemClickListener {

        void onItemClick(Exercise t, View v);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(Exercise t, View v);
    }


    private List<Exercise> data;
    private List<Exercise> originalData;

    private Context context;
    private final LayoutInflater inflater;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    //----------------------------------------------------------------------

    public AddExerciseAdapter(Context context, List<Exercise> data) {

        this.context = context;
        inflater = LayoutInflater.from(context);

        this.data = new ArrayList<>();
        originalData = new ArrayList<>(data);

        setData(data, false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.listitem_add_exercise, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
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

    public void setData(List<Exercise> data, boolean filtering) {

        if (data == null) {
            return;
        }

        if (!filtering) {
            originalData = new ArrayList<>(data);
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

    public void filter(String query) {

        if (!query.isEmpty() || query.length() != 0) {
            List<Exercise> filtered = filterList(data, query);
            setData(filtered, true);
        } else {
            // Restore original data
            setData(new ArrayList<>(originalData), true);
        }
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

    //----------------------------------------------------------------------
    class ViewHolder extends RecyclerView.ViewHolder {

        private Exercise exercise;

        @Bind(R.id.listitem_add_exercise_btn_title)
        Button btnTitle;

        ViewHolder(final View itemView) {
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

        public void bind(Exercise exercise) {
            this.exercise = exercise;
            btnTitle.setText(exercise.getDisplayName(context));
        }
    }
}
