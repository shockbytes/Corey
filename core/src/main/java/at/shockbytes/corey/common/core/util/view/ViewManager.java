package at.shockbytes.corey.common.core.util.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;

public class ViewManager {

	private static final float ANIMATION_TIME_SCALE = 1.5f;
	
	public static Bitmap createStringBitmap(int width, int color, String text) {

		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(width, width, config);

		//Text paint settings
		Paint tPt = new Paint();
		tPt.setAntiAlias(true);
		tPt.setSubpixelText(true);
		tPt.setColor(Color.WHITE);
		tPt.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
		tPt.setTextAlign(Paint.Align.CENTER);
		tPt.setTextSize(width / 2);

		Canvas canvas = new Canvas(bmp);
		canvas.drawColor(color);

		canvas.drawText(text, width / 2,
				width / 2 - ((tPt.descent() + tPt.ascent()) / 2), tPt);

		return bmp;
	}

	public static void animateEmptyField(View v) {

		ObjectAnimator anim = ObjectAnimator
				.ofFloat(v, View.ROTATION, 0, 8, -8);
		anim.setRepeatCount(1);
		anim.setRepeatMode(ValueAnimator.REVERSE);
		anim.setDuration(400);
		anim.start();
	}

	public static void expand(final View v) {
		expand(v, 300, null);
	}
	
	public static void expand(final View v, int duration, Interpolator interpolator){
				
		v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		final int targetHeight = v.getMeasuredHeight();

		v.getLayoutParams().height = 0;
		v.setVisibility(View.VISIBLE);

		Animation a = new Animation() {

			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				
				v.getLayoutParams().height = (interpolatedTime == 1) ? LayoutParams.WRAP_CONTENT
						: (int) (targetHeight * interpolatedTime);
				v.requestLayout();
			}

			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		
		
		if(interpolator != null){
			a.setInterpolator(interpolator);
		}
		
		if(duration <= 0){
			a.setDuration((int)((ANIMATION_TIME_SCALE*targetHeight) / v.getContext().getResources().getDisplayMetrics().density )); 
		}
		else{
			a.setDuration(duration);
		}
		
		v.startAnimation(a);
		
	}
	
	public static void collapse(final View v){
		collapse(v, 300, null);
	}

	public static void collapse(final View v, int duration, Interpolator interpolator){
		
		final int initialHeight = v.getMeasuredHeight();
		
		Animation a = new Animation() {
			
			@Override
			protected void applyTransformation(float interpolatedTime,
					Transformation t) {
				
				if(interpolatedTime == 1){
					v.setVisibility(View.GONE);
				}
				else{
					
					v.getLayoutParams().height = initialHeight - (int)(initialHeight*interpolatedTime);
					v.requestLayout();
				}
			}
			
			@Override
			public boolean willChangeBounds() {
				return true;
			}
		};
		
		if(interpolator != null){
			a.setInterpolator(interpolator);
		}
		
		if(duration <= 0){
			a.setDuration((int)((ANIMATION_TIME_SCALE*initialHeight) / v.getContext().getResources().getDisplayMetrics().density )); 
		}
		else {
			a.setDuration(duration);
		}
		v.startAnimation(a);		
	}

    public static void backgroundColorTransition(final View v, final int from, final int to) {

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                //Get current position
                float position = animation.getAnimatedFraction();

                //Blend colors and apply to bars
                int blended = blendColors(from, to, position);
                v.setBackground(new ColorDrawable(blended));
            }
        });
        anim.setDuration(250).start();
    }

    private static int blendColors(int from, int to, float ratio) {

        final float inverseRatio = 1f - ratio;

        float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }

}
