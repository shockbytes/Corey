package at.shockbytes.corey.common.core.util;

import java.util.Comparator;

import at.shockbytes.corey.common.core.workout.model.Workout;

/**
 * @author Martin Macheiner
 *         Date: 29.03.2017.
 */

public class WorkoutNameComparator implements Comparator<Workout> {

    @Override
    public int compare(Workout workout, Workout t1) {
        return workout.getDisplayableName().compareToIgnoreCase(t1.getDisplayableName());
    }
}
