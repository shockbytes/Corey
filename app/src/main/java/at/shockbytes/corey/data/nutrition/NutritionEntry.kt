package at.shockbytes.corey.data.nutrition

import at.shockbytes.corey.data.FirebaseStorable

data class NutritionEntry(
        val id: String,
        val name: String,
        val kcal: Int,
        val portion: String,
        val date: NutritionDate
): FirebaseStorable {

    override fun copyWithNewId(newId: String): FirebaseStorable {
        return this.copy(id = newId)
    }
}

