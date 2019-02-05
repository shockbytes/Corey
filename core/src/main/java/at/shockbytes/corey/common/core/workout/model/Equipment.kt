package at.shockbytes.corey.common.core.workout.model

enum class Equipment {
    BODYWEIGHT, PULLUP_BAR, BAR_BELL, GYM;

    companion object {
        fun names(): Array<String> = Equipment.values().map { it.name }.toTypedArray()

        fun fromIndex(index: Int): Equipment {
            return if (index < 0 || index >= Equipment.values().size) {
                throw IllegalArgumentException("Index $index must in range of [0, ${Equipment.values().size}")
            } else {
                Equipment.values()[index]
            }
        }
    }
}
