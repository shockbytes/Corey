package at.shockbytes.corey.workout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Macheiner
 *         Date: 15.03.2017.
 */

public class PulseLogger {

    private List<Integer> list;

    public PulseLogger() {
        list = new ArrayList<>();
    }

    public void logPulse(int pulse) {
        if (pulse > 0) {
            list.add(pulse);
        }
    }

    public int getAveragePulse(boolean resetBuffer) {

        int sum = 0;

        if (list.size() == 0) {
            return 0;
        }

        for (Integer i : list) {
            sum += i;
        }
        int avg = sum / list.size();

        if (resetBuffer) {
            reset();
        }

        return avg;
    }

    public void reset() {
        list.clear();
    }

}
