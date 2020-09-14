package at.shockbytes.corey.weather

import at.shockbytes.weather.util.WeatherResolverHelper
import org.assertj.core.api.Assertions.assertThat
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith

@RunWith(Theories::class)
class WeatherResolverHelperTest {

    @Theory
    fun `better name for that method`(@FromDataPoints("indexPoints") dp: IndexDataPoint) {
        val actualIdx = WeatherResolverHelper.indexRelativeToWeekDay(dp.idx, dp.dayOfWeek)
        assertThat(actualIdx).isEqualTo(dp.expectedRelativeIndex)
    }

    companion object {

        @JvmField
        @DataPoints("indexPoints")
        val diffDatePoints: Array<IndexDataPoint> = arrayOf(
                IndexDataPoint(WeekDay.WEDNESDAY.index, WeekDay.MONDAY.index, 2),
                IndexDataPoint(WeekDay.WEDNESDAY.index, WeekDay.WEDNESDAY.index, 7),
                IndexDataPoint(WeekDay.THURSDAY.index, WeekDay.WEDNESDAY.index, 1),
                IndexDataPoint(WeekDay.MONDAY.index, WeekDay.SUNDAY.index, 1),
                IndexDataPoint(WeekDay.SUNDAY.index, WeekDay.MONDAY.index, 6),
                IndexDataPoint(WeekDay.SUNDAY.index, WeekDay.TUESDAY.index, 5),
                IndexDataPoint(WeekDay.TUESDAY.index, WeekDay.FRIDAY.index, 4)
        )
    }

    enum class WeekDay(val index: Int) {
        MONDAY(0),
        TUESDAY(1),
        WEDNESDAY(2),
        THURSDAY(3),
        FRIDAY(4),
        SATURDAY(5),
        SUNDAY(6)
    }

    data class IndexDataPoint(val idx: Int, val dayOfWeek: Int, val expectedRelativeIndex: Int)
}