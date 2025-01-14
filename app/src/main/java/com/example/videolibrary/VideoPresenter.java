package com.example.videolibrary;


import android.graphics.Bitmap;

public class VideoPresenter {

    final static String TAG = "VideoPresenter";

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


    public void startReadVideo(final String videoPath, final int width, final int height) {
        nativeStartReadVideo(videoPath, width, height);
    }

    public void startReadVideo_2(final String videoPath, final String videoPath_2,
                                 final int x, final int y, final int x2, final int y2,
                                 final int width, final int height,
                                 final int screen_width, final int screen_height
    ) {
        nativeStartReadVideo2(videoPath, videoPath_2, x, y, x2, y2, width, height, screen_width, screen_height);
    }

    public void startReadVideo_3(final String videoPath, final String videoPath_2,
                                 final int left_1, final int top_1, final int right_1, final int bottom_1,
                                 final int left_2, final int right_2,
                                 final int screen_width, final int screen_height
    ) {
        nativeStartReadVideo3(videoPath, videoPath_2, left_1, top_1, right_1, bottom_1, left_2, right_2, screen_width, screen_height);
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

    private static final native void nativeStartReadVideo(String videoPath, int width, int height);

    private static final native void nativeStartReadVideo2(String videoPath, String videoPath_2, int x, int y, int x2, int y2, int width, int height, int screen_width, int screen_height);

    private static final native void nativeStartReadVideo3(String videoPath, String videoPath_2, int left_1, int top_1, int right_1, int bottom_1, int left_2, int right_2, int screen_width, int screen_height);

    private static final native void nativeStopReadVideo();


}
