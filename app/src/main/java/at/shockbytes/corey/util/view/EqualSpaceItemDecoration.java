package at.shockbytes.corey.util.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class EqualSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int space;

    public EqualSpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = space;
        outRect.top = space;
        outRect.left = space;
        outRect.right = space;
    }
}