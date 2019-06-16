package com.ziq.base.glide;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

public class GlideRoundTransform extends BitmapTransformation {
  
    private static float radius = 0f;
    private static final int VERSION = 1;
    private static final String ID = "com.ziq.baselib.widget.GlideRoundTransform." + VERSION;
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    public GlideRoundTransform(int dp) {
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }  
  
    @Override  
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        return roundCrop(pool, bitmap);  
    }  
  
    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;  
  
        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);  
        if (result == null) {  
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);  
        }  
  
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);  
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        canvas.drawRoundRect(rectF, radius, radius, paint);  
        return result;  
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GlideRoundTransform;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
  
    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }  
  
}