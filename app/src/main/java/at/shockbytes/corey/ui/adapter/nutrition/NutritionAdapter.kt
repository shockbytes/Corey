package at.shockbytes.corey.ui.adapter.nutrition

import android.content.Context
import android.view.View
import android.view.ViewGroup
import at.shockbytes.corey.R
import at.shockbytes.util.adapter.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_nutrition_day.*

class NutritionAdapter(context: Context) : BaseAdapter<NutritionAdapterItem>(context) {

    fun updateData(items: List<NutritionAdapterItem>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return NutritionViewHolder(inflater.inflate(R.layout.item_nutrition_day, parent, false))
    }

    private inner class NutritionViewHolder(
            override val containerView: View
    ) : BaseAdapter<NutritionAdapterItem>.ViewHolder(containerView), LayoutContainer {

        override fun bindToView(t: NutritionAdapterItem) {
            with(t) {
                tv_item_nutrition_day_date.text = formattedDate
                tv_item_nutrition_day_balance.text = balance
            }
        }
    }

}