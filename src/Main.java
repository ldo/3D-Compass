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
import android.graphics.ImageFormat;
import android.widget.Toast;
import android.view.View;
import nz.gen.geek_central.GLUseful.Mat4f;
import nz.gen.geek_central.GLUseful.Vec3f;
import nz.gen.geek_central.GLUseful.GLUseful;
import static nz.gen.geek_central.GLUseful.GLUseful.gl;
import nz.gen.geek_central.GLUseful.GLTextureView;
import nz.gen.geek_central.GLUseful.GLBitmapView;

public class Main extends android.app.Activity
  {

    public static boolean ClassExists
      (
        String ClassName
      )
      /* does the named class exist. */
      {
        boolean Exists;
        try
          {
            Exists =
                    Class.forName(ClassName)
                !=
                    null;
          }
        catch (ClassNotFoundException Nope)
          {
            Exists = false;
          } /*try*/
        return
            Exists;
      } /*ClassExists*/

    public static boolean ClassHasMethod
      (
        String ClassName,
        String MethodName,
        Class<?>... ArgTypes
      )
      /* does the named class have a method with the specified argument types. */
      {
        boolean HasIt;
        try
          {
            HasIt =
                    Class.forName(ClassName)
                        .getDeclaredMethod(MethodName, ArgTypes)
                !=
                    null;
          }
        catch (NoSuchMethodException Nope)
          {
            HasIt = false;
          }
        catch (ClassNotFoundException Huh)
          {
            throw new RuntimeException(Huh.toString());
          } /*try*/
        return
            HasIt;
      } /*ClassHasMethod*/

    public static final boolean HasPreviewTextures =
        ClassHasMethod
          (
            "android.hardware.Camera",
            "setPreviewTexture",
            android.graphics.SurfaceTexture.class
          );
    public static final boolean HasSurfaceTextureRelease =
            ClassExists("android.graphics.SurfaceTexture")
        &&
            ClassHasMethod("android.graphics.SurfaceTexture", "release");

    private static void SetCameraPreviewTexture
      (
        android.hardware.Camera TheCamera,
        android.graphics.SurfaceTexture TheTexture
      )
      {
        try
          {
            TheCamera.setPreviewTexture(TheTexture);
          }
        catch (java.io.IOException What)
          {
            throw new RuntimeException(What.toString());
          } /*try*/
      } /*SetCameraPreviewTexture*/

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

    private static java.util.Map<Integer, String> ImageFormats = new java.util.HashMap<Integer, String>();
    static
      {
        ImageFormats.put(ImageFormat.JPEG, "JPEG");
        ImageFormats.put(ImageFormat.NV16, "NV16");
        ImageFormats.put(ImageFormat.NV21, "NV21");
        ImageFormats.put(ImageFormat.RGB_565, "RGB_565");
        ImageFormats.put(ImageFormat.YUY2, "YUY2");
        ImageFormats.put(ImageFormat.YV12, "YV12");
      } /*static*/

    private class CommonListener
        implements
            android.hardware.SensorEventListener,
            android.hardware.Camera.PreviewCallback,
            MainView.Handler
      {
        private android.hardware.Camera TheCamera;
        private android.graphics.Point PreviewSize, RotatedPreviewSize;
        private Compass Needle;
        private GLBitmapView BackgroundBits;
        private GLTextureView BackgroundTex;
        private android.graphics.SurfaceTexture BackgroundTexture;
        private int[] ImageBuf;
        private int Rotation;
        private int ViewWidth, ViewHeight;
        private Mat4f TextureMatrix, ProjectionMatrix;

        public CommonListener()
          {
            TextureMatrix = Mat4f.identity(); /* default */
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

        private <EltType> void DumpList
          (
            String Description,
            java.util.List<EltType> TheList,
            java.util.Map<EltType, String> Symbols,
            java.io.PrintStream Out
          )
          {
            Out.print(Description);
            if (TheList != null)
              {
                Out.print(" ");
                boolean First = true;
                for (Object Val : TheList)
                  {
                    if (!First)
                      {
                        Out.print(", ");
                      } /*if*/
                    Out.print(Val);
                    if (Symbols != null)
                      {
                        final String Name = Symbols.get(Val);
                        Out.print("(" + (Name != null ? Name : "?") + ")");
                      } /*if*/
                    First = false;
                  } /*for*/
              }
            else
              {
                Out.print("(none)");
              } /*if*/
            Out.println();
          } /*DumpList*/

        private <EltType> void DumpList
          (
            String Description,
            java.util.List<EltType> TheList,
            java.io.PrintStream Out
          )
          {
            DumpList(Description, TheList, null, Out);
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
                if (false) /* debug */
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
                        ImageFormats,
                        System.err
                      );
                    DumpList
                      (
                        " Supported preview formats:",
                        Parms.getSupportedPreviewFormats(),
                        ImageFormats,
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
                System.err.printf("Set preview size to %d*%d, rotated %d*%d (at most %d*%d), HasPreviewTextures = %s\n", PreviewSize.x, PreviewSize.y, RotatedPreviewSize.x, RotatedPreviewSize.y, Graphical.getWidth(), Graphical.getHeight(), HasPreviewTextures); /* debug */
                TheCamera.setPreviewCallback(this);
                if (HasPreviewTextures)
                  {
                    if (BackgroundTexture == null)
                      {
                        System.err.printf("3DCompass BackgroundTex texture ID = %d\n", BackgroundTex.GetTextureID()); /* debug */
                        BackgroundTexture = new android.graphics.SurfaceTexture(BackgroundTex.GetTextureID());
                        BackgroundTexture.setOnFrameAvailableListener /* debug */
                          (
                            new android.graphics.SurfaceTexture.OnFrameAvailableListener()
                              {
                                public void onFrameAvailable
                                  (
                                    android.graphics.SurfaceTexture TheTexture
                                  )
                                  {
                                    System.err.println("3DCompass.Main SurfaceTexture frame available");
                                  } /*onFrameAvailable*/
                              } /*SurfaceTexture.OnFrameAvailableListener*/
                          );
                      } /*if*/
                    SetCameraPreviewTexture(TheCamera, BackgroundTexture);
                  }
                else
                  {
                    ImageBuf = new int[PreviewSize.x * PreviewSize.y];
                  } /*if*/
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
                if (BackgroundTexture != null)
                  {
                    SetCameraPreviewTexture(TheCamera, null);
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
            if (BackgroundTexture != null) /* TBD race condition with GL thread setting to null! */
              {
                Graphical.Render.Synchronize
                  (
                    new Runnable()
                      {
                        public void run()
                          {
                            BackgroundTexture.updateTexImage();
                          } /*run*/
                      } /*Runnable*/
                  );
                final float[] m = new float[16];
                BackgroundTexture.getTransformMatrix(m);
                TextureMatrix = new Mat4f(m);
                  { /* debug */
                    System.err.print("3DCompass TextureMatrix = [");
                    for (int i = 0; i < 16; ++i)
                      {
                        if (i != 0)
                          {
                            System.err.print(",");
                          } /*if*/
                        System.err.printf("%.3f", m[i]);
                      } /*for*/
                    System.err.println("]");
                  } /* debug */
              }
            else if (ImageBuf != null) /* can still get preview frames during rotate? */
              {
                CameraUseful.DecodeNV21
                  (
                    /*SrcWidth =*/ PreviewSize.x,
                    /*SrcHeight =*/ PreviewSize.y,
                    /*Data =*/ Data,
                    /*Rotate =*/ CameraUseful.CanTellCameraPresent() ? 0 : Rotation,
                    /*Alpha =*/ 255,
                    /*Pixels =*/ ImageBuf
                  );
              } /*if*/
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
            this.ViewWidth = ViewWidth;
            this.ViewHeight = ViewHeight;
            if (Needle != null)
              {
                Needle.Unbind(true);
                Needle = null;
              } /*if*/
            if (BackgroundTex != null) /* force re-creation to match view dimensions */
              {
                if (BackgroundTexture != null)
                  {
                    if (TheCamera != null)
                      {
                        SetCameraPreviewTexture(TheCamera, null);
                      } /*if*/
                    if (HasSurfaceTextureRelease)
                      {
                        BackgroundTexture.release();
                      } /*if*/
                    BackgroundTexture = null;
                  } /*if*/
                BackgroundTex.Unbind(true);
                BackgroundTex = null;
                BackgroundBits = null;
              } /*if*/
            Needle = new Compass(true);
            if (HasPreviewTextures)
              {
                BackgroundTex = new GLTextureView
                  (
                    /*CustomFragShading =*/ null,
                    /*InvertY =*/ false,
                    /*IsSurfaceTexture =*/ true,
                    /*BindNow =*/ true
                  );
              }
            else
              {
                BackgroundBits = new GLBitmapView(ViewWidth, ViewHeight, true);
                BackgroundTex = BackgroundBits;
              } /*if*/
            gl.glEnable(gl.GL_CULL_FACE);
            gl.glViewport(0, 0, ViewWidth, ViewHeight);
            final float ViewSize = Math.max(ViewWidth, ViewHeight);
            ProjectionMatrix =
                    Mat4f.frustum
                      (
                        /*L =*/ - ViewSize / ViewHeight * 0.1f,
                        /*R =*/ ViewSize / ViewHeight * 0.1f,
                        /*B =*/ - ViewSize / ViewWidth * 0.1f,
                        /*T =*/ ViewSize / ViewWidth * 0.1f,
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
            GLUseful.ClearColor(new GLUseful.Color(0)); /* all pixels initially transparent */
            gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
            gl.glDisable(gl.GL_DEPTH_TEST);
            if (BackgroundTexture == null && ImageBuf != null)
              {
                final android.graphics.Canvas g = BackgroundBits.Draw;
                g.drawColor(0, android.graphics.PorterDuff.Mode.SRC);
                  /* initialize all pixels to fully transparent */
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
                BackgroundBits.DrawChanged();
              } /*if*/
            if (BackgroundTexture != null || ImageBuf != null)
              {
                final float WidthShrink = RotatedPreviewSize.x * 1.0f / ViewWidth;
                final float HeightShrink = RotatedPreviewSize.y * 1.0f / ViewHeight;
                final boolean FitHeight = /* true to fit height and shrink width, false to shrink height and fit width */
                        WidthShrink
                    <
                        HeightShrink;
                final float Left = FitHeight ? - WidthShrink : -1.0f;
                final float Bottom = FitHeight ? -1.0f : - HeightShrink;
                final float Right = FitHeight ? WidthShrink : 1.0f;
                final float Top = FitHeight ? 1.0f : HeightShrink;
                final float Depth = 0.99f;
                final float Fudge = 0.09f; /* to leave off junk around edge of image */
                BackgroundTex.Draw
                  (
                        Mat4f.map_cuboid
                          (
                            /*src_lo =*/ TextureMatrix.xform(new Vec3f(Fudge - 1.0f, Fudge - 1.0f, 0.0f)),
                            /*src_hi =*/ TextureMatrix.xform(new Vec3f(1.0f - Fudge, 1.0f - Fudge, 1.0f)),
                            /*dst_lo =*/ new Vec3f(Left, Bottom, Depth),
                            /*dst_hi =*/ new Vec3f(Right, Top, Depth + 1.0f)
                          )
                    .mul(
                        TextureMatrix
                    )
                  );
              } /*if*/
            final Mat4f Orientation =
                (
                    Mat4f.rotation(Mat4f.AXIS_Z, (1 - Rotation) * (float)Math.PI / 2.0f, false)
                ).mul(
                    Mat4f.rotation(Mat4f.AXIS_Y, Roll, false)
                ).mul(
                    Mat4f.rotation(Mat4f.AXIS_X, Elev, false)
                ).mul(
                    Mat4f.rotation(Mat4f.AXIS_Z, Azi, false)
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
            BackgroundTexture = null;
            BackgroundTex = null;
            BackgroundBits = null;
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

  } /*Main*/;
