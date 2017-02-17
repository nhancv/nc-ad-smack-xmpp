package com.nhancv.hellosmack.helper;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Created by nhancao on 2/14/17.
 */

public class NZoomImageView extends ImageView {

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int CLICK = 3;

    private int mode = NONE;
    private Matrix matrix = new Matrix();

    private PointF lastTouch = new PointF();
    private PointF startTouch = new PointF();
    private float minScale = 1f;
    private float maxScale = 4f;
    private float[] criticPoints;

    private float scale = 1f;
    private float right;
    private float bottom;
    private float originalBitmapWidth;
    private float originalBitmapHeight;

    private ScaleGestureDetector scaleDetector;

    public NZoomImageView(Context context) {
        super(context);
        init(context);
    }

    public NZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int bmHeight = getBmHeight();
        int bmWidth = getBmWidth();

        float width = getMeasuredWidth();
        float height = getMeasuredHeight();
        float scale = 1.5f;

        // If image is bigger then display fit it to screen.
        if (width < bmWidth || height < bmHeight) {
            scale = width > height ? height / bmHeight : width / bmWidth;
        }

        matrix.setScale(scale, scale);
        this.scale = 1f;

        originalBitmapWidth = scale * bmWidth;
        originalBitmapHeight = scale * bmHeight;

        // Center the image
        float redundantYSpace = (height - originalBitmapHeight);
        float redundantXSpace = (width - originalBitmapWidth);

        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);

        setImageMatrix(matrix);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        matrix.getValues(criticPoints);
        float translateX = criticPoints[Matrix.MTRANS_X];
        float translateY = criticPoints[Matrix.MTRANS_Y];
        PointF currentPoint = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            //when one finger is touching
            //set the mode to DRAG
            case MotionEvent.ACTION_DOWN:
                lastTouch.set(event.getX(), event.getY());
                startTouch.set(lastTouch);
                mode = DRAG;
                break;
            //when two fingers are touching
            //set the mode to ZOOM
            case MotionEvent.ACTION_POINTER_DOWN:
                lastTouch.set(event.getX(), event.getY());
                startTouch.set(lastTouch);
                mode = ZOOM;
                break;
            //when a finger moves
            //If mode is applicable move image
            case MotionEvent.ACTION_MOVE:

                //if the mode is ZOOM or
                //if the mode is DRAG and already zoomed
                if (mode == ZOOM || (mode == DRAG && scale > minScale)) {

                    // region . Move  image.

                    float deltaX = currentPoint.x - lastTouch.x;// x difference
                    float deltaY = currentPoint.y - lastTouch.y;// y difference
                    float scaleWidth = Math.round(originalBitmapWidth * scale);// width after applying current scale
                    float scaleHeight = Math.round(originalBitmapHeight * scale);// height after applying current scale

                    // Move image to lef or right if its width is bigger than display width
                    if (scaleWidth > getWidth()) {
                        if (translateX + deltaX > 0) {
                            deltaX = -translateX;
                        } else if (translateX + deltaX < -right) {
                            deltaX = -(translateX + right);
                        }
                    } else {
                        deltaX = 0;
                    }
                    // Move image to up or bottom if its height is bigger than display height
                    if (scaleHeight > getHeight()) {
                        if (translateY + deltaY > 0) {
                            deltaY = -translateY;
                        } else if (translateY + deltaY < -bottom) {
                            deltaY = -(translateY + bottom);
                        }
                    } else {
                        deltaY = 0;
                    }

                    //move the image with the matrix
                    matrix.postTranslate(deltaX, deltaY);
                    //set the last touch location to the current
                    lastTouch.set(currentPoint.x, currentPoint.y);

                    // endregion . Move image .
                }
                break;
            //first finger is lifted
            case MotionEvent.ACTION_UP:
                mode = NONE;
                int xDiff = (int) Math.abs(currentPoint.x - startTouch.x);
                int yDiff = (int) Math.abs(currentPoint.y - startTouch.y);
                if (xDiff < CLICK && yDiff < CLICK)
                    performClick();
                break;
            // second finger is lifted
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        setImageMatrix(matrix);
        invalidate();
        return true;
    }

    private void init(Context context) {
        super.setClickable(true);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        criticPoints = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
    }

    private int getBmWidth() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return 0;
    }

    private int getBmHeight() {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return 0;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mode = ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float newScale = scale * scaleFactor;
            if (newScale < maxScale && newScale > minScale) {
                scale = newScale;
                float width = getWidth();
                float height = getHeight();
                right = (originalBitmapWidth * scale) - width;
                bottom = (originalBitmapHeight * scale) - height;

                float scaledBitmapWidth = originalBitmapWidth * scale;
                float scaledBitmapHeight = originalBitmapHeight * scale;

                if (scaledBitmapWidth <= width || scaledBitmapHeight <= height) {
                    matrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2);
                } else {
                    matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
                }
            }
            return true;
        }
    }

}
