package nz.gen.geek_central.Compass3D;
/*
    Graphical display of compass arrow.

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

import android.opengl.GLES11;
import nz.gen.geek_central.GLUseful.GeomBuilder;
import nz.gen.geek_central.GLUseful.Lathe;

public class Compass
  {
  /* parameters for compass arrow: */
    private static final float BodyThickness = 0.15f;
    private static final float HeadThickness = 0.3f;
    private static final float HeadLengthOuter = 0.7f;
    private static final float HeadLengthInner = 0.4f;
    private static final float BaseBevel = 0.2f * BodyThickness;
    private static final int NrSectors = 12;

    private final GeomBuilder.Obj Arrow;

    public Compass()
      {
        final float OuterTiltCos =
            HeadThickness / (float)Math.hypot(HeadThickness, HeadLengthOuter);
        final float OuterTiltSin =
            HeadLengthOuter / (float)Math.hypot(HeadThickness, HeadLengthOuter);
        final float InnerTiltCos =
            HeadThickness / (float)Math.hypot(HeadThickness, HeadLengthInner);
        final float InnerTiltSin =
            HeadLengthInner / (float)Math.hypot(HeadThickness, HeadLengthInner);
        final GeomBuilder.Vec3f[] Points =
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
              };
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
                new Lathe.VertexFunc()
                  {
                    public GeomBuilder.Vec3f Get
                      (
                        int PointIndex
                      )
                      {
                        return
                            Points[PointIndex];
                      } /*Get*/
                  } /*VertexFunc*/,
            /*NrPoints = */ Points.length,
            /*Normal =*/
                new Lathe.VectorFunc()
                  {
                    public GeomBuilder.Vec3f Get
                      (
                        int PointIndex,
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
      } /*Compass*/

    public void Setup
      (
        int ViewWidth,
        int ViewHeight
      )
      /* initial setup for drawing that doesn't need to be done for every frame. */
      {
        GLES11.glEnable(GLES11.GL_CULL_FACE);
        GLES11.glShadeModel(GLES11.GL_SMOOTH);
        GLES11.glEnable(GLES11.GL_LIGHTING);
        GLES11.glEnable(GLES11.GL_LIGHT0);
        GLES11.glEnable(GLES11.GL_DEPTH_TEST);
        GLES11.glViewport(0, 0, ViewWidth, ViewHeight);
        GLES11.glMatrixMode(GLES11.GL_PROJECTION);
        GLES11.glLoadIdentity();
        GLES11.glFrustumf
          (
            /*l =*/ - (float)ViewWidth / ViewHeight,
            /*r =*/ (float)ViewWidth / ViewHeight,
            /*b =*/ -1.0f,
            /*t =*/ 1.0f,
            /*n =*/ 1.0f,
            /*f =*/ 10.0f
          );
      } /*Setup*/

    public void Draw
      (
        float Azi,
          /* always Earth-horizontal, regardless of orientation of phone */
        float Elev,
          /* always around X-axis of phone, +ve is top-down, -ve is top-up */
        float Roll
          /* always around Y-axis of phone, +ve is anticlockwise
            viewed from bottom, -ve is clockwise, until it reaches
            ±90° when it its starts decreasing in magnitude again, so
            0° is when phone is horizontal either face-up or face-down */
      )
      /* draws the compass arrow in the specified orientation. Setup must already
        have been called on current GL context. */
      {
        GLES11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES11.glClear(GLES11.GL_COLOR_BUFFER_BIT | GLES11.GL_DEPTH_BUFFER_BIT);
        GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
        GLES11.glLoadIdentity();
      /* Note that, by positioning the light _before_ doing all the
        rotate calls, its position is fixed relative to the display,
        not the compass arrow. */
        GLES11.glLightfv
          (
            /*light =*/ GLES11.GL_LIGHT0,
            /*pname =*/ GLES11.GL_POSITION,
            /*params =*/ new float[] {0.0f, 2.0f, -2.0f, 1.0f},
            /*offset =*/ 0
          );
        GLES11.glLightfv
          (
            /*light =*/ GLES11.GL_LIGHT0,
            /*pname =*/ GLES11.GL_AMBIENT,
            /*params =*/ new float[] {0.4f, 0.4f, 0.4f, 1.0f},
            /*offset =*/ 0
          );
        GLES11.glLightfv
          (
            /*light =*/ GLES11.GL_LIGHT0,
            /*pname =*/ GLES11.GL_SPECULAR,
            /*params =*/ new float[] {0.7f, 0.7f, 0.7f, 1.0f},
            /*offset =*/ 0
          );
        GLES11.glTranslatef(0, 0, -3.0f);
        GLES11.glRotatef(Roll, 0, 1, 0);
        GLES11.glRotatef(Elev, 1, 0, 0);
        GLES11.glRotatef(Azi, 0, 0, 1);
        GLES11.glScalef(2.0f, 2.0f, 2.0f);
        GLES11.glFrontFace(GLES11.GL_CCW);
        GLES11.glMaterialfv
          (
            /*face =*/ GLES11.GL_FRONT_AND_BACK,
            /*pname =*/ GLES11.GL_AMBIENT,
            /*params =*/ new float[] {0.4f, 0.4f, 0.4f, 1.0f},
            /*offset =*/ 0
          );
        GLES11.glMaterialfv
          (
            /*face =*/ GLES11.GL_FRONT_AND_BACK,
            /*pname =*/ GLES11.GL_SPECULAR,
            /*params =*/ new float[] {0.6f, 0.6f, 0.36f, 1.0f},
            /*offset =*/ 0
          );
        Arrow.Draw();
      } /*Draw*/

  } /*Compass*/
