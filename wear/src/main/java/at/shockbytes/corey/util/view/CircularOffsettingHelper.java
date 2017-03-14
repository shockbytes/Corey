package at.shockbytes.corey.util.view;

import android.support.wearable.view.WearableRecyclerView;
import android.view.View;

/**
 * @author Martin Macheiner
 *         Date: 14.03.2017.
 */

public class CircularOffsettingHelper extends WearableRecyclerView.OffsettingHelper {

    @Override
    public void updateChild(View child, WearableRecyclerView parent) {
        int progress = child.getTop() / parent.getHeight();
        child.setTranslationX(-child.getHeight() * progress);
    }
}