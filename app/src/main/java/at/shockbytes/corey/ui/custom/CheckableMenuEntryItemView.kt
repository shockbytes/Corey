package at.shockbytes.corey.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import at.shockbytes.corey.R
import kotlinx.android.synthetic.main.checkable_menu_entry_item_view.view.*

class CheckableMenuEntryItemView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    var isChecked: Boolean = false
        set(value) {
            field = value
            switch_checkable_menu_entry_item_view.isChecked = value
        }

    init {
        inflate(context, R.layout.checkable_menu_entry_item_view, this)
        initializeAttributes(attrs)

        layout_checkable_menu_entry_item_view.setOnClickListener {
            switch_checkable_menu_entry_item_view.toggle()
        }
    }

    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        switch_checkable_menu_entry_item_view.setOnCheckedChangeListener { _, isChecked ->
            listener.invoke(isChecked)
        }
    }

    private fun initializeAttributes(attrs: AttributeSet) {

        with(context.obtainStyledAttributes(attrs, R.styleable.CheckableMenuEntryItemView)) {

            getDrawable(R.styleable.CheckableMenuEntryItemView_checkable_item_icon)?.let { drawable ->
                iv_checkable_menu_entry_item_view.setImageDrawable(drawable)
            }

            getResourceId(R.styleable.CheckableMenuEntryItemView_checkable_item_title, 0).let { titleRes ->
                tv_checkable_menu_entry_item_view.setText(titleRes)
            }

            getBoolean(R.styleable.CheckableMenuEntryItemView_checkable_item_checked, true).let { checked ->
                switch_checkable_menu_entry_item_view.isChecked = checked
            }

            recycle()
        }
    }
}