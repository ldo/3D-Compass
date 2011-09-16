package nz.gen.geek_central.Compass3D;
/*
    Display a 3D compass arrow using OpenGL, composited on a live camera view.

    Copyright 2011 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

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
import javax.microedition.khronos.opengles.GL11;
import java.nio.ByteBuffer;

public class Main extends android.app.Activity
  {
    android.widget.TextView MessageView;
    android.view.SurfaceView Graphical;
    android.hardware.SensorManager SensorMan;
    android.hardware.Sensor Compass = null;
    CommonListener Listen;
    android.hardware.Camera TheCamera;
    final long[] FrameTimes = new long[25];
    int NextFrameTime = 0;

    void StopCompass()
      {
        if (Compass != null && Listen != null)
          {
            SensorMan.unregisterListener(Listen, Compass);
          } /*if*/
      } /*StopCompass*/

    void StartCompass()
      {
        if (Compass != null && Listen != null)
          {
            SensorMan.registerListener
              (
                Listen,
                Compass,
                android.hardware.SensorManager.SENSOR_DELAY_UI
              );
          } /*if*/
      } /*StartCompass*/

    void StopCamera()
      {
        if (TheCamera != null)
          {
            if (Listen != null)
              {
                Listen.StopCamera();
              } /*if*/
            TheCamera.release();
            TheCamera = null;
          } /*if*/
      } /*StopCamera*/

    void StartCamera()
      {
        TheCamera = android.hardware.Camera.open();
        if (TheCamera != null)
          {
            if (Listen != null)
              {
                Listen.StartCamera();
              } /*if*/
          }
        else
          {
            android.widget.Toast.makeText
              (
                /*context =*/ this,
                /*text =*/ "Failed to open camera",
                /*duration =*/ android.widget.Toast.LENGTH_LONG
              ).show();
          } /*if*/
      } /*StartCamera*/

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
        private final Compass Needle;
        private android.graphics.Point PreviewSize;
        private int[] ImageBuf;

        public CommonListener()
          {
            Display = EGLUseful.NewDisplay();
            Needle = new Compass();
          } /*CommonListener*/

        public void Release()
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
          } /*Release*/

        public void StartCamera()
          {
            if (TheCamera != null && ImageBuf != null)
              {
                TheCamera.setPreviewCallback(this);
                TheCamera.startPreview();
              } /*if*/
          } /*StartCamera*/

        public void StopCamera()
          {
            if (TheCamera != null)
              {
                TheCamera.stopPreview();
                TheCamera.setPreviewCallback(null);
              } /*if*/
          } /*StopCamera*/

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
                    final android.graphics.Bitmap ImageBits = android.graphics.Bitmap.createBitmap
                      (
                        /*colors =*/ ImageBuf,
                        /*width =*/ PreviewSize.x,
                        /*height =*/ PreviewSize.y,
                        /*config =*/ android.graphics.Bitmap.Config.ARGB_8888
                      );
                    final android.graphics.Matrix Rotate = new android.graphics.Matrix();
                  /* fixme: how do I figure out right rotation angle for different
                    situations at API level 7? */
                    Rotate.postRotate(90, PreviewSize.x / 2.0f, PreviewSize.y / 2.0f);
                    Display.drawBitmap(ImageBits, Rotate, null);
                  } /*if*/
                if (GLContext != null)
                  {
                    GLContext.SetCurrent();
                    final GL11 gl = GLContext.GetGL();
                    Needle.Draw(gl, Azi, Elev, Roll);
                    gl.glFinish();
                    gl.glReadPixels
                      (
                        /*x =*/ 0,
                        /*y =*/ 0,
                        /*width =*/ GLBits.getWidth(),
                        /*height =*/ GLBits.getHeight(),
                        /*format =*/ GL11.GL_RGBA,
                        /*type =*/ GL11.GL_UNSIGNED_BYTE,
                        /*pixels =*/ GLPixels
                      );
                    GLContext.ClearCurrent();
                    GLBits.copyPixelsFromBuffer(GLPixels);
                    final android.graphics.Matrix FlipY = new android.graphics.Matrix();
                    FlipY.preScale
                      (
                        1, -1,
                        0, GLBits.getHeight() / 2.0f
                      );
                      /* Y-axis goes up for OpenGL, down for 2D Canvas */
                    Display.drawBitmap(GLBits, FlipY, null);
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
            MessageView.setText(MessageBuf.toString());
            Azi = Event.values[0];
            Elev = Event.values[1];
            Roll = Event.values[2];
            Draw();
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
            System.err.printf
              (
                "Got preview frame length %d fps %.2f\n",
                Data.length,
                    FrameTimes.length * 1000f
                /
                    (
                        FrameTimes[(NextFrameTime + FrameTimes.length - 1) % FrameTimes.length]
                    -
                        FrameTimes[NextFrameTime]
                    )
              );
            CameraUseful.DecodeNV21(PreviewSize.x, PreviewSize.y, Data, 255, ImageBuf);
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
            StopCamera();
            Release();
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
            Needle.Setup(GLContext.GetGL(), Width, Height);
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
            if (TheCamera != null)
              {
                System.err.printf
                  (
                    "My activity orientation is %d\n",
                    Main.this.getWindowManager().getDefaultDisplay().getOrientation()
                  );
                PreviewSize = CameraUseful.GetLargestPreviewSizeAtMost(TheCamera, Width, Height);
                System.err.printf("Setting preview size to %d*%d (at most %d*%d)\n", PreviewSize.x, PreviewSize.y, Width, Height);
                final android.hardware.Camera.Parameters Parms = TheCamera.getParameters();
                Parms.setPreviewSize(PreviewSize.x, PreviewSize.y);
                TheCamera.setParameters(Parms);
                ImageBuf = new int[PreviewSize.x * PreviewSize.y];
                StartCamera();
              }
            else
              {
                ImageBuf = null;
              } /*if*/
          } /*surfaceChanged*/

        public void surfaceCreated
          (
            android.view.SurfaceHolder TheHolder
          )
          {
          /* do everything in surfaceChanged */
          } /*surfaceCreated*/

        public void surfaceDestroyed
          (
            android.view.SurfaceHolder TheHolder
          )
          {
            StopCamera();
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
        MessageView = (android.widget.TextView)findViewById(R.id.message);
        Graphical = (android.view.SurfaceView)findViewById(R.id.display);
        SensorMan = (android.hardware.SensorManager)getSystemService(SENSOR_SERVICE);
        Compass = SensorMan.getDefaultSensor(android.hardware.Sensor.TYPE_ORIENTATION);
        if (Compass == null)
          {
            MessageView.setText("No compass hardware present");
          } /*if*/
        Listen = new CommonListener();
        Graphical.getHolder().addCallback(Listen);
      } /*onCreate*/

    @Override
    public void onPause()
      {
        super.onPause();
        StopCompass();
        StopCamera();
        if (Listen != null)
          {
            Listen.Release();
          } /*if*/
      } /*onPause*/

    @Override
    public void onResume()
      {
        super.onResume();
        StartCamera();
        StartCompass();
      } /*onResume*/

  } /*Main*/
