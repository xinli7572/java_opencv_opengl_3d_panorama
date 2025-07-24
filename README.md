
https://github.com/user-attachments/assets/0f7275f0-5b3b-4bf2-b0d7-29cd0b153b75

# 3D Panorama Video Player

This is an Android-based 3D panorama video player that supports playing panoramic videos and allows users to rotate and view them in 3D space. The project uses OpenGL ES 2.0 to render video textures and utilizes JNI to interface with a native C/C++ library for video decoding and playback.

## Setup Instructions

### 1. Set Up Android Environment

Ensure you have the Android development environment set up:

- **Install Android Studio**: Download Android Studio from the official website ([https://developer.android.com/studio](https://developer.android.com/studio)) and follow the installation instructions for your OS.
- **Install Android SDK**: After installing Android Studio, go to `File > Settings > Appearance & Behavior > System Settings > Android SDK` and install the necessary SDK tools. You'll need the following:
    - Android SDK
    - NDK (Native Development Kit)
    - CMake
- **Ensure correct environment variables are set up**: Verify that the `ANDROID_HOME` and other SDK/NDK paths are correctly configured in your environment variables.

### 2. Set Up Native C/C++ Libraries

The project depends on a native library `video_play` for video decoding. You need to add the `video_play` library to your project and ensure that JNI can correctly link to it. The C/C++ code is located in the `src/main/cpp/` folder. Modify the `CMakeLists.txt` file to link the native code properly.

### 3. Import the Project into Android Studio

1. Open **Android Studio**.
2. Choose **Open an existing Android Studio project**.
3. Navigate to the project directory and select it.
4. Click **OK** to import the project.

### 4. Build and Run

1. In Android Studio, select the target device (or emulator) for testing.
2. Click the **Run** button or use the shortcut `Shift + F10` to build and install the app on the selected device.

### 5. Using the App

1. After launching the app, users can click buttons to start playing the video.
2. During playback, users can rotate the 3D panoramic video using touch gestures.
3. Videos can be stopped by clicking the stop button.

## Code Overview

### Main Classes

#### `MainActivity`

`MainActivity` is the entry point of the app, responsible for initializing video playback controls and OpenGL rendering views. It provides the following functionality:

- Loads video files and displays them.
- Controls video playback and stop actions via buttons.
- Supports touch gesture interactions to update the video viewing angle.

#### `ImageGLRenderer`

`ImageGLRenderer` renders the panoramic video using OpenGL ES 2.0. It is responsible for:

- Initializing OpenGL context and rendering the sphere.
- Updating the rotation angles of the sphere based on user touch gestures.
- Processing each video frame from the queue and mapping it as a texture onto the sphere.

#### `Sphere`

The `Sphere` class defines the 3D sphere model, calculating the sphere's vertices and texture coordinates for rendering. The sphere's texture is mapped to the video frames, rendering a panoramic video effect.

#### `VideoPresenter`

`VideoPresenter` handles video playback control, offering multiple methods to start different video playback scenarios. It interacts with the underlying C/C++ video decoding library through JNI.

## Video Playback Flow

1. When the user clicks the play button, the `VideoPresenter` starts reading the video and passes video frames to the OpenGL renderer.
2. The renderer updates the texture mapping to display video frames, allowing users to rotate the sphere and view the video in 3D space.
3. The user can click the stop button to stop video playback.

## Customization and Extension

### Custom Video Source

You can customize the video source path by placing video files in the assets directory or loading online videos via URL.

### Extend Gesture Control

The app currently supports basic rotation control. You can modify the `onTouchEvent` method in `MainActivity` to add other gesture operations, such as zooming or panning.

### Add More Video Scenes

Using the `VideoPresenter` class, you can configure multiple video playback scenarios with different video sources and display areas. Refer to the `startReadVideo_2` and `startReadVideo_3` methods for easily extending multi-screen playback functionality.

## Sample Code

```java
// Start and play a video
Uri uri = Uri.parse("asset:///sample_video.mp4");
VideoPresenter.getInstance().startReadVideo(uri.toString(), 1920, 1080);
```






