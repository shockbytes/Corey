package at.shockbytes.corey.common.core

enum class WeightUnit(val acronym: String) {
    KG("kg"),
    POUNDS("lb");

    companion object {

        fun of(acronym: String): WeightUnit {
            return values().find { it.acronym == acronym }
                    ?: throw IllegalStateException("No weight unit with acronym $acronym found")
        }
    }
}