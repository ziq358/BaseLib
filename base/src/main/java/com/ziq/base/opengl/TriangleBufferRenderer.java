package com.ziq.base.opengl;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.GL_ARRAY_BUFFER;

public class TriangleBufferRenderer implements GLSurfaceView.Renderer {
    private int surfaceWidth,surfaceHeight;

    private final float vertices[] = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f,  0.5f, 0.0f
    };

    public TriangleBufferRenderer(Context context) {
    }

    int VBO;
    int mProgramId;
    int mPositionHandle;

    String vertexshader =
            "attribute vec4 position;\n" +
            "void main() {\n" +
            "    gl_Position = position;\n" +
            "}\n";

    String fragmentshader =
            "void main() {\n" +
            "    gl_FragColor = vec4(1.0, 0.5, 0.2, 1.0);\n" +
            "}\n";

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        mProgramId = OpenGlUtil.createProgram(vertexshader, fragmentshader);
        if (mProgramId == 0) {
            return;
        }
        mPositionHandle = GLES30.glGetAttribLocation(mProgramId, "position");
        OpenGlUtil.checkGlError("glGetAttribLocation position");
        if (mPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for position");
        }


        // 第一步，我们向OpenGL服务端申请创建缓冲区
        final int buffers[] = new int[1];
        GLES30.glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            int i = GLES30.glGetError();
            throw new RuntimeException("Could not create a new vertex buffer object, glGetError : "+i);
        }
        // 保存申请返回的缓冲区标示
        VBO = buffers[0];
        // 绑定缓冲区 为 数组缓存
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, VBO);
        FloatBuffer mVerticesBuffer = ByteBuffer.allocateDirect(
                vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        mVerticesBuffer.position(0);
        // 把native的数据绑定保存到缓存区，注意长度为字节单位。用途是为GL_STATIC_DRAW
        GLES30.glBufferData(GL_ARRAY_BUFFER, mVerticesBuffer.capacity()*4, mVerticesBuffer, GLES30.GL_STATIC_DRAW);
        // 告诉OpenGL 解绑缓冲区的操作。
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, 0);


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

    public void onDrawFrame() {

        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);
        //分成 4份，左下角显示
        GLES30.glViewport(0,0,surfaceWidth / 2,surfaceHeight/2);
        GLES30.glUseProgram(mProgramId);

        GLES30.glBindBuffer(GL_ARRAY_BUFFER,VBO);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, 0);
        OpenGlUtil.checkGlError("glVertexAttribPointer mPositionHandle");
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        OpenGlUtil.checkGlError("glEnableVertexAttribArray mPositionHandle");
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 3);
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void onDestry(){
        GLES30.glDeleteProgram(mProgramId);
    }
}