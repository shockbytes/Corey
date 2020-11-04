package at.shockbytes.corey.util

import at.shockbytes.corey.common.core.util.FindClosestDiffable
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Test

class FindClosestTest {

    @Test
    fun testFindClosest() {

        val data = listOf(
            TestClassA(0.0),
            TestClassA(1.0),
            TestClassA(2.0),
            TestClassA(3.0),
            TestClassA(4.0),
            TestClassA(5.0)
        )
        val input = TestClassA(1.4)
        val expected = TestClassA(1.0)

        val actual = data.findClosest(input, TestClassA(-1.0))

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testFindClosestExceedUpperBound() {

        val data = listOf(
            TestClassA(0.0),
            TestClassA(1.0),
            TestClassA(2.0),
            TestClassA(3.0),
            TestClassA(4.0),
            TestClassA(5.0)
        )
        val input = TestClassA(6.4)
        val expected = TestClassA(5.0)

        val actual = data.findClosest(input, TestClassA(-1.0))

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testFindClosestExceedLowerBound() {

        val data = listOf(
            TestClassA(0.0),
            TestClassA(1.0),
            TestClassA(2.0),
            TestClassA(3.0),
            TestClassA(4.0),
            TestClassA(5.0)
        )
        val input = TestClassA(-6.4)
        val expected = TestClassA(0.0)

        val actual = data.findClosest(input, TestClassA(-1.0))

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun testFindClosestDefaultValue() {

        val data = listOf<TestClassA>()
        val input = TestClassA(1.4)
        val default = TestClassA(-1.0)

        val actual = data.findClosest(input, default)

        assertThat(actual).isEqualTo(default)
    }

    private data class TestClassA(
        override val diffValue: Double
    ) : FindClosestDiffable

    @Test
    fun testFindClosestWithDateTime() {

        val data = listOf(
            TestClassB(DateTime.now().minusHours(10)),
            TestClassB(DateTime.now().plusHours(10)),
            TestClassB(DateTime.now().minusHours(4)),
            TestClassB(DateTime.now().plusHours(2)),
            TestClassB(DateTime.now().minusHours(1)),
            TestClassB(DateTime.now().plusHours(5))
        )
        val input = TestClassB(DateTime.now())
        val expected = data[4]

        val actual = data.findClosest(input, data[0])

        assertThat(actual).isEqualTo(expected)
    }

    private data class TestClassB(
        private val dateTime: DateTime,
        override val diffValue: Double = dateTime.millis.toDouble()
    ) : FindClosestDiffable
}