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
            final float BaseBevel = 0.2f * BodyThickness;
            final int NrSectors = 12;
            final float OuterTiltCos =
                HeadThickness / (float)Math.hypot(HeadThickness, HeadLengthOuter);
            final float OuterTiltSin =
                HeadLengthOuter / (float)Math.hypot(HeadThickness, HeadLengthOuter);
            final float InnerTiltCos =
                HeadThickness / (float)Math.hypot(HeadThickness, HeadLengthInner);
            final float InnerTiltSin =
                HeadLengthInner / (float)Math.hypot(HeadThickness, HeadLengthInner);
            final GeomBuilder.Vec3f[] Normals =
                new GeomBuilder.Vec3f[]
                  {
                    new GeomBuilder.Vec3f(OuterTiltSin, OuterTiltCos, 0.0f), /* tip */
                    new GeomBuilder.Vec3f(InnerTiltSin, - InnerTiltCos, 0.0f), /* head */
                    new GeomBuilder.Vec3f(1.0f, 0.0f, 0.0f), /* body */
                    new GeomBuilder.Vec3f
                      (
                        android.util.FloatMath.sqrt(0.5f),
                        -android.util.FloatMath.sqrt(0.5f),
                        0.0f
                      ), /* bevel */
                    new GeomBuilder.Vec3f(0.0f, -1.0f, 0.0f), /* base */
                  };
            Arrow = Lathe.Make
              (
                /*Points =*/
                    new GeomBuilder.Vec3f[]
                      {
                        new GeomBuilder.Vec3f(0.0f, 1.0f, 0.0f),
                        new GeomBuilder.Vec3f(HeadThickness, 1.0f - HeadLengthOuter, 0.0f),
                        new GeomBuilder.Vec3f(BodyThickness, 1.0f - HeadLengthInner, 0.0f),
                        new GeomBuilder.Vec3f(BodyThickness, BaseBevel - 1.0f, 0.0f),
                        new GeomBuilder.Vec3f(BodyThickness - BaseBevel, -0.98f, 0.0f),
                          /* y-coord of -1.0 seems to produce gaps in rendering when base
                            is face-on to viewer */
                        new GeomBuilder.Vec3f(0.0f, -1.0f, 0.0f),
                      },
                /*Normal =*/
                    new Lathe.VectorFunc()
                      {
                        public GeomBuilder.Vec3f Get
                          (
                            int PointIndex, /* into Points array */
                            int SectorIndex, /* 0 .. NrSectors - 1 */
                            boolean Upper
                              /* indicates which of two calls for each point (except for
                                start and end points, which only get one call each) to allow
                                for discontiguous shading */
                          )
                          {
                            final float FaceAngle =
                                (float)(2.0 * Math.PI * SectorIndex / NrSectors);
                            final GeomBuilder.Vec3f OrigNormal =
                                Normals[PointIndex - (Upper ? 0 : 1)];
                            return
                                new GeomBuilder.Vec3f
                                  (
                                    OrigNormal.x * android.util.FloatMath.cos(FaceAngle),
                                    OrigNormal.y,
                                    OrigNormal.x * android.util.FloatMath.sin(FaceAngle)
                                  );
                          } /*Get*/
                      } /*VectorFunc*/,
                /*TexCoord = */ null,
                /*VertexColor =*/ null,
                /*NrSectors =*/ NrSectors
              );
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
