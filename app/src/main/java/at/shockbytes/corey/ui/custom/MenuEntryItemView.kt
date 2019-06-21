package at.shockbytes.corey.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import at.shockbytes.corey.R
import kotlinx.android.synthetic.main.menu_entry_item_view.view.*

class MenuEntryItemView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.menu_entry_item_view, this)
        initializeAttributes(attrs)
    }

    private fun initializeAttributes(attrs: AttributeSet) {

        with(context.obtainStyledAttributes(attrs, R.styleable.MenuEntryItemView)) {

            getDrawable(R.styleable.MenuEntryItemView_item_icon)?.let { drawable ->
                iv_menu_entry_item_view.setImageDrawable(drawable)
            }

            getResourceId(R.styleable.MenuEntryItemView_item_title, 0).let { titleRes ->
                tv_menu_entry_item_view.setText(titleRes)
            }

            recycle()
        }
    }

    fun setTitle(title: String) {
        tv_menu_entry_item_view.text = title
    }
}