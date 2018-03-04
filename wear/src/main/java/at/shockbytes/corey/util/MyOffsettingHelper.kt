package at.shockbytes.corey.util

import android.support.wearable.view.DefaultOffsettingHelper
import android.support.wearable.view.WearableRecyclerView
import android.view.View

class MyOffsettingHelper : DefaultOffsettingHelper() {

    private var mProgressToCenter: Float = 0.toFloat()

    override fun updateChild(child: View, parent: WearableRecyclerView) {
        super.updateChild(child, parent)


        // Figure out % progress from top to bottom
        val centerOffset = child.height.toFloat() / 2.0f / parent.height.toFloat()
        val yRelativeToCenterOffset = child.y / parent.height + centerOffset

        // Normalize for center
        mProgressToCenter = Math.abs(0.5f - yRelativeToCenterOffset)
        // Adjust to the maximum scale
        mProgressToCenter = Math.min(mProgressToCenter, MAX_ICON_PROGRESS)

        child.scaleX = 1 - mProgressToCenter
        child.scaleY = 1 - mProgressToCenter
    }

    companion object {

        /** How much should we scale the icon at most.  */
        private const val MAX_ICON_PROGRESS = 0.65f
    }
}