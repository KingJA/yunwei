package com.tdr.yunwei.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * Created by Administrator on 2016/4/7.
 */
public class AnimatorUtil {


    /**
     * 方法: ShowAnimator 动画出现的高度
     */
    public static ValueAnimator createHeightAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public static void ShowAnimator(final View view) {
        view.setVisibility(View.VISIBLE);
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, heightSpec);
        int height = view.getMeasuredHeight();
        ValueAnimator animator = createHeightAnimator(view, 0, height);
        animator.setDuration(500);
        animator.start();

    }

    /**
     * 方法: animateCollapsing <p>
     * 描述: 收缩动画 <p>
     */
    public static void HideAnimator(final View view) {
        int height = view.getHeight();
        ValueAnimator animator = createHeightAnimator(view, height, 0);
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();

    }


    //定义从右侧进入的动画效果
    public static Animation inFromRightAnimation() {
        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    //定义从左侧进入的动画效果
    public static Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
}
