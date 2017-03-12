package at.shockbytes.corey.adapter.helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @author Martin Macheiner
 *         Date: 09.09.2015.
 */
public class CoreyItemTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter adapter;

    private boolean allowSwipeToDismiss;
    private boolean allowDragInAllDirections;

    public CoreyItemTouchHelper(ItemTouchHelperAdapter adapter, boolean allowSwipeToDismiss,
                                boolean allowDragInAllDirections) {
        this.adapter = adapter;
        this.allowSwipeToDismiss = allowSwipeToDismiss;
        this.allowDragInAllDirections = allowDragInAllDirections;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = (!allowDragInAllDirections) ? ItemTouchHelper.UP | ItemTouchHelper.DOWN
                : ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return allowSwipeToDismiss;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            float width = viewHolder.itemView.getWidth();
            float alpha = 1.0f - Math.abs(dX) / width;

            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        adapter.onItemMoveFinished();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
