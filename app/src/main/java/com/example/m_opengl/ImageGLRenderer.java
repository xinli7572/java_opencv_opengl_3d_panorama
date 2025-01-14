package com.example.m_opengl;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ImageGLRenderer implements GLSurfaceView.Renderer {

    private Sphere sphere;
    private float[] projectionMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] modelMatrix = new float[16];

    private float angleX = 0f;  // X轴旋转角度
    private float angleY = 0f;  // Y轴旋转角度

    public static int[] textureHandle = new int[1];
    public static Bitmap bitmap;
    private static ImageGLRenderer m;

    public static ImageGLRenderer getInstance() {
        if (m == null) {
            m = new ImageGLRenderer();
        }
        return m;
    }

    public ImageGLRenderer() {
        Matrix.setIdentityM(modelMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // 设置清除颜色
        GLES20.glEnable(GLES20.GL_DEPTH_TEST); // 启用深度测试
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT); // 清除缓冲

        // 初始化球体网格和纹理
        sphere = new Sphere(); // 设置球体的经纬度细分数

        // 设置投影矩阵和视图矩阵
        Matrix.setLookAtM(viewMatrix, 0, 0, -3, 0, 0, 0, 0, 0, 0, -1);
        Matrix.frustumM(projectionMatrix, 0, -0.3f, 0.3f, -0.6f, 0.6f, 3, 7);

    }

    int p = 0;

    @Override
    public void onDrawFrame(GL10 gl) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT); // 清除缓冲

        // 旋转球体（根据角度变化）
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, angleX, 1f, 0f, 0f);  // 绕X轴旋转
        Matrix.rotateM(modelMatrix, 0, angleY, 0f, 1f, 0f);  // 绕Y轴旋转

        //从队列中获取图片
        bitmap = (Bitmap) BitmapQueue.getInstance().dequeue();

        //初始化 textureHandle
        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            //加载 图片
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        // 绘制球体
        sphere.bindTexture(textureHandle[0]);
        sphere.draw(viewMatrix, projectionMatrix, modelMatrix);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0); // 解绑纹理
        GLES20.glDeleteTextures(1, textureHandle, 0);// 清除纹理 关键一步 可以不死机

    }

    // 每帧更新角度来实现动画
    public void updateRotation(float deltaX, float deltaY) {
        angleX += deltaY * 0.3f;  // 根据触摸移动改变X轴角度
        angleY += deltaX * 0.3f;  // 根据触摸移动改变Y轴角度

        // 防止角度超过 360 度
        if (angleX > 360) angleX -= 360;
        if (angleY > 360) angleY -= 360;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

}
