package at.shockbytes.corey.ui.custom.selection

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import at.shockbytes.corey.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.corey_single_selection_view.view.*

class CoreySingleSelectionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val activeColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private val inactiveColor = ContextCompat.getColor(context, R.color.white)

    fun selectedItem(): CoreySingleSelectionItem {
        return data[selectedItemPosition]
    }

    private var selectedItemPosition: Int = 0

    var data: List<CoreySingleSelectionItem> = listOf()
        set(value) {
            field = value

            value.forEach { item ->
                tabs_corey_single_selection
                    .addTab(
                        tabs_corey_single_selection.newTab()
                            .apply { text = item.title }
                    )
            }
        }

    init {
        inflate(context, R.layout.corey_single_selection_view, this)

        tabs_corey_single_selection.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
                override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let { pos ->
                        selectedItemPosition = pos
                    }
                }
            })

            setTabTextColors(inactiveColor, activeColor)
            setTabIconTintResource(R.color.corey_single_selection_selector)
        }
    }

    fun selectPosition(position: Int) {
        tabs_corey_single_selection.selectTab(tabs_corey_single_selection.getTabAt(position))
    }
}