package at.shockbytes.corey.adapter;

import android.content.Context;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import at.shockbytes.corey.R;
import at.shockbytes.corey.util.ResourceManager;
import at.shockbytes.corey.workout.model.Workout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Martin Macheiner
 *         Date: 27.10.2015.
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder>{

    public interface OnItemClickListener{

        void onItemClick(Workout t, View v);
    }

    public interface OnItemLongClickListener{

        void onItemLongClick(Workout t, View v);
    }

    public interface OnWorkoutPopupItemSelectedListener {

        void onDelete(Workout w);

        void onEdit(Workout w);
    }

    private List<Workout> data;
    private LayoutInflater layoutInflater;
    private Context context;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnWorkoutPopupItemSelectedListener onWorkoutPopupItemSelectedListener;

    //----------------------------------------------------------------------

    public WorkoutAdapter(Context cxt, List<Workout> data,
                          OnWorkoutPopupItemSelectedListener listener){

        context = cxt;
        layoutInflater = LayoutInflater.from(cxt);
        this.data = new ArrayList<>();
        this.onWorkoutPopupItemSelectedListener = listener;

        setData(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_workout, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        onItemLongClickListener = listener;
    }

    //-----------------------------Data Section-----------------------------
    public void addEntity(int i, Workout entity){
        data.add(i, entity);
        notifyItemInserted(i);
    }

    public void deleteEntity(Workout entity) {
        int location = getLocation(data, entity);
        if (location >= 0) {
            deleteEntity(location);
        }
    }

    public void deleteEntity(int i){
        data.remove(i);
        notifyItemRemoved(i);
    }

    public void addEntityAtLast(Workout entity) {
        addEntity(data.size(), entity);
    }

    public void addEntityAtFirst(Workout entity) {
        addEntity(0, entity);
    }

    public void updateEntity(Workout entity) {
        int location = getLocation(data, entity);
        if (location >= 0) {
            data.set(location, entity);
            notifyItemChanged(location);
        }
    }

    public void moveEntity(int i, int dest){
        Workout temp = data.remove(i);
        data.add(dest, temp);
        notifyItemMoved(i, dest);
        notifyDataSetChanged();
    }

    public List<Workout> getData(){
        return data;
    }

    public void setData(List<Workout> data){

        if(data == null){
            return;
        }

        //Remove all deleted items
        for(int i = this.data.size() - 1; i >= 0; --i){
            //Remove all deleted items
            if(getLocation(data, this.data.get(i)) < 0){
                deleteEntity(i);
            }
        }

        //Add and move items
        for(int i = 0; i < data.size(); ++i){
            Workout entity = data.get(i);
            int location = getLocation(this.data, entity);
            if(location < 0){
                addEntity(i, entity);
            }
            else if(location != i){
                moveEntity(i, location);
            }
        }
        notifyDataSetChanged();
    }

    private int getLocation(List<Workout> data, Workout searching){

        for(int j = 0; j < data.size(); ++j){
            Workout newEntity = data.get(j);
            if(searching.equals(newEntity)){
                return j;
            }
        }
        return -1;
    }
    //----------------------------------------------------------------------

    class ViewHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

        private Workout content;

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
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(content, itemView);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (onItemLongClickListener != null) {
                        onItemLongClickListener.onItemLongClick(content, itemView);
                    }
                    return true;
                }
            });

            popupMenu = new PopupMenu(context, imgBtnOverflow);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_workout, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            tryShowIconsInPopupMenu(popupMenu);
        }

        public void bind(Workout w){

            content = w;

            txtTitle.setText(w.getName());
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
