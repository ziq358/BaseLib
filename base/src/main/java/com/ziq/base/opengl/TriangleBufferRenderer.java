package com.ziq.base.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.ziq.base.utils.PictureUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.GL_ARRAY_BUFFER;

public class TriangleBufferRenderer implements GLSurfaceView.Renderer {
    private int surfaceWidth,surfaceHeight;

    private final float vertices[] = {
            0.5f, 0.5f, 0.0f,   // 右上角
            0.5f, -0.5f, 0.0f,  // 右下角
            -0.5f, -0.5f, 0.0f, // 左下角
            -0.5f, 0.5f, 0.0f   // 左上角
    };

    private final float texCoords[] = {
            2.0f, 2.0f,   // 右上角
            2.0f, 0.0f,   // 右下角
            0.0f, 0.0f,   // 左下角
            0.0f, 2.0f    // 左上角
    };

    int indexs[] = { // 起始于0!
        0, 1, 3, // 第一个三角形
        1, 2, 3  // 第二个三角形
    };

    Context context;
    public TriangleBufferRenderer(Context context) {
        this.context = context;
    }

    int VAO;
    int VBO_vertices;
    int VBO_texCoords;
    int EBO;
    int imageTextureId1;
    int imageTextureId2;
    int mProgramId;
    int mPositionHandle;
    int mTexCoordHandle;
    int ourTextureSamplerHandle1;
    int ourTextureSamplerHandle2;

    private float[] modelMatrix = new float[16];//世界
    private float[] viewMatrix = new float[16];//视察
    private float[] projectionMatrix = new float[16];//投影

    String vertexshader =
            "#version 300 es\n" +
            "precision highp float;\n" +
            "in vec3 position; \n" +
            "in vec2 texCoord; \n" +
            "out vec4 vertexColor;\n" +
            "out vec2 TexCoord;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = projection * view * model * vec4(position, 1.0);\n" +
            "    vertexColor = vec4(0.5, 0.0, 0.0, 1.0);\n" +
            "    TexCoord = texCoord;\n" +
            "}";

    String fragmentshader =
            "#version 300 es\n" +
            "precision highp float;\n" +
            "in vec4 vertexColor;\n" +
            "in vec2 TexCoord;\n" +
            "uniform sampler2D ourTexture1;\n" +
            "uniform sampler2D ourTexture2;\n" +
            "out vec4 color;\n" +
            "void main()\n" +
            "{\n" +
            "    color = mix(texture(ourTexture1, TexCoord), texture(ourTexture2, TexCoord), 0.2);\n" +
            "}";

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        mProgramId = OpenGlUtil.createProgram(vertexshader, fragmentshader);
        if (mProgramId == 0) {
            return;
        }
        mPositionHandle = GLES30.glGetAttribLocation(mProgramId, "position");
        mTexCoordHandle = GLES30.glGetAttribLocation(mProgramId, "texCoord");
        ourTextureSamplerHandle1= GLES30.glGetUniformLocation(mProgramId,"ourTexture1");
        ourTextureSamplerHandle2= GLES30.glGetUniformLocation(mProgramId,"ourTexture2");
        if (mPositionHandle == -1) { throw new RuntimeException("Could not get attrib location for position"); }
        if (mTexCoordHandle == -1) { throw new RuntimeException("Could not get attrib location for texCoord"); }
        if (ourTextureSamplerHandle1 == -1) { throw new RuntimeException("Could not get attrib location for ourTexture1"); }
        if (ourTextureSamplerHandle2 == -1) { throw new RuntimeException("Could not get attrib location for ourTexture2"); }

        final int buffers[] = new int[3];
        GLES30.glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            int i = GLES30.glGetError();
            throw new RuntimeException("Could not create a new vertex buffer object, glGetError : "+i);
        }

        //1、VAO
        final int vaos[] = new int[1];
        GLES30.glGenVertexArrays(1, vaos, 0);
        VAO = vaos[0];
        GLES30.glBindVertexArray(VAO);

        //2、VBO
        // 把我们的顶点数组复制到一个顶点缓冲中，提供给OpenGL使用
        VBO_vertices = buffers[0];
        FloatBuffer vboBuffer = ByteBuffer.allocateDirect(
                vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        vboBuffer.position(0);
        // 把native的数据绑定保存到缓存区，注意长度为字节单位。用途是为GL_STATIC_DRAW
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, VBO_vertices);
        GLES30.glBufferData(GL_ARRAY_BUFFER, vboBuffer.capacity()*4, vboBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, 0);
        OpenGlUtil.checkGlError("glVertexAttribPointer mPositionHandle");
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        OpenGlUtil.checkGlError("glEnableVertexAttribArray mPositionHandle");
        // 告诉OpenGL 解绑缓冲区的操作。
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, 0);

        // 3. EBO 复制我们的索引数组到一个索引缓冲中，提供给OpenGL使用
        EBO = buffers[1];
        IntBuffer eboBuffer = ByteBuffer.allocateDirect(
                indexs.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(indexs);
        eboBuffer.position(0);
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, EBO);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, eboBuffer.capacity()*4, eboBuffer, GLES30.GL_STATIC_DRAW);

        //4、纹理坐标
        VBO_texCoords = buffers[2];
        FloatBuffer texCoordBuffer = ByteBuffer.allocateDirect(
                texCoords.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(texCoords);
        texCoordBuffer.position(0);
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, VBO_texCoords);
        GLES30.glBufferData(GL_ARRAY_BUFFER, texCoordBuffer.capacity()*4, texCoordBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(mTexCoordHandle, 2, GLES30.GL_FLOAT, false, 0, 0);
        OpenGlUtil.checkGlError("glVertexAttribPointer mTexCoordHandle");
        GLES30.glEnableVertexAttribArray(mTexCoordHandle);
        OpenGlUtil.checkGlError("glEnableVertexAttribArray mTexCoordHandle");
        // 告诉OpenGL 解绑缓冲区的操作。
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, 0);

        if(imageTextureId1 == 0){
            int[] textureObjectIds=new int[2];
            GLES30.glGenTextures(textureObjectIds.length ,textureObjectIds,0);
            imageTextureId1 = textureObjectIds[0];
            imageTextureId2 = textureObjectIds[1];

            if(imageTextureId1 != 0){
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId1);
                // 为当前绑定的纹理对象设置方大放小 过滤 线性过滤
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                // 为当前绑定的纹理对象设置环绕 重复
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                Bitmap bitmap = PictureUtil.loadBitmapFromAssets(context, "images/door.jpeg");
                GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0, bitmap,0);
                bitmap.recycle();
                bitmap = null;
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
            }
            if(imageTextureId2 != 0){
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId2);
                // 为当前绑定的纹理对象设置方大放小 过滤 线性过滤
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                // 为当前绑定的纹理对象设置环绕 重复
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                Bitmap bitmap = PictureUtil.loadBitmapFromAssets(context, "images/awesomeface.png");
                GLUtils.texImage2D(GLES30.GL_TEXTURE_2D,0, bitmap,0);
                bitmap.recycle();
                bitmap = null;
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D,0);
            }

        }

        GLES30.glBindVertexArray(0);

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
        GLES30.glEnable(GLES20.GL_DEPTH_TEST);
        //分成 4份，左下角 是 0,0
        GLES30.glViewport(0,0,surfaceWidth,surfaceHeight);
        GLES30.glUseProgram(mProgramId);
        //当只有一个采样时，自动把纹理赋值给片段着色器的采样器,多个采样时 需要 赋值指定
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId1);
        GLES30.glUniform1i(ourTextureSamplerHandle1, 0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId2);
        GLES30.glUniform1i(ourTextureSamplerHandle2, 1);

        //矩阵 变换
        Matrix.setIdentityM(modelMatrix,0);// 单位矩阵
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(projectionMatrix,0);
        //旋转 跟 摄像机 的位置 会影响 显示效果，有可能部分直接不显示，得调整好距离
        Matrix.rotateM(modelMatrix, 0, -45, 1.0f, 0.0f, 0.0f);
        //设置了透视投影，摄像机 的位置 需要改变
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 3.0f,
                0.0f, 0.0f,0.0f,
                0.0f, 1.0f, 0.0f);
        float ratio = (float)surfaceWidth / (float)surfaceHeight;
        //设置了投影 离 镜头 0.1 - 100 的东西能看到
        Matrix.perspectiveM(projectionMatrix, 0, 45, ratio, 0.1f, 100.0f);

        int modelLoc = GLES30.glGetUniformLocation(mProgramId, "model");
        GLES30.glUniformMatrix4fv(modelLoc, 1, false, modelMatrix, 0);
        int viewLoc = GLES30.glGetUniformLocation (mProgramId, "view");
        GLES30.glUniformMatrix4fv(viewLoc, 1, false, viewMatrix, 0);
        int projectionLoc = GLES30.glGetUniformLocation (mProgramId, "projection");
        GLES30.glUniformMatrix4fv(projectionLoc, 1, false, projectionMatrix, 0);

        //绑定vao， 绘制时 使用vao中记录的数据
        GLES30.glBindVertexArray(VAO);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);
        //解绑
        GLES30.glBindVertexArray(0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void onDestry(){
        GLES30.glDeleteProgram(mProgramId);
    }
}