package com.example.visualizadorapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.Context;
import com.example.visualizadorapp.R;

public class AnimationHelper {
    
    // Fade In
    public static void fadeIn(View view) {
        fadeIn(view, 300, null);
    }
    
    public static void fadeIn(View view, long duration, Animator.AnimatorListener listener) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        if (listener != null) {
            animator.addListener(listener);
        }
        animator.start();
    }
    
    // Fade Out
    public static void fadeOut(View view) {
        fadeOut(view, 300, null);
    }
    
    public static void fadeOut(View view, long duration, Runnable onComplete) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        animator.start();
    }
    
    // Slide In from Bottom
    public static void slideInFromBottom(View view) {
        view.setVisibility(View.VISIBLE);
        view.setTranslationY(view.getHeight());
        view.animate()
            .translationY(0)
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }
    
    // Slide Out to Bottom
    public static void slideOutToBottom(View view, Runnable onComplete) {
        view.animate()
            .translationY(view.getHeight())
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .withEndAction(() -> {
                view.setVisibility(View.GONE);
                if (onComplete != null) {
                    onComplete.run();
                }
            })
            .start();
    }
    
    // Scale Animation (for buttons/cards)
    public static void scaleButton(View view) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start();
            })
            .start();
    }
    
    // Shake animation (for errors)
    public static void shake(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
        animator.setDuration(500);
        animator.start();
    }
    
    // Pulse animation (for notifications)
    public static void pulse(View view) {
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(200)
            .withEndAction(() -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .start();
            })
            .start();
    }
    
    // Rotate animation
    public static void rotate(View view, float degrees) {
        view.animate()
            .rotation(degrees)
            .setDuration(300)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .start();
    }
    
    // Cross fade between two views
    public static void crossFade(View viewOut, View viewIn) {
        fadeOut(viewOut, 200, () -> {
            fadeIn(viewIn, 200, null);
        });
    }
}
