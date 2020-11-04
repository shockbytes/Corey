package at.shockbytes.corey.ui.adapter.nutrition

import android.content.Context
import android.view.View
import android.view.ViewGroup
import at.shockbytes.core.image.ImageLoader
import at.shockbytes.corey.R
import at.shockbytes.corey.data.nutrition.lookup.KcalLookupItem
import at.shockbytes.util.adapter.BaseAdapter
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_nutrition_lookup_picker.*

class NutritionLookupAdapter(
    context: Context,
    private val imageLoader: ImageLoader,
    onItemClickCallback: (KcalLookupItem) -> Unit
) : BaseAdapter<KcalLookupItem>(
    context,
    onItemClickListener = object : OnItemClickListener<KcalLookupItem> {
        override fun onItemClick(content: KcalLookupItem, position: Int, v: View) {
            onItemClickCallback(content)
        }
    }
) {

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is KcalLookupItem.Standard -> throw UnsupportedOperationException("Standard ViewHolder not supported")
            is KcalLookupItem.WithImage -> R.layout.item_nutrition_lookup_picker
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<KcalLookupItem> {
        return when (viewType) {
            R.layout.item_nutrition_lookup_picker -> {
                ImageLookupViewHolder(inflater.inflate(viewType, parent, false))
            }
            else -> throw UnsupportedOperationException("Standard ViewHolder not supported")
        }
    }

    inner class ImageLookupViewHolder(
        override val containerView: View
    ) : BaseAdapter.ViewHolder<KcalLookupItem>(containerView), LayoutContainer {

        override fun bindToView(content: KcalLookupItem, position: Int) {
            with(content as KcalLookupItem.WithImage) {

                tv_nutrition_lookup_name.text = dishName
                tv_nutrition_lookup_portion.text = context.getString(R.string.kcal_format_w_portion, kcal, portionSize)

                imageUrl?.let { imgUrl ->
                    imageLoader
                        .loadImageWithCornerRadius(
                            context,
                            url = imgUrl,
                            target = iv_nutrition_lookup_icon,
                            cornerDimension = context.resources.getDimension(R.dimen.dish_lookup_radius).toInt()
                        )
                }
            }
        }
    }
}