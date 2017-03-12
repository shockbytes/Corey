package at.shockbytes.corey.util.view;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author Martin Macheiner
 *         Date: 03.12.2015.
 */
public class DepthPageTransformer implements ViewPager.PageTransformer{

    private static final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View page, float position) {

        int pageWidth = page.getWidth();

        //All invisible views
        if(position < -1){
            page.setAlpha(0);
        }
        //Use default slide animation
        else if(position <= 0){
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
        }
        //Fade page out
        else if(position <= 1){

            page.setAlpha(1 - position);
            page.setTranslationX(pageWidth * -position);
            float scaleFactor = MIN_SCALE + (1-MIN_SCALE)*(1-Math.abs(position));
            page.setScaleY(scaleFactor);
            page.setScaleX(scaleFactor);
        }
        //Rest of invisible views
        else{
            page.setAlpha(0);
        }
    }
}
