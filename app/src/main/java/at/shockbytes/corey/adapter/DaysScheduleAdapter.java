package at.shockbytes.corey.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.adapter.BaseAdapter;
import butterknife.Bind;

/**
 * @author Martin Macheiner
 *         Date: 02.12.2015.
 */
public class DaysScheduleAdapter extends BaseAdapter<String> {

    public DaysScheduleAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public BaseAdapter<String>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_schedule_days, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseAdapter.ViewHolder holder, int position) {
        String s = data.get(position) + "_"+position;
        holder.bind(s);
    }

    class ViewHolder extends BaseAdapter<String>.ViewHolder {

        @Bind(R.id.item_schedule_days_txt_name)
        TextView txtName;

        ViewHolder(final View itemView) {
            super(itemView);
        }

        @Override
        public void bind(String s) {

            String[] split = s.split("_");
            String item = split[0];
            int position = Integer.parseInt(split[1]);

            txtName.setText(item);

            if (LocalDate.now().getDayOfWeek()-1 == position) {
                txtName.setBackgroundResource(R.color.current_day);
            } else {
                txtName.setBackgroundResource(R.color.colorAccent);
            }
        }
    }

}