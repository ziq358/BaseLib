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
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.GL_ARRAY_BUFFER;

public class SphereRenderer implements GLSurfaceView.Renderer {
    private int surfaceWidth,surfaceHeight;

    Context context;
    private Sphere mSphere;
    public SphereRenderer(Context context) {
        this.context = context;
        mSphere = new Sphere(18,75,150);
    }


    int VAO;
    int VBO_VERTICES;
    int VBO_TEXCOORD;
    int EBO;

    int imageTextureId;
    int mProgramId;
    int mPositionHandle;
    int mTexCoordHandle;
    int ourTextureSamplerHandle;

    private float[] modelMatrix = new float[16];//世界
    private float[] viewMatrix = new float[16];//视察
    private float[] projectionMatrix = new float[16];//投影

    public float mDeltaX = -90;// 起点  是 x = 1 的位置，中间点是x = -1 面向的方向是x=0，所以差90 度
    public float mDeltaY = 0;
    float angle = 0f;

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
            "uniform sampler2D ourTexture;\n" +
            "out vec4 color;\n" +
            "void main()\n" +
            "{\n" +
            "    color = texture(ourTexture, TexCoord);\n" +
            "}";

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

        mProgramId = OpenGlUtil.createProgram(vertexshader, fragmentshader);
        if (mProgramId == 0) {
            return;
        }
        mPositionHandle = GLES30.glGetAttribLocation(mProgramId, "position");
        mTexCoordHandle = GLES30.glGetAttribLocation(mProgramId, "texCoord");
        ourTextureSamplerHandle= GLES30.glGetUniformLocation(mProgramId,"ourTexture");
        if (mPositionHandle == -1) { throw new RuntimeException("Could not get attrib location for position"); }
        if (mTexCoordHandle == -1) { throw new RuntimeException("Could not get attrib location for texCoord"); }
        if (ourTextureSamplerHandle == -1) { throw new RuntimeException("Could not get attrib location for ourTexture"); }

        //1、VAO
        final int vaos[] = new int[1];
        GLES30.glGenVertexArrays(1, vaos, 0);
        VAO = vaos[0];
        GLES30.glBindVertexArray(VAO);

        final int buffers[] = new int[3];
        GLES30.glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            int i = GLES30.glGetError();
            throw new RuntimeException("Could not create a new vertex buffer object, glGetError : "+i);
        }
        //2、VBO
        // 把我们的顶点数组复制到一个顶点缓冲中，提供给OpenGL使用
        VBO_VERTICES = buffers[0];
        FloatBuffer mVerticesBuffer = mSphere.getVerticesBuffer();
        // 把native的数据绑定保存到缓存区，注意长度为字节单位。用途是为GL_STATIC_DRAW
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, VBO_VERTICES);
        GLES30.glBufferData(GL_ARRAY_BUFFER, mVerticesBuffer.capacity()*4, mVerticesBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(mPositionHandle, 3, GLES30.GL_FLOAT, false, 0, 0);
        OpenGlUtil.checkGlError("glVertexAttribPointer mPositionHandle");
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        OpenGlUtil.checkGlError("glEnableVertexAttribArray mPositionHandle");
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, 0);

        VBO_TEXCOORD = buffers[1];
        FloatBuffer mTexCoordinateBuffer = mSphere.getTexCoordinateBuffer();
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, VBO_TEXCOORD);
        GLES30.glBufferData(GL_ARRAY_BUFFER, mTexCoordinateBuffer.capacity()*4, mTexCoordinateBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glVertexAttribPointer(mTexCoordHandle, 2, GLES30.GL_FLOAT, false, 0, 0);
        OpenGlUtil.checkGlError("glVertexAttribPointer mTexCoordHandle");
        GLES30.glEnableVertexAttribArray(mTexCoordHandle);
        OpenGlUtil.checkGlError("glEnableVertexAttribArray mTexCoordHandle");
        GLES30.glBindBuffer(GL_ARRAY_BUFFER, 0);// 告诉OpenGL 解绑缓冲区的操作。


        // 3. EBO 复制我们的索引数组到一个索引缓冲中，提供给OpenGL使用
        EBO = buffers[2];
        ShortBuffer mIndexBuffer = mSphere.getIndexBuffer();
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, EBO);
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.capacity()*2, mIndexBuffer, GLES30.GL_STATIC_DRAW);


        if(imageTextureId == 0){
            int[] textureObjectIds=new int[1];
            GLES30.glGenTextures(textureObjectIds.length ,textureObjectIds,0);
            imageTextureId = textureObjectIds[0];
            if(imageTextureId != 0){
                GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId);
                // 为当前绑定的纹理对象设置方大放小 过滤 线性过滤
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
                // 为当前绑定的纹理对象设置环绕 重复
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_REPEAT);
                GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_REPEAT);
                Bitmap bitmap = PictureUtil.loadBitmapFromAssets(context, "images/texture_360_n.jpg");
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
        //设置深度测试，不然 后面和前面会相互覆盖
        GLES30.glEnable(GLES20.GL_DEPTH_TEST);
        //分成 4份，左下角 是 0,0
        GLES30.glViewport(0,0,surfaceWidth,surfaceHeight);
        GLES30.glUseProgram(mProgramId);
        //当只有一个采样时，自动把纹理赋值给片段着色器的采样器,多个采样时 需要 赋值指定
//        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, imageTextureId);
        GLES30.glUniform1i(ourTextureSamplerHandle, 0);
        //矩阵 变换
        Matrix.setIdentityM(modelMatrix,0);// 单位矩阵
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setIdentityM(projectionMatrix,0);
        //旋转 跟 摄像机 的位置 会影响 显示效果，有可能部分直接不显示，得调整好距离
        //转动
        Matrix.rotateM(modelMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
//        //设置了透视投影，摄像机 的位置 需要改变
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0.0f,  //位置
                0.0f, 0.0f,-1.0f, //望向的点
                0.0f, 1.0f, 0.0f);
        float ratio = (float)surfaceWidth / (float)surfaceHeight;
//        //设置了投影 离 镜头 0.1 - 100 的东西能看到
        float currentDegree= (float) (Math.toDegrees(Math.atan(1))*2);//90 视野  越大看得越多
        Matrix.perspectiveM(projectionMatrix, 0, currentDegree, ratio, 0.1f, 500.0f);

        int modelLoc = GLES30.glGetUniformLocation(mProgramId, "model");
        GLES30.glUniformMatrix4fv(modelLoc, 1, false, modelMatrix, 0);
        int viewLoc = GLES30.glGetUniformLocation (mProgramId, "view");
        GLES30.glUniformMatrix4fv(viewLoc, 1, false, viewMatrix, 0);
        int projectionLoc = GLES30.glGetUniformLocation (mProgramId, "projection");
        GLES30.glUniformMatrix4fv(projectionLoc, 1, false, projectionMatrix, 0);

        //绑定vao， 绘制时 使用vao中记录的数据
        GLES30.glBindVertexArray(VAO);
        int indicesNum = mSphere.getIndicesNum();
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indicesNum, GLES30.GL_UNSIGNED_SHORT, 0);
        //解绑
        GLES30.glBindVertexArray(0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glDisable(GLES20.GL_DEPTH_TEST);
    }

    public void onDestry(){
        GLES30.glDeleteProgram(mProgramId);
    }


    class Sphere {

        private FloatBuffer mVerticesBuffer;
        private FloatBuffer mTexCoordinateBuffer;
        private ShortBuffer mIndexBuffer;
        private int mNumIndices;
        //new Sphere(18,75,150);
        //半径   //环     //把…划成扇形
        public Sphere(float radius, int rings, int sectors) {
            final float PI = (float) Math.PI;
            final float PI_2 = (float) (Math.PI / 2);

            float R = 1f/(float)rings;
            float S = 1f/(float)sectors;
            short r, s;
            float x, y, z;

            int numPoint = (rings + 1) * (sectors + 1);

            float[] vertexs = new float[numPoint * 3];
            float[] texcoords = new float[numPoint * 2];
            short[] indices = new short[numPoint * 6];

            //map texture 2d-3d
            int t = 0, v = 0;
            for(r = 0; r < rings + 1; r++) {
                for(s = 0; s < sectors + 1; s++) {

                    texcoords[t++] = s*S;// 0 -> 1 所以 纹理是 从 下往上 ，左到右
                    texcoords[t++] = r*R;// 0 -> 1

                    x = (float) (Math.cos(2*PI * s * S) * Math.sin( PI * r * R ));
                    y = -1 * (float) Math.sin( -PI_2 + PI * r * R );
                    z = (float) (Math.sin(2*PI * s * S) * Math.sin( PI * r * R ));
                    vertexs[v++] = x * radius;
                    vertexs[v++] = y * radius;
                    vertexs[v++] = z * radius;
                }
            }

            //点是 从上端到下端 ， 一环接一环 共rings 环，每环sectors + 1个点， 每环从右边x = 1 开始环绕一周
            //new Sphere(18,75,150);为例子
            // 302 303 304 ... 451 452              第三环
            // 151 152 153 ... 300 301             第二环
            // 0    1   2  ... 149 150           第一环

            //形成的三角形是
            // 152 302 303，
            // 151 302 152，    第二条 环
            // 1 151 152，
            // 0 151 1，     第一条 环



            //glDrawElements
            int counter = 0;
            int sectorsPlusOne = sectors + 1;
            for(r = 0; r < rings; r++){
                for(s = 0; s < sectors; s++) {
                    indices[counter++] = (short) (r * sectorsPlusOne + s);       //(a)
                    indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s));    //(b)
                    indices[counter++] = (short) ((r) * sectorsPlusOne + (s+1));  // (c)
                    indices[counter++] = (short) ((r) * sectorsPlusOne + (s+1));  // (c)
                    indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s));    //(b)
                    indices[counter++] = (short) ((r+1) * sectorsPlusOne + (s+1));  // (d)
                }
            }

            // initialize vertex byte buffer for shape coordinates

            FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(
                    vertexs.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(vertexs);
            vertexBuffer.position(0);

            // initialize vertex byte buffer for shape coordinates
            FloatBuffer texBuffer = ByteBuffer.allocateDirect(
                    texcoords.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(texcoords);
            texBuffer.position(0);

            // initialize byte buffer for the draw list

            ByteBuffer dlb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 2 bytes per short)
                    indices.length * 2);
            dlb.order(ByteOrder.nativeOrder());
            ShortBuffer indexBuffer = dlb.asShortBuffer();
            indexBuffer.put(indices);
            indexBuffer.position(0);

            mTexCoordinateBuffer=texBuffer;
            mVerticesBuffer=vertexBuffer;
            mIndexBuffer = indexBuffer;
            mNumIndices=indices.length;
        }

        public FloatBuffer getVerticesBuffer() {
            return mVerticesBuffer;
        }

        public FloatBuffer getTexCoordinateBuffer() {
            return mTexCoordinateBuffer;
        }

        public ShortBuffer getIndexBuffer() {
            return mIndexBuffer;
        }

        public int getIndicesNum() {
            return mNumIndices;
        }
    }

}