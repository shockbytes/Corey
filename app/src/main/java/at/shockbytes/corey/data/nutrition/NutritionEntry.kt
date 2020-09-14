package at.shockbytes.corey.data.nutrition

import at.shockbytes.corey.data.CoreyDate
import at.shockbytes.corey.data.FirebaseStorable

data class NutritionEntry(
        val id: String = "",
        val name: String = "",
        val kcal: Int = -1,
        val portion: String = "", // TODO Not hardcoded string
        val time: NutritionTime = NutritionTime(), // TODO Not hardcoded string
        val date: CoreyDate = CoreyDate()
) : FirebaseStorable {

    override fun copyWithNewId(newId: String): FirebaseStorable {
        return this.copy(id = newId)
    }
}

