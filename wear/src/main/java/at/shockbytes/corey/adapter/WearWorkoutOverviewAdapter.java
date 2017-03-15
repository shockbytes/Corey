package at.shockbytes.corey.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.adapter.BaseAdapter;
import at.shockbytes.corey.common.core.util.ResourceManager;
import at.shockbytes.corey.common.core.workout.model.Workout;
import butterknife.Bind;

/**
 * @author Martin Macheiner
 *         Date: 14.03.2017.
 */

public class WearWorkoutOverviewAdapter extends BaseAdapter<Workout> {

    public WearWorkoutOverviewAdapter(Context cxt, List<Workout> data) {
        super(cxt, data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_wear_workout_overview, parent, false));
    }

    public class ViewHolder extends BaseAdapter<Workout>.ViewHolder {

        @Bind(R.id.item_wear_workout_overview_txt)
        TextView txtName;

        @Bind(R.id.item_wear_workout_overview_img)
        ImageView imgView;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bind(Workout w) {
            content = w;

            txtName.setText(w.getDisplayableName());
            imgView.setImageDrawable(ResourceManager.createRoundedBitmapFromResource(context,
                    w.getImageResForBodyRegion(), w.getColorResForIntensity()));
        }
    }

}
