package at.shockbytes.corey.common.core.util;

import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;


/**
 * @author Martin Macheiner
 *         Date: 10.09.2017.
 */

public class RunUtils {

    public static String calculatePace(long timeInMs, double distance) {

        if (distance <= 0) {
            return "-:--";
        }
        long kmMillis = (long) (timeInMs / distance);
        return formatPaceMillisToString(kmMillis);
    }

    public static int calculateCaloriesBurned(double distance, double weightOfRunner) {
        double burned = distance * weightOfRunner * 1.036;
        return (int) Math.floor(burned);
    }

    private static String formatPaceMillisToString(long kmMillis) {

        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .minimumPrintedDigits(2)
                .appendMinutes()
                .appendSeparator(":")
                .appendSeconds()
                .toFormatter();

        PeriodType minutesSeconds = PeriodType.time()
                .withMillisRemoved()
                .withHoursRemoved();

        Period kmPeriod = new Period(kmMillis, minutesSeconds);
        return formatter.print(kmPeriod);
    }

}
