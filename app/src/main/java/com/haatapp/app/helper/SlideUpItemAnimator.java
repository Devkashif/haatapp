package com.haatapp.app.helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Tamil on 10/4/2017.
 */

public class SlideUpItemAnimator extends SimpleItemAnimator {
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        TranslateAnimation anim =  new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f);
        anim.setDuration(500);
        holder.itemView.startAnimation(anim);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        TranslateAnimation anim =  new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f);
        anim.setDuration(500);
        holder.itemView.startAnimation(anim);
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {

    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
