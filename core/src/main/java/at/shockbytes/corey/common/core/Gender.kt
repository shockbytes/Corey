package at.shockbytes.corey.common.core

enum class Gender(val acronym: String) {
    MALE("m"),
    FEMALE("f");

    companion object {

        fun of(acronym: String): Gender {
            return values().find { it.acronym == acronym }
                    ?: throw IllegalStateException("No gender with acronym $acronym found")
        }
    }
}