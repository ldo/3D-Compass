package nz.gen.geek_central.Compass3D;
/*
    Graphical display of sensor data.

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

import javax.microedition.khronos.opengles.GL10;

public class VectorView extends android.opengl.GLSurfaceView
  {
    float Azi, Elev, Roll;

    class VectorViewRenderer implements Renderer
      {
        private final GeomBuilder.Obj Arrow;

        public VectorViewRenderer()
          {
            super();
            final float BodyThickness = 0.15f;
            final float HeadThickness = 0.3f;
            final float HeadLengthOuter = 0.7f;
            final float HeadLengthInner = 0.4f;
            final int NrSegments = 12;
            final GeomBuilder.Vec3f BaseNormal = new GeomBuilder.Vec3f(0.0f, -1.0f, 0.0f);
            final GeomBuilder Geom = new GeomBuilder
              (
                /*GotNormals =*/ true,
                /*GotTexCoords =*/ false,
                /*GotColors =*/ false
              );
            final int Base = Geom.Add(new GeomBuilder.Vec3f(0.0f, -1.0f, 0.0f), BaseNormal, null, null);
          /* note point positions may be duplicates, but their normals
            are different to ensure proper lighting */
            int
                PrevTip = -1,
                PrevHead1 = -1,
                PrevHead2 = -1,
                PrevBodyTop1 = -1,
                PrevBodyTop2 = -1,
                PrevBodyBottom1 = -1,
                PrevBodyBottom2 = -1;
            int
                FirstTip = -1,
                FirstHead1 = -1,
                FirstHead2 = -1,
                FirstBodyTop1 = -1,
                FirstBodyTop2 = -1,
                FirstBodyBottom1 = -1,
                FirstBodyBottom2 = -1;
            final float OuterTiltCos =
                HeadThickness / (float)Math.hypot(HeadThickness, HeadLengthOuter);
            final float OuterTiltSin =
                HeadLengthOuter / (float)Math.hypot(HeadThickness, HeadLengthOuter);
            final float InnerTiltCos =
                HeadThickness / (float)Math.hypot(HeadThickness, HeadLengthInner);
            final float InnerTiltSin =
                HeadLengthInner / (float)Math.hypot(HeadThickness, HeadLengthInner);
            for (int i = 0;;)
              {
                final int
                    ThisTip,
                    ThisHead1,
                    ThisHead2,
                    ThisBodyTop1,
                    ThisBodyTop2,
                    ThisBodyBottom1,
                    ThisBodyBottom2;
                if (i < NrSegments)
                  {
                    final float Angle = (float)(2.0 * Math.PI * i / NrSegments);
                    final float Cos = android.util.FloatMath.cos(Angle);
                    final float Sin = android.util.FloatMath.sin(Angle);
                    final float FaceAngle =
                        (float)(2.0 * Math.PI * (2 * i - 1) / (2 * NrSegments));
                    final float FaceCos = android.util.FloatMath.cos(FaceAngle);
                    final float FaceSin = android.util.FloatMath.sin(FaceAngle);
                    final GeomBuilder.Vec3f TipNormal =
                        new GeomBuilder.Vec3f
                          (
                            FaceCos * OuterTiltSin,
                            OuterTiltCos,
                            FaceSin * OuterTiltSin
                          );
                    final GeomBuilder.Vec3f HeadNormal =
                        new GeomBuilder.Vec3f
                          (
                            - FaceCos * InnerTiltSin,
                            - InnerTiltCos,
                            - FaceSin * InnerTiltSin
                          );
                    final GeomBuilder.Vec3f BodyNormal = new GeomBuilder.Vec3f(FaceCos, 0.0f, FaceSin);
                    ThisTip = Geom.Add(new GeomBuilder.Vec3f(0.0f, 1.0f, 0.0f), TipNormal, null, null);
                    final GeomBuilder.Vec3f HeadPoint =
                        new GeomBuilder.Vec3f(HeadThickness * Cos, 1.0f - HeadLengthOuter, HeadThickness * Sin);
                    ThisHead1 = Geom.Add(HeadPoint, TipNormal, null, null);
                    ThisHead2 = Geom.Add(HeadPoint, HeadNormal, null, null);
                    final GeomBuilder.Vec3f BodyTopPoint =
                        new GeomBuilder.Vec3f(BodyThickness * Cos, 1.0f - HeadLengthInner, BodyThickness * Sin);
                    ThisBodyTop1 = Geom.Add(BodyTopPoint, HeadNormal, null, null);
                    ThisBodyTop2 = Geom.Add(BodyTopPoint, BodyNormal, null, null);
                    final GeomBuilder.Vec3f BodyBottomPoint =
                        new GeomBuilder.Vec3f(BodyThickness * Cos, -1.0f, BodyThickness * Sin);
                    ThisBodyBottom1 = Geom.Add(BodyBottomPoint, BodyNormal, null, null);
                    ThisBodyBottom2 = Geom.Add(BodyBottomPoint, BaseNormal, null, null);
                  }
                else
                  {
                    ThisTip = FirstTip;
                    ThisHead1 = FirstHead1;
                    ThisHead2 = FirstHead2;
                    ThisBodyTop1 = FirstBodyTop1;
                    ThisBodyTop2 = FirstBodyTop2;
                    ThisBodyBottom1 = FirstBodyBottom1;
                    ThisBodyBottom2 = FirstBodyBottom2;
                  } /*if*/
                if (i != 0)
                  {
                    Geom.AddTri(PrevHead1, ThisTip, ThisHead1);
                    Geom.AddQuad(PrevBodyTop1, PrevHead2, ThisHead2, ThisBodyTop1);
                    Geom.AddQuad(PrevBodyBottom1, PrevBodyTop2, ThisBodyTop2, ThisBodyBottom1);
                    Geom.AddTri(PrevBodyBottom2, ThisBodyBottom2, Base);
                  }
                else
                  {
                    FirstTip = ThisTip;
                    FirstHead1 = ThisHead1;
                    FirstHead2 = ThisHead2;
                    FirstBodyTop1 = ThisBodyTop1;
                    FirstBodyTop2 = ThisBodyTop2;
                    FirstBodyBottom1 = ThisBodyBottom1;
                    FirstBodyBottom2 = ThisBodyBottom2;
                  } /*if*/
                PrevTip = ThisTip;
                PrevHead1 = ThisHead1;
                PrevHead2 = ThisHead2;
                PrevBodyTop1 = ThisBodyTop1;
                PrevBodyTop2 = ThisBodyTop2;
                PrevBodyBottom1 = ThisBodyBottom1;
                PrevBodyBottom2 = ThisBodyBottom2;
                if (i == NrSegments)
                    break;
                ++i;
              } /*for*/
            Arrow = Geom.MakeObj();
          } /*VectorViewRenderer*/

        public void onDrawFrame
          (
            GL10 gl
          )
          {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
          /* Note that, by positioning the light _before_ doing all the
            rotate calls, its position is fixed relative to the phone,
            not the compass arrow. */
            gl.glLightfv
              (
                /*light =*/ GL10.GL_LIGHT0,
                /*pname =*/ GL10.GL_POSITION,
                /*params =*/ new float[] {0.0f, 2.0f, -2.0f, 1.0f},
                /*offset =*/ 0
              );
            gl.glLightfv
              (
                /*light =*/ GL10.GL_LIGHT0,
                /*pname =*/ GL10.GL_AMBIENT,
                /*params =*/ new float[] {0.4f, 0.4f, 0.4f, 1.0f},
                /*offset =*/ 0
              );
            gl.glLightfv
              (
                /*light =*/ GL10.GL_LIGHT0,
                /*pname =*/ GL10.GL_SPECULAR,
                /*params =*/ new float[] {0.7f, 0.7f, 0.7f, 1.0f},
                /*offset =*/ 0
              );
            gl.glTranslatef(0, 0, -3.0f);
            gl.glRotatef(Roll, 0, 1, 0);
            gl.glRotatef(Elev, 1, 0, 0);
            gl.glRotatef(Azi, 0, 0, 1);
            gl.glScalef(2.0f, 2.0f, 2.0f);
            gl.glFrontFace(GL10.GL_CCW);
            gl.glMaterialfv
              (
                /*face =*/ GL10.GL_FRONT_AND_BACK,
                /*pname =*/ GL10.GL_AMBIENT,
                /*params =*/ new float[] {0.4f, 0.4f, 0.4f, 1.0f},
                /*offset =*/ 0
              );
            gl.glMaterialfv
              (
                /*face =*/ GL10.GL_FRONT_AND_BACK,
                /*pname =*/ GL10.GL_SPECULAR,
                /*params =*/ new float[] {0.6f, 0.6f, 0.36f, 1.0f},
                /*offset =*/ 0
              );
            Arrow.Draw(gl);
          } /*onDrawFrame*/

        public void onSurfaceChanged
          (
            GL10 gl,
            int ViewWidth,
            int ViewHeight
          )
          {
            gl.glViewport(0, 0, ViewWidth, ViewHeight);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glFrustumf
              (
                /*l =*/ - (float)ViewWidth / ViewHeight,
                /*r =*/ (float)ViewWidth / ViewHeight,
                /*b =*/ -1.0f,
                /*t =*/ 1.0f,
                /*n =*/ 1.0f,
                /*f =*/ 10.0f
              );
          } /*onSurfaceChanged*/

        public void onSurfaceCreated
          (
            GL10 gl,
            javax.microedition.khronos.egl.EGLConfig Config
          )
          {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_LIGHTING);
            gl.glEnable(GL10.GL_LIGHT0);
            gl.glEnable(GL10.GL_DEPTH_TEST);
          } /*onSurfaceCreated*/

      } /*VectorViewRenderer*/

    public VectorView
      (
        android.content.Context TheContext,
        android.util.AttributeSet TheAttributes
      )
      {
        super(TheContext, TheAttributes);
        setRenderer(new VectorViewRenderer());
        setRenderMode(RENDERMODE_WHEN_DIRTY);
      } /*VectorView*/

    public void SetData
      (
        float[] NewData
      )
      {
        Azi = NewData[0];
          /* always Earth-horizontal, regardless of orientation of phone */
        Elev = NewData[1];
          /* always around X-axis of phone, +ve is top-down, -ve is top-up */
        Roll = NewData[2];
          /* always around Y-axis of phone, +ve is anticlockwise
            viewed from bottom, -ve is clockwise, until it reaches
            ±90° when it its starts decreasing in magnitude again, so
            0° is when phone is horizontal either face-up or face-down */
        requestRender();
      } /*SetData*/

  } /*VectorView*/
