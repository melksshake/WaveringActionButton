package com.natalia.melkonyan.waveringactionbutton;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;

public class WaveringActionButton implements View.OnTouchListener {
    private final Context context;
    private final View viewToTouch;
    private final View viewToMove;
    private final View viewToPulse;
    private final View.OnClickListener onClickListener;

    private ObjectAnimator alphaAnimator;
    private ObjectAnimator scaleAnimator;

    private int screenWidth;
    private int screenHeight;

    private float dX = 0.0f;
    private float dY = 0.0f;
    private float finalX;
    private int deltaX, deltaY, x = 0, y = 0;

    public WaveringActionButton(WindowManager windowManager, Context context, View viewToTouch, View viewToMove,
                                View viewToPulse, View.OnClickListener onClickListener) {
        this.context = context;
        this.viewToTouch = viewToTouch;
        this.viewToMove = viewToMove;
        this.viewToPulse = viewToPulse;
        this.onClickListener = onClickListener;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        this.viewToTouch.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        viewToMove.performClick();
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = viewToMove.getX() - motionEvent.getRawX();
                dY = viewToMove.getY() - motionEvent.getRawY();

                x = (int) viewToMove.getX();
                y = (int) viewToMove.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                viewToMove.setX(motionEvent.getRawX() + dX);
                viewToMove.setY(motionEvent.getRawY() + dY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                deltaX = (int) viewToMove.getX() - x;
                deltaY = (int) viewToMove.getY() - y;

                if (Math.abs(deltaX) <= 3 || Math.abs(deltaY) <= 3) {
                    onClickListener.onClick(viewToTouch);
                } else {
                    checkViewVerticalPosition(viewToMove, motionEvent, dY);
                    finalX = getFinalViewHorizontalPosition(motionEvent);
                    animatePopupViewStickingToBounds(finalX, viewToMove);
                }
                break;
            default:
                return false;
        }
        return true;
    }

    private void animatePopupViewStickingToBounds(float toX, View viewToAnimate) {
        SpringAnimation animation = new SpringAnimation(viewToAnimate, SpringAnimation.X);
        SpringForce spring = new SpringForce();
        spring.setFinalPosition(toX);
        spring.setStiffness(SpringForce.STIFFNESS_LOW);
        spring.setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY);
        animation.setSpring(spring);
        animation.start();
        animation.addEndListener((animation1, canceled, value, velocity) -> viewToTouch.setEnabled(true));
    }

    private void checkViewVerticalPosition(View view, MotionEvent motionEvent, float dY) {
        if (view.getY() < 0) {
            view.setY(0);
        } else if (motionEvent.getRawY() > (screenHeight - context.getResources().getDimension(R.dimen.window_size))) {
            view.setY(screenHeight - context.getResources().getDimension(R.dimen.window_size));
        } else {
            view.setY(motionEvent.getRawY() + dY);
        }
    }

    private int getFinalViewHorizontalPosition(MotionEvent motionEvent) {
        if ((int) motionEvent.getRawX() > screenWidth / 2) {
            return screenWidth - (int) context.getResources().getDimension(R.dimen.window_size)
                    + (int) context.getResources().getDimension(
                    R.dimen.size_to_hide);
        } else {
            return -(int) context.getResources().getDimension(R.dimen.size_to_hide);
        }
    }

    private void initPulseAnimator(View viewToPulse) {
        scaleAnimator = ObjectAnimator.ofPropertyValuesHolder(
                viewToPulse,
                PropertyValuesHolder.ofFloat("scaleX", .8f),
                PropertyValuesHolder.ofFloat("scaleY", .8f));
        scaleAnimator.setDuration(500);
        scaleAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        scaleAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        alphaAnimator = ObjectAnimator.ofFloat(viewToPulse, "alpha", .05f);
        alphaAnimator.setDuration(500);
        alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ObjectAnimator.REVERSE);

        viewToPulse.setAlpha(0.5f);
        alphaAnimator.start();
        scaleAnimator.start();
    }

    public void start() {
        initPulseAnimator(this.viewToPulse);
    }
}
