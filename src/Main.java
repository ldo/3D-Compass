package nz.gen.geek_central.Compass3D;
/*
    Display a 3D compass arrow using OpenGL, composited on a live camera view.

    Copyright 2011-2013 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

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

import android.graphics.Matrix;
import android.widget.Toast;
import android.view.View;
import nz.gen.geek_central.GLUseful.Mat4f;
import nz.gen.geek_central.GLUseful.Vec3f;
import nz.gen.geek_central.GLUseful.GLUseful;
import static nz.gen.geek_central.GLUseful.GLUseful.gl;
import nz.gen.geek_central.GLUseful.GLView;

public class Main extends android.app.Activity
  {

    private java.util.Map<android.view.MenuItem, Runnable> OptionsMenu;
    private java.util.Map<android.view.MenuItem, Runnable> ContextMenu;

    interface RequestResponseAction /* response to an activity result */
      {
        public void Run
          (
            int ResultCode,
            android.content.Intent Data
          );
      } /*RequestResponseAction*/

    private java.util.Map<Integer, RequestResponseAction> ActivityResultActions;
  /* request codes */
    private static final int ChooseCameraRequest = 1;

    android.widget.TextView Message1View, Message2View;
    MainView Graphical;
    android.hardware.SensorManager SensorMan;
    android.hardware.Sensor CompassSensor = null;
    CommonListener Listen;
    int TheCameraID;
    final long[] FrameTimes = new long[25];
    boolean Active = false, MainViewActive = false;
    int NextFrameTime = 0;

    private class CommonListener
        implements
            android.hardware.SensorEventListener,
            android.hardware.Camera.PreviewCallback,
            MainView.Handler
      {
        private android.hardware.Camera TheCamera;
        private CameraSetupExtra TheCameraExtra = null;
        private android.graphics.Point PreviewSize, RotatedPreviewSize;
        private float ViewRadius;
        private Compass Needle;
        private GLView Background;
        private int[] ImageBuf;
        private int Rotation;
        private Mat4f ProjectionMatrix;

        public CommonListener()
          {
          /* nothing to do for now */
          } /*CommonListener*/

        public void Start()
          {
            Rotation = (5 - Main.this.getWindowManager().getDefaultDisplay().getOrientation()) % 4;
            StartCompass();
            if (MainViewActive)
              {
                StartCamera();
              } /*if*/
          } /*Start*/

        public void Stop()
          {
            StopCamera();
            StopCompass();
          } /*Stop*/

        public void Finish()
          {
            Stop();
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
                gl.glGenTextures(1, ID, 0);
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
                    "My activity orientation is %d, using camera %d\n",
                    Main.this.getWindowManager().getDefaultDisplay().getOrientation(),
                    TheCameraID
                  ); /* debug */
                try
                  {
                    final int RightOrientation = CameraUseful.RightOrientation(Main.this, TheCameraID);
                    TheCamera.setDisplayOrientation(RightOrientation);
                    Toast.makeText /* debug */
                      (
                        /*context =*/ Main.this,
                        /*text =*/
                            String.format
                              (
                                "Set camera orientation to %d°",
                                RightOrientation
                              ),
                        /*duration =*/ Toast.LENGTH_SHORT
                      ).show();
                  }
                catch (RuntimeException Failed)
                  {
                    Toast.makeText
                      (
                        /*context =*/ Main.this,
                        /*text =*/
                            String.format
                              (
                                getString(R.string.set_orientation_failed),
                                CameraUseful.RightOrientation(Main.this, TheCameraID)
                              ),
                        /*duration =*/ Toast.LENGTH_SHORT
                      ).show();
                  } /*try*/
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

        private float
            Azi,
              /* always Earth-horizontal, regardless of orientation of phone */
            Elev,
              /* always around X-axis of phone, +ve is top-down, -ve is top-up */
            Roll;
              /* always around Y-axis of phone, +ve is anticlockwise
                viewed from bottom, -ve is clockwise, until it reaches
                ±90° when it its starts decreasing in magnitude again, so
                0° is when phone is horizontal either face-up or face-down */

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
            Azi = (float)Math.toRadians(Event.values[0]);
            Elev = (float)Math.toRadians(Event.values[1]);
            Roll = (float)Math.toRadians(Event.values[2]);
            final long Now = System.currentTimeMillis();
            if (Now - LastSensorUpdate >= 250)
              /* throttle sensor updates because they seem to cause contention
                with camera preview updates */
              {
                LastSensorUpdate = Now;
                Graphical.requestRender();
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
                /*Rotate =*/ CameraUseful.CanTellCameraPresent() ? 0 : Rotation,
                /*Alpha =*/ 255,
                /*Pixels =*/ ImageBuf
              );
            Graphical.requestRender();
          } /*onPreviewFrame*/

      /* MainView.Handler methods: */

        public void Setup
          (
            int ViewWidth,
            int ViewHeight
          )
          /* initial setup for drawing that doesn't need to be done for every frame. */
          {
            ViewRadius = Math.min(ViewWidth, ViewHeight) / 2.0f;
            if (Needle != null)
              {
                Needle.Unbind(true);
                Needle = null;
              } /*if*/
            if (Background != null) /* force re-creation to match view dimensions */
              {
                Background.Unbind(true);
                Background = null;
              } /*if*/
            Needle = new Compass(true);
            Background = new GLView(ViewWidth, ViewHeight, true);
            gl.glEnable(gl.GL_CULL_FACE);
            gl.glViewport
              (
                Math.round(ViewWidth / 2.0f - ViewRadius),
                Math.round(ViewHeight / 2.0f - ViewRadius),
                Math.round(2.0f * ViewRadius),
                Math.round(2.0f * ViewRadius)
              );
            ProjectionMatrix =
                    Mat4f.frustum
                      (
                        /*L =*/ -0.1f,
                        /*R =*/ 0.1f,
                        /*B =*/ -0.1f,
                        /*T =*/ 0.1f,
                        /*N =*/ 0.1f,
                        /*F =*/ 3.1f
                      )
                .mul(
                    Mat4f.translation(new Vec3f(0.0f, 0.0f, -1.6f))
                );
            MainViewActive = true;
            if (Active)
              {
                runOnUiThread
                  (
                    new Runnable()
                      {
                        public void run()
                          {
                            StopCamera();
                            StartCamera();
                          } /*run*/
                      } /*Runnable*/
                  );
              } /*if*/
          } /*Setup*/

        public void Draw()
          /* generates the complete composite display. */
          {
              {
                final android.graphics.Canvas g = Background.Draw;
                g.drawColor(0, android.graphics.PorterDuff.Mode.SRC);
                  /* initialize all pixels to fully transparent */
                if (ImageBuf != null)
                  {
                    g.drawBitmap
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
                Background.DrawChanged();
              }
            GLUseful.ClearColor(new GLUseful.Color(0)); /* all pixels initially transparent */
            gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
            gl.glDisable(gl.GL_DEPTH_TEST);
            Background.Draw
              (
                /*Projection =*/ Mat4f.identity(),
                /*Left =*/ -1.0f,
                /*Bottom =*/ -1.0f,
                /*Right =*/ 1.0f,
                /*Top =*/ 1.0f,
                /*Depth =*/ 0.99f
              );
            final Mat4f Orientation =
                (
                    Mat4f.rotation(Mat4f.AXIS_Z, (1 - Rotation) * (float)Math.PI / 2.0f)
                ).mul(
                    Mat4f.rotation(Mat4f.AXIS_Y, Roll)
                ).mul(
                    Mat4f.rotation(Mat4f.AXIS_X, Elev)
                ).mul(
                    Mat4f.rotation(Mat4f.AXIS_Z, Azi)
                );
            gl.glEnable(gl.GL_DEPTH_TEST);
            Needle.Draw
              (
                /*ProjectionMatrix =*/ ProjectionMatrix,
                /*ModelViewMatrix =*/ Orientation
              );
          } /*Draw*/

        public void Release()
          {
          /* losing the GL context anyway, so don't bother releasing anything: */
            Needle = null;
            Background = null;
            MainViewActive = false;
          } /*Release*/

      } /*CommonListener*/;

    void BuildActivityResultActions()
      {
        ActivityResultActions = new java.util.HashMap<Integer, RequestResponseAction>();
        ActivityResultActions.put
          (
            ChooseCameraRequest,
            new RequestResponseAction()
              {
                public void Run
                  (
                    int ResultCode,
                    android.content.Intent Data
                  )
                  {
                    Listen.StopCamera();
                    TheCameraID = Data.getIntExtra(CameraList.CameraIDID, TheCameraID);
                    if (Active && MainViewActive)
                      {
                        Listen.StartCamera();
                      } /*if*/
                  } /*Run*/
              } /*RequestResponseAction*/
          );
      } /*BuildActivityResultActions*/

    @Override
    public void onCreate
      (
        android.os.Bundle SavedInstanceState
      )
      {
        super.onCreate(SavedInstanceState);
        BuildActivityResultActions();
        if (SavedInstanceState != null)
          {
            TheCameraID = SavedInstanceState.getInt(CameraList.CameraIDID, 0);
          } /*if*/
        setContentView(R.layout.main);
        findViewById(R.id.main_view).setOnClickListener
          (
            new View.OnClickListener()
              {
                public void onClick
                  (
                    View TheView
                  )
                  {
                    if (CameraUseful.CanTellCameraPresent())
                      {
                        CameraList.Launch
                          (
                            /*Caller =*/ Main.this,
                            /*RequestCode =*/ ChooseCameraRequest,
                            /*CurCameraID =*/ TheCameraID
                          );
                      }
                    else
                      {
                        Toast.makeText
                          (
                            /*context =*/ Main.this,
                            /*text =*/ getString(R.string.no_camera_info),
                            /*duration =*/ Toast.LENGTH_SHORT
                          ).show();
                      } /*if*/
                  } /*onClick*/
              } /*View.OnClickListener*/
          );
        Message1View = (android.widget.TextView)findViewById(R.id.message1);
        Message2View = (android.widget.TextView)findViewById(R.id.message2);
        Graphical = (MainView)findViewById(R.id.display);
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
        Graphical.SetHandler(Listen);
      } /*onCreate*/

    @Override
    public void onSaveInstanceState
      (
        android.os.Bundle SavedInstanceState
      )
      {
        SavedInstanceState.putInt(CameraList.CameraIDID, TheCameraID);
      } /*onSaveInstanceState*/

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
        Listen.Start();
      } /*onResume*/

    @Override
    public void onActivityResult
      (
        int RequestCode,
        int ResultCode,
        android.content.Intent Data
      )
      {
        CameraList.Cleanup();
        if (ResultCode != android.app.Activity.RESULT_CANCELED)
          {
            final RequestResponseAction Action = ActivityResultActions.get(RequestCode);
            if (Action != null)
              {
                Action.Run(ResultCode, Data);
              } /*if*/
          } /*if*/
      } /*onActivityResult*/

  } /*Main*/
