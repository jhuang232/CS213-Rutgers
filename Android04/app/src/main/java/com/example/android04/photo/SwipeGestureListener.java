package com.example.android04.photo;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    // Interface to communicate swipe events to the caller
    public interface OnSwipeListener {
        void onSwipeLeft();

        void onSwipeRight();
    }

    private OnSwipeListener onSwipeListener;

    // Constructor to set the OnSwipeListener
    public SwipeGestureListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float distanceX = e2.getX() - e1.getX();
        if (Math.abs(distanceX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (distanceX > 0) {
                if (onSwipeListener != null) {
                    onSwipeListener.onSwipeRight();
                }
            } else {
                if (onSwipeListener != null) {
                    onSwipeListener.onSwipeLeft();
                }
            }
            return true;
        }
        return false;
    }
}
