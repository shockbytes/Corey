package at.shockbytes.corey.util.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * @author Martin Macheiner
 * Date: 19.03.2015.
 */
public class NonSwipeableViewPager extends ViewPager{

    private static int DURATION = 1000;

    private NonSwipeableScroller mScroller;

    public NonSwipeableViewPager(Context context) {
        super(context);
        initialize();
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize(){

        //Initilialize Scroller
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new NonSwipeableScroller(getContext(), (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void makeFancyPageTransformation(){
        setPageTransformer(true, new DepthPageTransformer());
    }

    public void setScrollDuration(int duration){
        DURATION = duration;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        return false;
    }

    private class NonSwipeableScroller extends Scroller{

        public NonSwipeableScroller(Context context) {
            super(context);
        }

        public NonSwipeableScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public NonSwipeableScroller(Context context, Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, DURATION);
        }
    }

}
