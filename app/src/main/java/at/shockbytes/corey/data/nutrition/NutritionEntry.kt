package at.shockbytes.corey.data.nutrition

import at.shockbytes.corey.data.FirebaseStorable

data class NutritionEntry(
        val id: String = "",
        val name: String = "",
        val kcal: Int = -1,
        val portion: String = "",
        val time: NutritionTime = NutritionTime(),
        val date: NutritionDate = NutritionDate()
) : FirebaseStorable {

    override fun copyWithNewId(newId: String): FirebaseStorable {
        return this.copy(id = newId)
    }
}

