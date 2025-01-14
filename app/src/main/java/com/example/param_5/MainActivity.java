package com.example.param_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.m_opengl.BitmapQueue;
import com.example.m_opengl.ImageGLRenderer;
import com.example.m_opengl.ImageGLSurfaceView;
import com.example.videolibrary.VideoPresenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private final static int WIDTH = 1920;
    private final static int HEIGHT = 1080;
    private Bitmap mBitmap = null;

    private ImageView m_ImageView;
    private Button start_video;
    private Button end_video;
    private Button start_video_img;
    private Button end_video_img;

    //==========================================================

    private ImageGLSurfaceView mGLSurfaceView;
    private float lastX, lastY;

    private static Bitmap[] frames_;
    
    //===========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //===========================================================
        mGLSurfaceView = findViewById(R.id.gl_surface_view);

        // 加载动画的帧图片
        frames_ = new Bitmap[]{
                BitmapFactory.decodeResource(this.getResources(), R.drawable.h1),
                BitmapFactory.decodeResource(this.getResources(), R.drawable.h1),
                BitmapFactory.decodeResource(this.getResources(), R.drawable.h1),
                BitmapFactory.decodeResource(this.getResources(), R.drawable.h1),

                // 可以继续添加更多帧
        };
        BitmapQueue.getInstance().enqueue(frames_[0]);
        BitmapQueue.getInstance().enqueue(frames_[1]);
        BitmapQueue.getInstance().enqueue(frames_[2]);
        BitmapQueue.getInstance().enqueue(frames_[3]);

        Uri uri = copyAssetToLocalAndGetUri(this, "e.mp4");
        if (uri != null) {
            System.out.println("uri.toString() = " + uri.toString());
        }


        //===========================================================
        start_video = findViewById(R.id.start_video);
        end_video = findViewById(R.id.end_video);

        start_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regVideoCallback();

                VideoPresenter.getInstance().startReadVideo(
                        uri.toString(),
                        WIDTH, HEIGHT);

                if (!BitmapQueue.getInstance().isEmpty()) {
                    mGLSurfaceView.triggerRender();
                }

            }
        });

        end_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGLSurfaceView.stop();
                VideoPresenter.getInstance().stopReadVideo();
            }
        });
        //===========================================================

    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 根据触摸的移动来更新旋转角度
                float deltaX = (x - lastX) * 0.3f;
                float deltaY = (y - lastY) * 0.3f;
                ImageGLRenderer.getInstance().updateRotation(-deltaX, deltaY);
                break;
        }

        // 记录当前触摸位置
        lastX = x;
        lastY = y;
        return true;
    }

    public void regVideoCallback() {
        mBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_4444);
        VideoPresenter.getInstance().regVideoCallback(new VideoPresenter.IVideoCallback() {
            @Override
            public void onImageShow() {
                showImage();
            }
        }, mBitmap);

    }

    private void showImage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBitmap != null) {
                    BitmapQueue.getInstance().enqueue(mBitmap);
                }
            }
        });
    }


    public Uri copyAssetToLocalAndGetUri(Context context, String assetFileName) {
        try {
            // 从 assets 中读取文件
            InputStream inputStream = context.getAssets().open(assetFileName);
            File outputFile = new File(context.getCacheDir(), assetFileName);
            FileOutputStream outputStream = new FileOutputStream(outputFile);

            // 复制文件
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // 返回文件的 Uri
            return Uri.fromFile(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}