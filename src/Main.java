package nz.gen.geek_central.Compass3D;
/*
    Display a 3D compass arrow using OpenGL, composited on a live camera view.

    Copyright 2011, 2012 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

    Licensed under the Apache License, Version 2.0 (the "License"); you may not
    use this file except in compliance with the License. You may obtain a copy of
    the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
    License for the specific language governing permissions and limitations under
    the License.
*/

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLDisplay;
import android.opengl.GLES11;
import android.graphics.Matrix;
import java.nio.ByteBuffer;

public class Main extends android.app.Activity
  {
    android.widget.TextView Message1View, Message2View;
    android.view.SurfaceView Graphical;
    android.hardware.SensorManager SensorMan;
    android.hardware.Sensor CompassSensor = null;
    CommonListener Listen;
    int TheCameraID;
    final long[] FrameTimes = new long[25];
    boolean Active = false, SurfaceExists = false;
    int NextFrameTime = 0;

    private class CommonListener
        implements
            android.hardware.SensorEventListener,
            android.hardware.Camera.PreviewCallback,
            android.view.SurfaceHolder.Callback
      {
        private final EGLDisplay Display;
        private EGLUseful.SurfaceContext GLContext;
        ByteBuffer GLPixels;
        android.graphics.Bitmap GLBits;
        private android.hardware.Camera TheCamera;
        private CameraSetupExtra TheCameraExtra = null;
        private final Compass Needle;
        private android.graphics.Point PreviewSize, RotatedPreviewSize;
        private int[] ImageBuf;
        private int Rotation;
        private Matrix ImageTransform;

        public CommonListener()
          {
            Display = EGLUseful.NewDisplay();
            Needle = new Compass();
          } /*CommonListener*/

        public void Start()
          {
            AllocateGL();
            Rotation = (5 - Main.this.getWindowManager().getDefaultDisplay().getOrientation()) % 4;
            ImageTransform = new Matrix();
            ImageTransform.preScale
              (
                1, -1,
                0, GLBits.getHeight() / 2.0f
              );
              /* Y-axis goes up for OpenGL, down for 2D Canvas */
            ImageTransform.postRotate
              (
                (Rotation - 1) * 90.0f,
                GLBits.getWidth() / 2.0f,
                GLBits.getHeight() / 2.0f
              );
            StartCompass();
            StartCamera();
          } /*Start*/

        public void Stop()
          {
            StopCamera();
            StopCompass();
            ReleaseGL();
          } /*Stop*/

        public void Finish()
          {
            Stop();
            EGLUseful.EGL.eglTerminate(Display);
          } /*Finish*/

        private void StartCompass()
          {
            if (CompassSensor != null)
              {
                SensorMan.registerListener
                  (
                    this,
                    CompassSensor,
                    android.hardware.SensorManager.SENSOR_DELAY_UI
                  );
              } /*if*/
          } /*StartCompass*/

        private void StopCompass()
          {
            if (CompassSensor != null)
              {
                SensorMan.unregisterListener(this, CompassSensor);
              } /*if*/
          } /*StopCompass*/

        private class CameraSetupExtra
          /* does extra setup specific to Android 3.0 and later. Instantiation
            will fail with NoClassDefFoundError on earlier versions. */
          {
            private final android.graphics.SurfaceTexture DummyTexture;
            private final android.hardware.Camera TheCamera;

            public CameraSetupExtra
              (
                android.hardware.Camera TheCamera
              )
              {
                this.TheCamera = TheCamera;
                int[] ID = new int[1];
                android.opengl.GLES11.glGenTextures(1, ID, 0);
                DummyTexture = new android.graphics.SurfaceTexture(ID[0]);
                TheCamera.setPreviewTexture(DummyTexture);
              } /*CameraSetupExtra*/

            public void Release()
              {
                TheCamera.setPreviewTexture(null);
              /* DummyTexture.release(); */ /* API 14 or later only */
              } /*Release*/

          } /*CameraSetupExtra*/

        private void DumpList
          (
            String Description,
            java.util.List<?> TheList,
            java.io.PrintStream Out
          )
          {
            Out.print(Description);
            if (TheList != null)
              {
                Out.print(" ");
                boolean first = true;
                for (Object val : TheList)
                  {
                    if (!first)
                      {
                        Out.print(", ");
                      } /*if*/
                    Out.print(val);
                    first = false;
                  } /*for*/
              }
            else
              {
                Out.print("(none)");
              } /*if*/
            Out.println();
          } /*DumpList*/

        private void StartCamera()
          {
            TheCamera = CameraUseful.OpenCamera(TheCameraID);
            if (TheCamera != null)
              {
                System.err.printf
                  (
                    "My activity orientation is %d\n",
                    Main.this.getWindowManager().getDefaultDisplay().getOrientation()
                  ); /* debug */
                TheCamera.setDisplayOrientation(CameraUseful.RightOrientation(Main.this, TheCameraID));
                PreviewSize = CameraUseful.GetLargestPreviewSizeAtMost(TheCamera, Graphical.getWidth(), Graphical.getHeight());
                System.err.printf("Setting preview size to %d*%d (at most %d*%d)\n", PreviewSize.x, PreviewSize.y, Graphical.getWidth(), Graphical.getHeight()); /* debug */
                final android.hardware.Camera.Parameters Parms = TheCamera.getParameters();
                  { /* debug */
                    System.err.println("Main.StartCamera initial params:");
                    System.err.printf
                      (
                        " Scene mode: %s, preview frame rate %d\n",
                        Parms.getSceneMode(),
                        Parms.getPreviewFrameRate()
                      );
                    System.err.printf(" White balance: %s\n", Parms.getWhiteBalance());
                    DumpList
                      (
                        " Supported anti-banding:",
                        Parms.getSupportedAntibanding(),
                        System.err
                      );
                    DumpList
                      (
                        " Supported colour effects:",
                        Parms.getSupportedColorEffects(),
                        System.err
                      );
                    DumpList
                      (
                        " Supported flash modes:",
                        Parms.getSupportedFlashModes(),
                        System.err
                      );
                    DumpList
                      (
                        " Supported picture formats:",
                        Parms.getSupportedPictureFormats(),
                        System.err
                      );
                    DumpList
                      (
                        " Supported preview formats:",
                        Parms.getSupportedPreviewFormats(),
                        System.err
                      );
                    DumpList
                      (
                        " Supported preview frame rates:",
                        Parms.getSupportedPreviewFrameRates(),
                        System.err
                      );
                    DumpList
                      (
                        " Supported scene modes:",
                        Parms.getSupportedSceneModes(),
                        System.err
                      );
                    DumpList
                      (
                        " Supported white balances:",
                        Parms.getSupportedWhiteBalance(),
                        System.err
                      );
                  }
                Parms.setPreviewSize(PreviewSize.x, PreviewSize.y);
                TheCamera.setParameters(Parms);
                RotatedPreviewSize = new android.graphics.Point
                  (
                    (Rotation & 1) != 0 ? PreviewSize.y : PreviewSize.x,
                    (Rotation & 1) != 0 ? PreviewSize.x : PreviewSize.y
                  );
                ImageBuf = new int[PreviewSize.x * PreviewSize.y];
                TheCamera.setPreviewCallback(this);
              /* Note I don't call TheCamera.setPreviewDisplay, even though the docs
                say this is necessary. I don't want to do that, because I don't want
                any preview to appear on-screen. I got away with that on an HTC Desire
                (Android 2.2), but it apears the Samsung Galaxy Nexus (Android 4.0) is
                not so forgiving. Luckily Honeycomb and later offer setPreviewTexture
                as an alternative. So I set a dummy one of these. However, this seems
                to make for a horrible frame rate. I'll have to see how to remedy
                that later (fixme!). */
                try
                  {
                    TheCameraExtra = new CameraSetupExtra(TheCamera);
                  }
                catch (NoClassDefFoundError TooOld)
                  {
                  } /*try*/
                TheCamera.startPreview();
              }
            else
              {
                Message2View.setText("Failed to open camera");
              } /*if*/
          } /*StartCamera*/

        private void StopCamera()
          {
            if (TheCamera != null)
              {
                TheCamera.stopPreview();
                if (TheCameraExtra != null)
                  {
                    TheCameraExtra.Release();
                    TheCameraExtra = null;
                  } /*if*/
                TheCamera.setPreviewCallback(null);
                TheCamera.release();
                TheCamera = null;
                ImageBuf = null;
              } /*if*/
          } /*StopCamera*/

        private void AllocateGL()
          {
            final int Width = Graphical.getWidth();
            final int Height = Graphical.getHeight();
            GLContext = EGLUseful.SurfaceContext.CreatePbuffer
              (
                /*ForDisplay =*/ Display,
                /*TryConfigs =*/
                    EGLUseful.GetCompatConfigs
                      (
                        /*ForDisplay =*/ Display,
                        /*MatchingAttribs =*/
                            new int[]
                                {
                                    EGL10.EGL_RED_SIZE, 8,
                                    EGL10.EGL_GREEN_SIZE, 8,
                                    EGL10.EGL_BLUE_SIZE, 8,
                                    EGL10.EGL_ALPHA_SIZE, 8,
                                    EGL10.EGL_DEPTH_SIZE, 16,
                                    EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                                    EGL10.EGL_CONFIG_CAVEAT, EGL10.EGL_NONE,
                                    EGL10.EGL_NONE /* marks end of list */
                                }
                      ),
                /*Width =*/ Width,
                /*Height =*/ Height,
                /*ExactSize =*/ true,
                /*ShareContext =*/ null
              );
            GLContext.SetCurrent();
            Needle.Setup(Width, Height);
            GLContext.ClearCurrent();
            GLPixels = ByteBuffer.allocateDirect
              (
                Width * Height * 4
              ).order(java.nio.ByteOrder.nativeOrder());
            GLBits = android.graphics.Bitmap.createBitmap
              (
                /*width =*/ Width,
                /*height =*/ Height,
                /*config =*/ android.graphics.Bitmap.Config.ARGB_8888
              );
          } /*AllocateGL*/

        private void ReleaseGL()
          {
            if (GLContext != null)
              {
                GLContext.Release();
                GLContext = null;
              } /*if*/
            if (GLBits != null)
              {
                GLBits.recycle();
                GLBits = null;
              } /*if*/
          } /*ReleaseGL*/

        private float Azi, Elev, Roll;

        private void Draw()
          /* (re)draws the complete composited display. */
          {
            final android.graphics.Canvas Display = Graphical.getHolder().lockCanvas();
            if (Display != null)
              {
                Display.drawColor(0, android.graphics.PorterDuff.Mode.SRC);
                  /* initialize all pixels to fully transparent */
                if (ImageBuf != null)
                  {
                    Display.drawBitmap
                      (
                        /*colors =*/ ImageBuf,
                        /*offset =*/ 0,
                        /*stride =*/ RotatedPreviewSize.x,
                        /*x =*/ (Graphical.getWidth() - RotatedPreviewSize.x) / 2,
                        /*y =*/ (Graphical.getHeight() - RotatedPreviewSize.y) / 2,
                        /*width =*/ RotatedPreviewSize.x,
                        /*height =*/ RotatedPreviewSize.y,
                        /*hasAlpha =*/ true,
                        /*paint =*/ null
                      );
                  } /*if*/
                if (GLContext != null)
                  {
                    GLContext.SetCurrent();
                    Needle.Draw(Azi, Elev, Roll);
                      { /* debug */
                        final int EGLError = EGLUseful.EGL.eglGetError();
                        if (EGLError != EGL10.EGL_SUCCESS)
                          {
                            System.err.printf
                              (
                                "Compass3D.Main EGL error 0x%04x\n", EGLError
                              );
                          } /*if*/
                      }
                    GLES11.glFinish();
                    GLES11.glReadPixels
                      (
                        /*x =*/ 0,
                        /*y =*/ 0,
                        /*width =*/ GLBits.getWidth(),
                        /*height =*/ GLBits.getHeight(),
                        /*format =*/ GLES11.GL_RGBA,
                        /*type =*/ GLES11.GL_UNSIGNED_BYTE,
                        /*pixels =*/ GLPixels
                      );
                    GLContext.ClearCurrent();
                    GLBits.copyPixelsFromBuffer(GLPixels);
                    Display.drawBitmap(GLBits, ImageTransform, null);
                  } /*if*/
                Graphical.getHolder().unlockCanvasAndPost(Display);
              }
            else
              {
                System.err.println("Graphical surface not ready");
              } /*if*/
          } /*Draw*/

      /* SensorEventListener methods: */

        public void onAccuracyChanged
          (
            android.hardware.Sensor TheSensor,
            int NewAccuracy
          )
          {
          /* ignore for now */
          } /*onAccuracyChanged*/

        private long LastSensorUpdate = 0;

        public void onSensorChanged
          (
            android.hardware.SensorEvent Event
          )
          {
            final java.io.ByteArrayOutputStream MessageBuf = new java.io.ByteArrayOutputStream();
            final java.io.PrintStream Msg = new java.io.PrintStream(MessageBuf);
            Msg.printf
              (
                "Sensor event at %.6f accuracy %d\nValues(%d): (",
                Event.timestamp / Math.pow(10.0d, 9),
                Event.accuracy,
                Event.values.length
              );
            for (int i = 0; i < Event.values.length; ++i)
              {
                if (i != 0)
                  {
                    Msg.print(", ");
                  } /*if*/
                Msg.printf("%.0f°", Event.values[i]);
              } /*for*/
            Msg.print(")\n");
            Msg.flush();
            Message1View.setText(MessageBuf.toString());
            Azi = Event.values[0];
            Elev = Event.values[1];
            Roll = Event.values[2];
            final long Now = System.currentTimeMillis();
            if (Now - LastSensorUpdate >= 250)
              /* throttle sensor updates because they seem to cause contention
                with camera preview updates */
              {
                LastSensorUpdate = Now;
                Draw();
              } /*if*/
          } /*onSensorChanged*/

      /* Camera.PreviewCallback methods: */

        public void onPreviewFrame
          (
            byte[] Data,
            android.hardware.Camera TheCamera
          )
          {
            FrameTimes[NextFrameTime] = System.currentTimeMillis();
            NextFrameTime = (NextFrameTime + 1) % FrameTimes.length;
            final float FrameRate =
                    FrameTimes.length * 1000f
                /
                    (
                        FrameTimes[(NextFrameTime + FrameTimes.length - 1) % FrameTimes.length]
                    -
                        FrameTimes[NextFrameTime]
                    );
            if (NextFrameTime == 0)
              {
                Message2View.setText(String.format("Camera fps %.2f", FrameRate));
              } /*if*/
            CameraUseful.DecodeNV21
              (
                /*SrcWidth =*/ PreviewSize.x,
                /*SrcHeight =*/ PreviewSize.y,
                /*Data =*/ Data,
                /*Rotate =*/ Rotation,
                /*Alpha =*/ 255,
                /*Pixels =*/ ImageBuf
              );
            Draw();
          } /*onPreviewFrame*/

      /* SurfaceHolder.Callback methods: */

        public void surfaceChanged
          (
            android.view.SurfaceHolder TheHolder,
            int Format,
            int Width,
            int Height
          )
          {
            System.err.println("Compass3D.Main surfaceChanged"); /* debug */
            Stop();
            SurfaceExists = true;
            if (Active)
              {
                Start();
              } /*if*/
          } /*surfaceChanged*/

        public void surfaceCreated
          (
            android.view.SurfaceHolder TheHolder
          )
          {
          /* do everything in surfaceChanged */
            System.err.println("Compass3D.Main surfaceCreated"); /* debug */
          } /*surfaceCreated*/

        public void surfaceDestroyed
          (
            android.view.SurfaceHolder TheHolder
          )
          {
            SurfaceExists = false;
            Stop();
            System.err.println("Compass3D.Main surfaceDestroyed"); /* debug */
          } /*surfaceDestroyed*/

      } /*CommonListener*/

    @Override
    public void onCreate
      (
        android.os.Bundle savedInstanceState
      )
      {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Message1View = (android.widget.TextView)findViewById(R.id.message1);
        Message2View = (android.widget.TextView)findViewById(R.id.message2);
        Graphical = (android.view.SurfaceView)findViewById(R.id.display);
        SensorMan = (android.hardware.SensorManager)getSystemService(SENSOR_SERVICE);
        CompassSensor = SensorMan.getDefaultSensor(android.hardware.Sensor.TYPE_ORIENTATION);
        if (CompassSensor == null)
          {
            Message1View.setText("No compass hardware present");
          } /*if*/
        if (CameraUseful.CanTellCameraPresent()) /* debug */
          {
            System.err.println("3DCompass.Main: rear camera present? " + (CameraUseful.FirstCamera(false) >= 0 ? "YES" : "NO"));
            System.err.println("3DCompass.Main: front camera present? " + (CameraUseful.FirstCamera(true) >= 0 ? "YES" : "NO"));
          }
        else
          {
            System.err.println("3DCompass.Main: cannot tell what cameras are present");
          } /*if*/ /* end debug */
        Listen = new CommonListener();
        Graphical.getHolder().addCallback(Listen);
      } /*onCreate*/

    @Override
    public void onDestroy()
      {
        Listen.Finish();
        super.onDestroy();
      } /*onDestroy*/

    @Override
    public void onPause()
      {
        Listen.Stop();
        Active = false;
        super.onPause();
      } /*onPause*/

    @Override
    public void onResume()
      {
        super.onResume();
        Active = true;
        if (SurfaceExists)
          {
            Listen.Start();
          } /*if*/
      } /*onResume*/

  } /*Main*/
