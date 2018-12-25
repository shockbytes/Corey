package at.shockbytes.corey.ui.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.wear.widget.drawer.WearableNavigationDrawerView


/**
 * @author  Martin Macheiner
 * Date:    23.03.2017
 */

class CoreyNavigationAdapter(private val context: Context,
                             private val items: List<NavigationItem>)
    : WearableNavigationDrawerView.WearableNavigationDrawerAdapter() {

    override fun getItemText(i: Int): String {
        return context.getString(items[i].text)
    }

    override fun getItemDrawable(i: Int): Drawable {
        return ContextCompat.getDrawable(context, items[i].drawable)!! // Should never be null!!!
    }

    override fun getCount(): Int {
        return items.size
    }

    class NavigationItem(@param:StringRes var text: Int, @param:DrawableRes var drawable: Int)
}
