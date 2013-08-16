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
import android.graphics.Point;
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
    DisplayRenderer Render;
    int TheCameraID;
    final long[] FrameTimes = new long[25];
    boolean Active = false, MainViewActive = false; /* shouldn't be any worries about race conditions accessing these... */
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
            android.hardware.Camera.PreviewCallback
      {
        private android.hardware.Camera TheCamera;

        public CommonListener()
          {
          /* nothing to do, really */
          } /*CommonListener*/

        public void Start()
          {
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
                for (EltType Val : TheList)
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
                final Point PreviewSize = CameraUseful.GetLargestPreviewSizeAtMost(TheCamera, Graphical.getWidth(), Graphical.getHeight());
                Parms.setPreviewSize(PreviewSize.x, PreviewSize.y);
                TheCamera.setParameters(Parms);
                TheCamera.setPreviewCallback(this);
                Graphical.Render.Synchronize
                  (
                    new Runnable()
                      {
                        public void run()
                          {
                            if (false)/*debug*/ Render.Rotation = (5 - Main.this.getWindowManager().getDefaultDisplay().getOrientation()) % 4;
                            Render.PreviewSize = PreviewSize;
                            Render.RotatedPreviewSize = new Point
                              (
                                (Render.Rotation & 1) != 0 ? PreviewSize.y : PreviewSize.x,
                                (Render.Rotation & 1) != 0 ? PreviewSize.x : PreviewSize.y
                              );
                            System.err.printf("Set preview size to %d*%d, rotated %d*%d (at most %d*%d), HasPreviewTextures = %s, HasSurfaceTextureRelease = %s\n", PreviewSize.x, PreviewSize.y, Render.RotatedPreviewSize.x, Render.RotatedPreviewSize.y, Graphical.getWidth(), Graphical.getHeight(), HasPreviewTextures, HasSurfaceTextureRelease); /* debug */
                            if (HasPreviewTextures)
                              {
                                if (Render.BackgroundTexture == null)
                                  {
                                    System.err.printf("3DCompass BackgroundTex texture ID = %d\n", Render.BackgroundTex.GetTextureID()); /* debug */
                                    Render.BackgroundTexture = new android.graphics.SurfaceTexture(Render.BackgroundTex.GetTextureID());
                                    if (false) Render.BackgroundTexture.setOnFrameAvailableListener /* debug */
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
                                SetCameraPreviewTexture(TheCamera, Render.BackgroundTexture);
                              }
                            else
                              {
                                Render.ImageBuf = new int[PreviewSize.x * PreviewSize.y];
                              } /*if*/
                          } /*run*/
                      } /*Runnable*/
                  );
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
                Graphical.Render.Synchronize
                  (
                    new Runnable()
                      {
                        public void run()
                          {
                            if (Render.BackgroundTexture != null)
                              {
                                SetCameraPreviewTexture(TheCamera, null);
                              } /*if*/
                            Render.ImageBuf = null;
                          } /*run*/
                      } /*Runnable*/
                  );
                TheCamera.setPreviewCallback(null);
                TheCamera.release();
                TheCamera = null;
              } /*if*/
          } /*StopCamera*/

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
            final android.hardware.SensorEvent Event
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
            Graphical.queueEvent /* use of Render.Synchronize here can cause deadlock! */
              (
                new Runnable()
                  {
                    public void run()
                      {
                        Render.Azi = (float)Math.toRadians(Event.values[0]);
                        Render.Elev = (float)Math.toRadians(Event.values[1]);
                        Render.Roll = (float)Math.toRadians(Event.values[2]);
                      } /*run*/
                  } /*Runnable*/
              );
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
            final byte[] Data,
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
            Graphical.queueEvent
              (
                new Runnable()
                  {
                    public void run()
                      {
                        if (Render.BackgroundTexture != null)
                          {
                            Render.BackgroundTexture.updateTexImage();
                            final float[] m = new float[16];
                            Render.BackgroundTexture.getTransformMatrix(m);
                            Render.TextureMatrix = new Mat4f(m);
                          }
                        else if (Render.ImageBuf != null) /* can still get preview frames during rotate? */
                          {
                            CameraUseful.DecodeNV21
                              (
                                /*SrcWidth =*/ Render.PreviewSize.x,
                                /*SrcHeight =*/ Render.PreviewSize.y,
                                /*Data =*/ Data,
                                /*Rotate =*/ CameraUseful.CanTellCameraPresent() ? 0 : Render.Rotation,
                                /*Alpha =*/ 255,
                                /*Pixels =*/ Render.ImageBuf
                              );
                          } /*if*/
                        Graphical.requestRender();
                      } /*run*/
                  } /*Runnable*/
              );
          } /*onPreviewFrame*/

      } /*CommonListener*/;

    private class DisplayRenderer implements MainView.Handler
      /* separate this out from CommonListener because these calls need to
        happen on a separate thread */
      {
        private Point PreviewSize, RotatedPreviewSize;
        private int[] ImageBuf;
        private int Rotation;

        private Compass Needle;
        private GLBitmapView BackgroundBits;
        private GLTextureView BackgroundTex;
        private android.graphics.SurfaceTexture BackgroundTexture;
        private int ViewWidth, ViewHeight;
        private Mat4f TextureMatrix, ProjectionMatrix;

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

        public DisplayRenderer()
          {
            TextureMatrix = Mat4f.identity(); /* default for non-SurfaceTexture case */
          } /*DisplayRenderer*/

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
            if (BackgroundBits != null) /* force re-creation to match view dimensions */
              {
              /* assert BackgroundTexture is null, so no need to detach preview texture from camera */
                BackgroundBits.Unbind(true);
                BackgroundTex = null;
                BackgroundBits = null;
              } /*if*/
            Needle = new Compass(true);
            if (HasPreviewTextures)
              {
                if (BackgroundTex == null)
                  {
                    BackgroundTex = new GLTextureView
                      (
                        /*CustomFragShading =*/ null,
                        /*InvertY =*/ false,
                        /*IsSurfaceTexture =*/ true,
                        /*BindNow =*/ true
                      );
                  } /*if*/
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
            runOnUiThread
              (
                new Runnable()
                  {
                    public void run()
                      {
                        if (Active)
                          {
                            Listen.StopCamera();
                            Listen.StartCamera();
                          } /*if*/
                      } /*run*/
                  } /*Runnable*/
              );
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
                final boolean FitHeight = WidthShrink < HeightShrink;
                  /* true to fit height and shrink width, false to shrink height and fit width */
                final float Left = FitHeight ? - WidthShrink : -1.0f;
                final float Bottom = FitHeight ? -1.0f : - HeightShrink;
                final float Right = FitHeight ? WidthShrink : 1.0f;
                final float Top = FitHeight ? 1.0f : HeightShrink;
                final float Depth = 0.99f; /* image can disappear on some systems if this is exactly 1.0 */
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
            if (Needle != null)
              {
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
              } /*if*/
          } /*Draw*/

        public void Release()
          {
            Graphical.queueEvent
              (
                new Runnable()
                  {
                    public void run()
                      {
                      /* losing the GL context anyway, so don't bother releasing anything: */
                        Needle = null;
                        BackgroundTexture = null;
                        BackgroundTex = null;
                        BackgroundBits = null;
                        MainViewActive = false;
                      } /*run*/
                  } /*Runnable*/
              );
          } /*Release*/

      } /*DisplayRenderer*/;

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
        Render = new DisplayRenderer();
        Graphical.SetHandler(Render);
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
      /* Render.Release(); */ /* should already have happened */
        super.onDestroy();
      } /*onDestroy*/

    @Override
    public void onPause()
      {
        Listen.Stop();
        Render.Release();
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
