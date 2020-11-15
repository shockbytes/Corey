package at.shockbytes.corey.data.nutrition

import at.shockbytes.corey.common.core.CoreyDate
import at.shockbytes.corey.data.firebase.FirebaseStorable
import com.google.firebase.database.Exclude

data class NutritionEntry(
    val id: String = "",
    val name: String = "",
    val kcal: Int = -1,
    val portionCode: String = "",
    val timeCode: String = "",
    val date: CoreyDate = CoreyDate()
) : FirebaseStorable {

    @get:Exclude
    val time: NutritionTime
        get() = NutritionTime.fromCode(timeCode)

    @get:Exclude
    val portion: PortionSize
        get() = PortionSize.fromCode(portionCode)

    override fun copyWithNewId(newId: String): FirebaseStorable {
        return this.copy(id = newId)
    }
}