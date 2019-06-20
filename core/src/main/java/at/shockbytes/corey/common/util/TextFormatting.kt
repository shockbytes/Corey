package at.shockbytes.corey.common.util

object TextFormatting {

    /**
     * Take an [hour] and transform it to format hh:mm
     */
    fun formatHourToHourAndMinuteFormat(hour: Int): String {
        return if (hour < 10) {
            "0$hour:00"
        } else {
            "$hour:00"
        }
    }
}