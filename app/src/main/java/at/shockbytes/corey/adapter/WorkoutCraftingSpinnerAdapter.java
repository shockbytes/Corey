package at.shockbytes.corey.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.util.view.model.SpinnerData;

/**
 * @author Martin Macheiner
 *         Date: 24.02.2017.
 */

public class WorkoutCraftingSpinnerAdapter extends ArrayAdapter<SpinnerData> {

    public WorkoutCraftingSpinnerAdapter(Context context, List<SpinnerData> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (convertView == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_spinner_workout, parent, false);
        }

        TextView text = (TextView) v.findViewById(R.id.item_spinner_workout_text);
        text.setText(getItem(position).getText());
        ImageView imgView = (ImageView) v.findViewById(R.id.item_spinner_workout_image);
        int iconId = getItem(position).getIconId();
        if (iconId > 0) {
            imgView.setImageResource(iconId);
            imgView.setColorFilter(Color.parseColor("#545454"));
        } else {
            imgView.setVisibility(View.GONE);
        }

        return v;
    }
}
