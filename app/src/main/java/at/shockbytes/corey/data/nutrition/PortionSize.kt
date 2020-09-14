package at.shockbytes.corey.data.nutrition

import androidx.annotation.StringRes
import at.shockbytes.corey.R

enum class PortionSize(@StringRes val nameRes: Int, val code: String) {
    SMALL(R.string.portion_small, "s"),
    MEDIUM(R.string.portion_medium, "m"),
    BIG(R.string.portion_big, "l");

    companion object {

        fun fromCode(code: String): PortionSize {
            return values().find { it.code == code }
                    ?: throw IllegalStateException("Unknown portion size code $code")
        }
    }
}