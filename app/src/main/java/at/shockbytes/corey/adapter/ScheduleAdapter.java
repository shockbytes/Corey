package at.shockbytes.corey.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.schedule.ScheduleItem;
import at.shockbytes.util.adapter.ItemTouchHelperAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Martin Macheiner
 *         Date: 02.12.2015.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    public interface OnItemClickListener {

        void onItemClick(ScheduleItem item, View v, int position);
    }

    public interface OnItemLongClickListener {

        void onItemLongClick(ScheduleItem item, View v, int position);
    }

    public interface OnItemMoveListener {

        void onItemMove(ScheduleItem item, int from, int to);

        void onItemMoveFinished();

        void onItemDismissed(ScheduleItem item, int position);
    }

    private static final int MAX_SCHEDULES = 7;

    private Context context;
    private List<ScheduleItem> data;
    private final LayoutInflater inflater;

    private OnItemMoveListener onItemMoveListener;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private boolean isInPortraitMode;

    //----------------------------------------------------------------------

    public ScheduleAdapter(Context context, List<ScheduleItem> data) {

        this.context = context;
        this.data = new ArrayList<>();
        inflater = LayoutInflater.from(context);

        isInPortraitMode = context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;

        setData(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_schedule, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getScheduleItemForPosition(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public boolean onItemMove(int from, int to) {

        // Switch items
        if (!data.get(to).isEmpty() && !data.get(from).isEmpty()) {
            int tmp = data.get(from).getDay();
            data.get(from).setDay(to);
            data.get(to).setDay(tmp);
        } else {
            if (!data.get(to).isEmpty()) {
                data.get(to).setDay(from);
            }
            if (!data.get(from).isEmpty()) {
                data.get(from).setDay(to);
            }
        }


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
            onItemMoveListener.onItemMove(data.get(from), from, to);
        }
        return true;
    }

    @Override
    public void onItemMoveFinished() {

        if (onItemMoveListener != null) {
            onItemMoveListener.onItemMoveFinished();
        }
    }

    @Override
    public void onItemDismiss(int position) {

        ScheduleItem entry = data.remove(position);
        if (!entry.isEmpty()) {
            if (onItemMoveListener != null) {
                int pos = entry.getDay();
                onItemMoveListener.onItemDismissed(entry, pos);
            }
            notifyItemRemoved(position);
            addEntity(position, new ScheduleItem("", position));

        } else {
            notifyItemRemoved(position);
            addEntity(position, new ScheduleItem("", position));
        }

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

    //-----------------------------Data Section-----------------------------
    public void insertEntity(ScheduleItem item) {
        int location = item.getDay();
        if (location >= 0) {
            data.set(location, item);
            notifyItemChanged(location);
        }
    }

    public void updateEntity(ScheduleItem item) {

        int oldLocation = getLocation(data, item);
        int newLocation = item.getDay();

        if (newLocation >= 0 && oldLocation != newLocation) {
            ScheduleItem newLocationItem = data.get(newLocation);
            data.set(newLocation, item);
            data.set(oldLocation, newLocationItem);
            notifyItemChanged(newLocation);
            notifyItemChanged(oldLocation);
        }

    }

    public void resetEntity(ScheduleItem item) {
        int location = item.getDay();
        if (location >= 0) {
            data.set(location, new ScheduleItem("", location));
            notifyItemChanged(location);
        }
    }

    public void addEntity(int i, ScheduleItem entity) {
        data.add(i, entity);
        notifyItemInserted(i);
    }

    public void addEntityAtLast(ScheduleItem entity) {
        addEntity(data.size(), entity);
    }

    public void addEntityAtFirst(ScheduleItem entity) {
        addEntity(0, entity);
    }

    public void deleteEntity(ScheduleItem exercise) {
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
        ScheduleItem temp = data.remove(i);
        data.add(dest, temp);
        notifyItemMoved(i, dest);
    }

    private List<ScheduleItem> fillUpScheduleList(List<ScheduleItem> items) {

        List<ScheduleItem> filledItems = new ArrayList<>();
        for (int i = 0; i < MAX_SCHEDULES; i++) {
            filledItems.add(new ScheduleItem("", i));
        }

        // Fill up schedule items if not available (or db was reset)
        //if (items.size() < MAX_SCHEDULES) {
        for (int i = 0; i < items.size(); i++) {
            filledItems.set(items.get(i).getDay(), items.get(i));
        }

        // Remove empty views if there are too much in it
        if (items.size() >= MAX_SCHEDULES) {
            for (int i = 0; items.size() > MAX_SCHEDULES && i < items.size(); i++) {
                if (items.get(i).isEmpty()) {
                    items.remove(i);
                }
            }
            filledItems = new ArrayList<>(items);
        }

        return filledItems;
    }

    public void setData(List<ScheduleItem> data) {

        if (data == null) {
            return;
        }
        data = fillUpScheduleList(data);

        //Remove all deleted items
        for (int i = this.data.size() - 1; i >= 0; --i) {
            //Remove all deleted items
            if (getLocation(data, this.data.get(i)) < 0) {
                deleteEntity(i);
            }
        }

        //Add and move items
        for (int i = 0; i < data.size(); ++i) {
            ScheduleItem entity = data.get(i);
            int location = getLocation(this.data, entity);
            if (location < 0) {
                addEntity(i, entity);
            } else if (location != i) {
                moveEntity(i, location);
            }
        }
    }

    public List<ScheduleItem> getData() {
        return data;
    }

    public List<ScheduleItem> getScheduleData() {

        List<ScheduleItem> filled = new ArrayList<>();
        for (ScheduleItem item : data) {
            if (!item.isEmpty()) {
                filled.add(item);
            }
        }
        return filled;
    }

    private int getLocation(List<ScheduleItem> data, ScheduleItem searching) {

        for (int j = 0; j < data.size(); ++j) {
            ScheduleItem newEntity = data.get(j);
            if (searching.equals(newEntity)) {
                return j;
            }
        }

        return -1;
    }

    private ScheduleItem getScheduleItemForPosition(int position) {

        for (ScheduleItem item : data) {
            if (item.getDay() == position) {
                return item;
            }
        }
        return new ScheduleItem("", position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ScheduleItem item;
        private int position;

        @BindView(R.id.item_schedule_txt_name)
        TextView txtName;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(item, itemView, position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(item, itemView, position);
                    }
                    return true;
                }
            });
        }

        public void bind(ScheduleItem item, int position) {

            this.item = item;
            this.position = position;

            txtName.setText(item.getName());

            //if (item.getDay() == ResourceManager.getDayOfWeek()) {
            //txtName.setBackgroundResource(R.color.current_day);
            //}

        }
    }

}