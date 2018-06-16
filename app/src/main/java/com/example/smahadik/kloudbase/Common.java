package com.example.smahadik.kloudbase;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

/**
 * Created by smahadik on 6/1/18.
 */

public class Common {

    public static void EnableProgressBar (FrameLayout progressBarHolder, AlphaAnimation inAnimation) {
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);

    }

    public static void DisableProgressBar(FrameLayout progressBarHolder, AlphaAnimation outAnimation) {
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.INVISIBLE);
    }




}


