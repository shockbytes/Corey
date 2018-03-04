package at.shockbytes.corey.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.wearable.view.drawer.WearableNavigationDrawer


/**
 * @author Martin Macheiner
 * Date: 23.03.2017.
 */

class CoreyNavigationAdapter(private val context: Context,
                             private val items: List<NavigationItem>,
                             private var listener: OnNavigationItemSelectedListener?)
    : WearableNavigationDrawer.WearableNavigationDrawerAdapter() {

    interface OnNavigationItemSelectedListener {

        fun onNavigationItemSelected(index: Int)
    }

    override fun getItemText(i: Int): String {
        return context.getString(items[i].text)
    }

    override fun getItemDrawable(i: Int): Drawable {
        return ContextCompat.getDrawable(context, items[i].drawable)
    }

    override fun onItemSelected(i: Int) {
        listener?.onNavigationItemSelected(i)
    }

    override fun getCount(): Int {
        return items.size
    }

    fun setOnNavigationItemSelectedListener(listener: OnNavigationItemSelectedListener) {
        this.listener = listener
    }

    class NavigationItem(@param:StringRes var text: Int, @param:DrawableRes var drawable: Int)
}
