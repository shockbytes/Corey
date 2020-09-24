package at.shockbytes.corey.data.nutrition.lookup


sealed class KcalLookupItem {

    abstract val dishName: String
    abstract val portionSize: String
    abstract val kcal: Int

    data class Standard(
            override val dishName: String,
            override val portionSize: String,
            override val kcal: Int
    ) : KcalLookupItem()

    data class WithImage(
            override val dishName: String,
            override val portionSize: String,
            override val kcal: Int,
            val imageUrl: String
    ) : KcalLookupItem()

}
