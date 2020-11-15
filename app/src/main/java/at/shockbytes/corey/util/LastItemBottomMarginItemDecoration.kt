package at.shockbytes.corey.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Apply equal margin to a RecyclerViewItem
 */
class LastItemBottomMarginItemDecoration(
    private val lastItemBottomMargin: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        val position = parent.getChildLayoutPosition(view)
        if (position == 0) {
            outRect.bottom = lastItemBottomMargin
        }
    }
}