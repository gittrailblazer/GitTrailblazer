package com.example.githubtrailblazer.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.example.githubtrailblazer.R;

@SuppressLint("AppCompatCustomView")
public class LoadingSpinner extends ImageView {
    private boolean isActive = false;
    private Handler handler;
    private int handlerInterval = 800;
    private Animation rotateAnimation;


    public LoadingSpinner(Context context) {
        super(context);
        init(context);
    }

    public LoadingSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public LoadingSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
    }

    public LoadingSpinner start() {
        isActive = true;
        runHandler();
        return this;
    }

    public LoadingSpinner stop() {
        isActive = false;
        return this;
    }

    private void runHandler() {
        if (handler != null) return;
        LoadingSpinner _this = this;
        handler = new Handler();
        _this.startAnimation(rotateAnimation);
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (isActive) {
                    _this.startAnimation(rotateAnimation);
                    handler.postDelayed(this, handlerInterval);
                } else {
                    handler = null;
                }
            }
        }, handlerInterval);
    }
}
