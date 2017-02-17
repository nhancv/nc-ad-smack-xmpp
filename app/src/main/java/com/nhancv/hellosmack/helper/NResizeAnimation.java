package com.nhancv.hellosmack.helper;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by nhancao on 2/13/17.
 */

public class NResizeAnimation extends Animation {
    private Integer startWidth;
    private Integer startHeight;
    private Integer targetWidth;
    private Integer targetHeight;
    private View view;

    /**
     * If you don't want effect to any edge, just set it to null
     * NResizeAnimation resizeAnimation = new NResizeAnimation(primaryContentView, null, 1);
     * resizeAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
     * resizeAnimation.setDuration(300);
     * primaryContentView.startAnimation(resizeAnimation);
     *
     * @param view
     * @param targetWidth
     * @param targetHeight
     */
    public NResizeAnimation(View view, Integer targetWidth, Integer targetHeight) {
        this.view = view;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        startWidth = view.getWidth();
        startHeight = view.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if (targetWidth != null) {
            int newWidth;
            if (targetWidth == LinearLayout.LayoutParams.WRAP_CONTENT) {
                view.measure(view.getLayoutParams().width, LinearLayout.LayoutParams.WRAP_CONTENT);
                newWidth = (int) (startWidth + (view.getMeasuredWidth() - startWidth) * interpolatedTime);
                view.getLayoutParams().width = newWidth;
            } else {
                newWidth = (int) (startWidth + (targetWidth - startWidth) * interpolatedTime);
            }
            view.getLayoutParams().width = newWidth;
        }
        if (targetHeight != null) {
            int newHeight;
            if (targetHeight == LinearLayout.LayoutParams.WRAP_CONTENT) {
                view.measure(view.getLayoutParams().width, LinearLayout.LayoutParams.WRAP_CONTENT);
                newHeight = (int) (startHeight + (view.getMeasuredHeight() - startHeight) * interpolatedTime);
                view.getLayoutParams().height = newHeight;
            } else {
                newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
            }
            view.getLayoutParams().height = newHeight;

        }
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}