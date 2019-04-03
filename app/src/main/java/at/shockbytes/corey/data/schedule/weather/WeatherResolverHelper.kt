package at.shockbytes.corey.data.schedule.weather


object WeatherResolverHelper {

    private const val DAYS_PER_WEEK = 7

    fun indexRelativeToWeekDay(index: Int, currentDayOfWeek: Int): Int {

        return if (index > currentDayOfWeek) {
            index - currentDayOfWeek
        } else {
            index + DAYS_PER_WEEK - currentDayOfWeek
        }
    }

}