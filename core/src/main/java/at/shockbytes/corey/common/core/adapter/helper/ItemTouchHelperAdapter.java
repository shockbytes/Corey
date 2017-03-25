package at.shockbytes.corey.common.core.adapter.helper;

/**
 * @author Martin Macheiner
 * Date: 09.09.2015.
 */
public interface ItemTouchHelperAdapter {

    boolean onItemMove(int from, int to);

    void onItemMoveFinished();

    void onItemDismiss(int position);

}
