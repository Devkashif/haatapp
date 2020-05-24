package com.haatapp.app.utils;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnFlingListener implements View.OnTouchListener {
    Context context;

    public OnFlingListener(Context ctx) {
        this.context = ctx;
    }

    private final GestureDetectorCompat gdt = new GestureDetectorCompat(context, new GestureListener());

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        return gdt.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int MIN_DISTANCE = 190;
        private static final int SWIPE_VELOCITY = 250;
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > MIN_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
                onRightToLeft();
                return true;
            } else if (e2.getX() - e1.getX() > MIN_DISTANCE && Math.abs(velocityX) > SWIPE_VELOCITY) {
                onLeftToRight();
                return true;
            }
            if (e1.getY() - e2.getY() > MIN_DISTANCE && Math.abs(velocityY) > SWIPE_VELOCITY) {
                onBottomToTop();
                return true;
            } else if (e2.getY() - e1.getY() > MIN_DISTANCE && Math.abs(velocityY) > SWIPE_VELOCITY) {
                onTopToBottom();
                return true;
            }
            return false;
        }
    }

    public abstract void onRightToLeft();

    public abstract void onLeftToRight();

    public abstract void onBottomToTop();

    public abstract void onTopToBottom();

}