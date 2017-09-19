package at.shockbytes.corey.util;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * @author Martin Macheiner
 *         Date: 07.09.2017.
 */

public class ViewManager {

    public static void animateDoubleTap(@NonNull final View v) {

        final Runnable action4 = new Runnable() {
            @Override
            public void run() {

                //Mouse click up
                v.animate().scaleX(1f).scaleY(1f).rotationX(0)
                        .setStartDelay(0).setDuration(200)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .start();
            }
        };
        final Runnable action3 = new Runnable() {
            @Override
            public void run() {

                //Mouse click down
                v.animate().scaleX(0.8f).scaleY(0.8f).rotationX(20)
                        .setDuration(200).setStartDelay(30)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .withEndAction(action4).start();
            }
        };

        final Runnable action2 = new Runnable() {
            @Override
            public void run() {

                //Mouse click up
                v.animate().scaleX(1f).scaleY(1f).rotationX(0)
                        .setStartDelay(0).setDuration(70)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .withEndAction(action3).start();
            }
        };
        Runnable action1 = new Runnable() {
            @Override
            public void run() {

                //Mouse click down
                v.animate().scaleX(0.8f).scaleY(0.8f).rotationX(20)
                        .setDuration(200)
                        .setInterpolator(new OvershootInterpolator(2f))
                        .withEndAction(action2).start();

            }
        };

        //Start all animations, starting with this one
        v.animate().alpha(1)
                .setStartDelay(300)
                .setInterpolator(new OvershootInterpolator(2f))
                .withEndAction(action1).start();
    }

}
