package com.ziq.baselib.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;

import com.ziq.baselib.R;

public class UiDrawView extends FrameLayout {
    public UiDrawView(Context context) {
        super(context);
        init(context, null, 0);
    }


    public UiDrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public UiDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Button btn2 = findViewById(R.id.btn2);
        findViewById(R.id.btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 300f).setDuration(2000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        Log.e("ziq", "onAnimationUpdate: "+valueAnimator.getAnimatedValue());
                        btn2.setTranslationX((Float) valueAnimator.getAnimatedValue());
                    }
                });
                animator.start();
            }
        });
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Context context = getContext();
        int size = context.getResources().getDimensionPixelSize(R.dimen.dp50);
        Drawable image = context.getResources().getDrawable(R.mipmap.ic_launcher);
        int marginLeft = size * 3 + 150;
        int marginTop = size * 3 + 150;
        image.setBounds(marginLeft, marginTop, marginLeft+size, marginTop+size);
        image.draw(canvas);

    }
}
