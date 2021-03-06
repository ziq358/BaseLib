package com.ziq.base.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.ziq.base.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FullViewRenderer implements GLSurfaceView.Renderer {
    private int surfaceWidth,surfaceHeight;

    private int imageTextureId;
    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mTexCoordinateBuffer;
    private final float TRIANGLES_DATA_CW[] = {
            -1.0f, -1.0f, 0f, //LD
            -1.0f, 1.0f, 0f,  //LU
            1.0f, -1.0f, 0f,  //RD
            1.0f, 1.0f, 0f    //RU
    };

    public final float TEXTURE_NO_ROTATION[] = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };

    protected float[] projectionMatrix = new float[16];


    private int mProgramId;
    private int mPositionHandle;
    private int mTextureCoordinateHandle;
    private int uMVPMatrixHandle;
    private int sTextureSamplerHandle;




    Context context;
    public FullViewRenderer(Context context) {
        this.context = context;

        mVerticesBuffer = ByteBuffer.allocateDirect(
                TRIANGLES_DATA_CW.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TRIANGLES_DATA_CW);
        mVerticesBuffer.position(0);

        mTexCoordinateBuffer = ByteBuffer.allocateDirect(
                TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(TEXTURE_NO_ROTATION);
        mTexCoordinateBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mProgramId = OpenGlUtil.createProgram(context,R.raw.vertexshader, R.raw.fragmentshader);
        if (mProgramId == 0) {
            return;
        }
        mPositionHandle = GLES30.glGetAttribLocation(mProgramId, "position");
        OpenGlUtil.checkGlError("glGetAttribLocation position");
        if (mPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for position");
        }
        mTextureCoordinateHandle = GLES30.glGetAttribLocation(mProgramId, "texcoord");
        OpenGlUtil.checkGlError("glGetAttribLocation aTextureCoord");
        if (mTextureCoordinateHandle == -1) {
            throw new RuntimeException("Could not get attrib location for texcoord");
        }

        sTextureSamplerHandle= GLES30.glGetUniformLocation(mProgramId,"s_texture");
        OpenGlUtil.checkGlError("glGetUniformLocation uniform s_texture");

        uMVPMatrixHandle= GLES30.glGetUniformLocation(mProgramId,"uMVPMatrix");
        OpenGlUtil.checkGlError("glGetUniformLocation uMVPMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int surfaceWidth, int surfaceHeight) {
        this.surfaceWidth = surfaceWidth;
        this.surfaceHeight = surfaceHeight;
    }

    @Override
    public void onDrawFrame(GL10 gl10){
        onDrawFrame();
    }

    Bitmap bitmap;
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void onDrawFrame() {
        if(imageTextureId == 0){
            int[] textureObjectIds=new int[1];
            GLES30.glGenTextures(1,textureObjectIds,0);
            if (textureObjectIds[0]!=0){
                imageTextureId = textureObjectIds[0];
            }

        }

        if (imageTextureId == 0) {
            return;
        }

        if(bitmap != null){
            //对imageTextureId 进行配置
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId);
            // 为当前绑定的纹理对象设置方大放小 过滤 线性过滤
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
            // 为当前绑定的纹理对象设置环绕 重复
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
            GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0,bitmap,0);
            bitmap.recycle();
            bitmap = null;
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
        }


        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);

        GLES30.glUseProgram(mProgramId);
        Matrix.setIdentityM(projectionMatrix,0);

        GLES30.glViewport(0,0,surfaceWidth,surfaceHeight);
        GLES30.glEnable(GLES30.GL_BLEND);
//        OpenGL glBlendFunc() 设置颜色混合 透明度叠加计算
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId);
        GLES30.glUniform1i(sTextureSamplerHandle, 0);

        mTexCoordinateBuffer.position(0);
        GLES30.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES30.GL_FLOAT, false, 0, mTexCoordinateBuffer);
        OpenGlUtil.checkGlError("glVertexAttribPointer mTextureCoordinateHandle");
        GLES30.glEnableVertexAttribArray(mTextureCoordinateHandle);
        OpenGlUtil.checkGlError("glEnableVertexAttribArray mTextureCoordinateHandle");

        mVerticesBuffer.position(0);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, mVerticesBuffer);
        OpenGlUtil.checkGlError("glVertexAttribPointer mPositionHandle");
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        OpenGlUtil.checkGlError("glEnableVertexAttribArray mPositionHandle");

        Matrix.setIdentityM(projectionMatrix,0);
        GLES30.glUniformMatrix4fv(uMVPMatrixHandle, 1, false, projectionMatrix, 0);

        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);

        GLES30.glDisable(GLES30.GL_BLEND);
    }

    public void onDestry(){
        GLES30.glDeleteProgram(mProgramId);
    }
}