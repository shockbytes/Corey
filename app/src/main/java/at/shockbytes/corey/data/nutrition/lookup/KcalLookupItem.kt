package at.shockbytes.corey.data.nutrition.lookup

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class KcalLookupItem : Parcelable {

    abstract val dishName: String
    abstract val portionSize: String
    abstract val kcal: Int

    val formattedKcal: String
        get() = kcal.toString()

    @Parcelize
    data class Standard(
        override val dishName: String,
        override val portionSize: String,
        override val kcal: Int
    ) : KcalLookupItem()

    @Parcelize
    data class WithImage(
        override val dishName: String,
        override val portionSize: String,
        override val kcal: Int,
        val imageUrl: String?
    ) : KcalLookupItem()
}
