package com.ziq.baselib.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.ziq.baselib.R;

/**
 * @author john.
 * @since 2018/5/3.
 * Des:
 */
public class TranslateLoadingView extends androidx.appcompat.widget.AppCompatImageView {

    private UIHandler mHandler;

    private int status = 0;

    private int pointHeight;
    private int minHeight;
    private int maxHeight;
    private int dy;
    private int oneHeight;
    private int twoHeight;
    private int threeHeight;
    private boolean oneGrow = false;
    private boolean twoGrow = false;
    private boolean threeGrow = false;

    public TranslateLoadingView(Context context) {
        this(context, null);
    }

    public TranslateLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TranslateLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setWillNotDraw(false);
        mHandler = new UIHandler();
        pointHeight = (int) getContext().getResources().getDimension(R.dimen.dp14);
        minHeight = pointHeight / 8;
        maxHeight = pointHeight * 3;
        dy = (maxHeight - minHeight) / 24;

    }

    public void startAnimation() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessage(0);
        }
    }

    public void stopAnimation() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    private class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    break;
                case 1:
                    if(oneGrow){
                        oneHeight = oneHeight + dy * 2;
                        if(oneHeight >= maxHeight){
                            oneHeight = maxHeight;
                            oneGrow = false;
                        }
                    }else{
                        oneHeight = oneHeight - dy* 2;
                        if(oneHeight < minHeight){
                            oneHeight = minHeight;
                            oneGrow = true;
                        }
                    }
                    if(twoGrow){
                        twoHeight = twoHeight + dy* 2;
                        if(twoHeight >= maxHeight){
                            twoHeight = maxHeight;
                            twoGrow = false;
                        }
                    }else{
                        twoHeight = twoHeight - dy* 2;
                        if(twoHeight < minHeight){
                            twoHeight = minHeight;
                            twoGrow = true;
                        }
                    }
                    if(threeGrow){
                        threeHeight = threeHeight + dy* 2;
                        if(threeHeight >= maxHeight){
                            threeHeight = maxHeight;
                            threeGrow = false;
                        }
                    }else{
                        threeHeight = threeHeight - dy* 2;
                        if(threeHeight < minHeight){
                            threeHeight = minHeight;
                            threeGrow = true;
                        }
                    }
                    invalidate();
                    mHandler.sendEmptyMessageDelayed(1, 50);
                    break;
                case 2:
                    break;

            }
        }
    }

    public void setStatus(int status){
        mHandler.removeCallbacksAndMessages(null);
        this.status = status;
        if(status == 1){
            oneHeight = maxHeight;
            twoHeight = minHeight + dy * 8;
            threeHeight = minHeight + dy * 18;

            oneGrow = true;
            twoGrow = true;
            threeGrow  =false;
            mHandler.sendEmptyMessage(1);
        }
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();

        Paint pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        //背景
        pointPaint.setColor(Color.parseColor("#F1F1F1"));
        canvas.drawCircle(width / 2, height / 2, width / 2, pointPaint);

        if(status == 0){
            pointPaint.setColor(Color.parseColor("#10C569"));
            canvas.drawCircle(width / 2 - width /4, height / 2, pointHeight / 2, pointPaint);

            pointPaint.setColor(Color.parseColor("#FF9C1B"));
            canvas.drawCircle(width / 2, height / 2, pointHeight / 2, pointPaint);

            pointPaint.setColor(Color.parseColor("#1B8DFF"));
            canvas.drawCircle(width / 2 + width /4, height / 2, pointHeight / 2, pointPaint);
        }else if(status == 1){
            pointPaint.setStrokeWidth(pointHeight);
            pointPaint.setStrokeCap(Paint.Cap.ROUND);
            pointPaint.setColor(Color.parseColor("#10C569"));
            canvas.drawLine(width / 2 - width /4, height / 2 - oneHeight, width / 2 - width /4, height / 2 + oneHeight, pointPaint);

            pointPaint.setColor(Color.parseColor("#FF9C1B"));
            canvas.drawLine(width / 2, height / 2 - twoHeight, width / 2, height / 2 + twoHeight, pointPaint);


            pointPaint.setColor(Color.parseColor("#1B8DFF"));
            canvas.drawLine(width / 2 + width /4, height / 2 - threeHeight, width / 2+ width /4, height / 2 + threeHeight, pointPaint);

        }else if(status == 2){

        }


//        #10C569 #FF9C1B #1B8DFF

//        paint.setStrokeCap(Paint.Cap.ROUND);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeWidth(minHeight / 2);


    }
}