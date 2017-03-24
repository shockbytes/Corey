package at.shockbytes.corey.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.wearable.view.drawer.WearableNavigationDrawer;

import java.util.List;

/**
 * @author Martin Macheiner
 *         Date: 23.03.2017.
 */

public class CoreyNavigationAdapter extends WearableNavigationDrawer.WearableNavigationDrawerAdapter {

    public interface OnNavigationItemSelectedListener {

        void onNavigationItemSelected(int index);
    }


    private OnNavigationItemSelectedListener listener;

    private List<NavigationItem> items;

    private Context context;

    public CoreyNavigationAdapter(@NonNull Context context,
                                  List<NavigationItem> items,
                                  OnNavigationItemSelectedListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public String getItemText(int i) {
        return context.getString(items.get(i).text);
    }

    @Override
    public Drawable getItemDrawable(int i) {
        return ContextCompat.getDrawable(context, items.get(i).drawable);
    }

    @Override
    public void onItemSelected(int i) {

        if (listener != null) {
            listener.onNavigationItemSelected(i);
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
        this.listener = listener;
    }

    public static class NavigationItem {

        protected int text;
        protected int drawable;

        public NavigationItem(@StringRes int text, @DrawableRes int drawable) {
            this.text = text;
            this.drawable = drawable;
        }

    }
}
