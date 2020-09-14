package at.shockbytes.corey.ui.adapter.nutrition

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import at.shockbytes.corey.R
import at.shockbytes.corey.common.setVisible
import at.shockbytes.corey.data.nutrition.PhysicalActivity
import at.shockbytes.util.adapter.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_nutrition_day.*
import kotlinx.android.synthetic.main.item_nutrition_day_burned.*
import kotlinx.android.synthetic.main.item_nutrition_day_intake.*
import kotlinx.android.synthetic.main.item_nutrition_day_intake_header.*
import java.lang.IllegalStateException

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

                rv_item_nutrition_day_intake.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = NutritionIntakeAdapter(context, intake)
                }

                tv_item_nutrition_day_burned_header.setVisible(burned.isNotEmpty())

                rv_item_nutrition_day_burned.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = NutritionBurnedAdapter(context, burned)
                }
            }
        }
    }

    private class NutritionIntakeAdapter(
            context: Context,
            adapterData: List<NutritionIntakeAdapterItem>
    ): BaseAdapter<NutritionIntakeAdapterItem>(context) {

        init {
            data.addAll(adapterData)
        }

        override fun getItemViewType(position: Int): Int {
            return when (data[position]) {
                is NutritionIntakeAdapterItem.Header -> R.layout.item_nutrition_day_intake_header
                is NutritionIntakeAdapterItem.Intake -> R.layout.item_nutrition_day_intake
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val view = inflater.inflate(viewType, parent, false)
            return when (viewType) {
                R.layout.item_nutrition_day_intake_header -> NutritionIntakeHeaderViewHolder(view)
                R.layout.item_nutrition_day_intake -> NutritionIntakeViewHolder(view)
                else -> throw IllegalStateException("Unknown view for view type $viewType in ${javaClass.simpleName}!")
            }
        }

        inner class NutritionIntakeHeaderViewHolder(
                override val containerView: View
        ) : BaseAdapter<NutritionIntakeAdapterItem>.ViewHolder(containerView), LayoutContainer {
            override fun bindToView(t: NutritionIntakeAdapterItem) {
                with(t as NutritionIntakeAdapterItem.Header) {
                    tv_item_nutrition_day_intake_header.text = time
                }
            }
        }

        inner class NutritionIntakeViewHolder(
                override val containerView: View
        ) : BaseAdapter<NutritionIntakeAdapterItem>.ViewHolder(containerView), LayoutContainer {
            override fun bindToView(t: NutritionIntakeAdapterItem) {
                with(t as NutritionIntakeAdapterItem.Intake) {
                    tv_item_nutrition_day_intake_title.text = entry.name
                    tv_item_nutrition_day_intake_kcal.text = context.getString(R.string.kcal_format, entry.kcal)
                }
            }
        }
    }

    private class NutritionBurnedAdapter(
            context: Context,
            burned: List<PhysicalActivity>
    ) : BaseAdapter<PhysicalActivity>(context) {

        init {
            data.addAll(burned)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return NutritionBurnedViewHolder(inflater.inflate(R.layout.item_nutrition_day_burned, parent, false))
        }

        inner class NutritionBurnedViewHolder(
                override val containerView: View
        ) : BaseAdapter<PhysicalActivity>.ViewHolder(containerView), LayoutContainer {
            override fun bindToView(t: PhysicalActivity) {
                with(t) {
                    tv_item_nutrition_day_burned_title.text = activityName(context)
                    tv_item_nutrition_day_burned_kcal.text = context.getString(R.string.kcal_format, kcal)
                }
            }
        }
    }

}