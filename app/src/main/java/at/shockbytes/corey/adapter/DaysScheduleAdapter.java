package at.shockbytes.corey.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import at.shockbytes.corey.R;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Martin Macheiner
 *         Date: 02.12.2015.
 */
public class DaysScheduleAdapter extends RecyclerView.Adapter<DaysScheduleAdapter.ViewHolder> {


    private Context context;
    private List<String> data;
    private final LayoutInflater inflater;


    //----------------------------------------------------------------------

    public DaysScheduleAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = new ArrayList<>();
        inflater = LayoutInflater.from(context);

        setData(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_schedule_days, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //-----------------------------Data Section-----------------------------
    public void addEntity(int i, String entity) {
        data.add(i, entity);
        notifyItemInserted(i);
    }

    public void addEntityAtLast(String entity) {
        addEntity(data.size(), entity);
    }

    public void addEntityAtFirst(String entity) {
        addEntity(0, entity);
    }

    public void deleteEntity(String exercise) {
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
        String temp = data.remove(i);
        data.add(dest, temp);
        notifyItemMoved(i, dest);
    }

    public void setData(List<String> data) {

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
            String entity = data.get(i);
            int location = getLocation(this.data, entity);
            if (location < 0) {
                addEntity(i, entity);
            } else if (location != i) {
                moveEntity(i, location);
            }
        }
    }

    public List<String> getData() {
        return data;
    }

    private int getLocation(List<String> data, String searching) {

        for (int j = 0; j < data.size(); ++j) {
            String newEntity = data.get(j);
            if (searching.equals(newEntity)) {
                return j;
            }
        }

        return -1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_schedule_days_txt_name)
        TextView txtName;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(String item, int position) {
            txtName.setText(item);

            if (LocalDate.now().getDayOfWeek()-1 == position) {
                txtName.setBackgroundResource(R.color.current_day);
            } else {
                txtName.setBackgroundResource(R.color.colorAccent);
            }

        }
    }

}