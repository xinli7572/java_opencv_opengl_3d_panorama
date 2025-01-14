package com.example.videolibrary;

import android.graphics.Bitmap;

public class VideoPresenter {

    final static String TAG = "video_play";

    private static VideoPresenter mInstance;


    static {
        System.loadLibrary("video_play");
    }


    private VideoPresenter() {

    }

    public static VideoPresenter getInstance() {
        if (mInstance == null) {
            synchronized (VideoPresenter.class) {
                if (mInstance == null) {
                    mInstance = new VideoPresenter();
                }
            }
        }
        return mInstance;
    }


    public void startReadVideo(final String videoPath, final int showFps, final int lostFps, final int width, final int height) {
        nativeStartReadVideo(videoPath, showFps, lostFps, width, height);
    }


    public void stopReadVideo() {
        nativeStopReadVideo();
    }


    public void regVideoCallback(IVideoCallback videoCallback, Bitmap bitmap) {
        nativeRegVideoCallback(videoCallback, bitmap);
    }


    public interface IVideoCallback {
        void onImageShow();
    }

    private static final native void nativeRegVideoCallback(IVideoCallback videoCallback, Bitmap bitmap);

    private static final native void nativeStartReadVideo(String videoPath, int showFps, int lostFps, int width, int height);

    private static final native void nativeStopReadVideo();


}