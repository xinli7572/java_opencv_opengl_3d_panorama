package com.example.m_opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class ImageGLSurfaceView extends GLSurfaceView {

    private ImageGLRenderer renderer;

    public ImageGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public ImageGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {

        // 设置 OpenGL ES 版本
        setEGLContextClientVersion(2);
        // 创建并设置渲染器
        renderer =  ImageGLRenderer.getInstance();
        setRenderer(renderer);
        // 设置渲染模式为 RENDERMODE_WHEN_DIRTY RENDERMODE_CONTINUOUSLY
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public void triggerRender() {
        setRenderMode(RENDERMODE_CONTINUOUSLY);
        requestRender();  // 请求重新渲染
    }

    public void stop() {
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }
}