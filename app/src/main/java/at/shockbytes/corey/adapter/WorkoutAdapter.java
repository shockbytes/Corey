package at.shockbytes.corey.adapter;

import android.content.Context;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.common.core.adapter.BaseAdapter;
import at.shockbytes.corey.common.core.util.ResourceManager;
import at.shockbytes.corey.common.core.workout.model.Workout;
import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author Martin Macheiner
 *         Date: 27.10.2015.
 */
public class WorkoutAdapter extends BaseAdapter<Workout> {

    public interface OnWorkoutPopupItemSelectedListener {

        void onDelete(Workout w);

        void onEdit(Workout w);
    }

    private OnWorkoutPopupItemSelectedListener onWorkoutPopupItemSelectedListener;

    //----------------------------------------------------------------------

    public WorkoutAdapter(Context cxt, List<Workout> data,
                          OnWorkoutPopupItemSelectedListener listener){
        super(cxt, data);
        this.onWorkoutPopupItemSelectedListener = listener;
    }

    @Override
    public BaseAdapter<Workout>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_workout, parent, false));
    }

    class ViewHolder extends BaseAdapter<Workout>.ViewHolder
            implements PopupMenu.OnMenuItemClickListener {

        private PopupMenu popupMenu;

        @Bind(R.id.item_training_cardview)
        CardView cardView;

        @Bind(R.id.item_training_txt_title)
        TextView txtTitle;

        @Bind(R.id.item_training_txt_duration)
        TextView txtDuration;

        @Bind(R.id.item_training_txt_workouts)
        TextView txtWorkoutCount;

        @Bind(R.id.item_training_imgview_body_region)
        ImageView imgViewBodyRegion;

        @Bind(R.id.item_training_imgbtn_overflow)
        ImageButton imgBtnOverflow;

        ViewHolder(final View itemView) {
            super(itemView);

            popupMenu = new PopupMenu(context, imgBtnOverflow);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_workout, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            tryShowIconsInPopupMenu(popupMenu);
        }

        @Override
        public void bind(Workout w){
            content = w;

            txtTitle.setText(w.getDisplayableName());
            txtDuration.setText(context.getString(R.string.duration_with_minutes, w.getDuration()));
            txtWorkoutCount.setText(context.getString(R.string.exercises_with_count, w.getExerciseCount()));

            imgViewBodyRegion.setImageDrawable(ResourceManager.createRoundedBitmapFromResource(context,
                    w.getImageResForBodyRegion(), w.getColorResForIntensity()));
        }

        private void tryShowIconsInPopupMenu(PopupMenu menu) {

            try {
                Field fieldPopup = menu.getClass().getDeclaredField("mPopup");
                fieldPopup.setAccessible(true);
                MenuPopupHelper popup = (MenuPopupHelper) fieldPopup.get(menu);
                popup.setForceShowIcon(true);
            } catch (Exception e) {
                Log.d("Corey", "Cannot force to show icons in popupmenu");
            }
        }

        @OnClick(R.id.item_training_imgbtn_overflow)
        void onClickOverflow() {
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            if (onWorkoutPopupItemSelectedListener == null) {
                return false;
            }

            if (item.getItemId() == R.id.menu_popup_workout_edit) {
                onWorkoutPopupItemSelectedListener.onEdit(content);
            } else if (item.getItemId() == R.id.menu_popup_workout_delete) {
                onWorkoutPopupItemSelectedListener.onDelete(content);
            }
            return true;
        }
    }

}
