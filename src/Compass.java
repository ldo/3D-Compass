package nz.gen.geek_central.Compass3D;
/*
    Graphical display of compass arrow.

    Copyright 2011, 2013 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

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

import nz.gen.geek_central.GLUseful.Mat4f;
import nz.gen.geek_central.GLUseful.Vec3f;
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

    public Compass
      (
        boolean BindNow
      )
      {
        final float OuterTiltCos =
            HeadThickness / (float)Math.hypot(HeadThickness, HeadLengthOuter);
        final float OuterTiltSin =
            HeadLengthOuter / (float)Math.hypot(HeadThickness, HeadLengthOuter);
        final float InnerTiltCos =
            HeadThickness / (float)Math.hypot(HeadThickness, HeadLengthInner);
        final float InnerTiltSin =
            HeadLengthInner / (float)Math.hypot(HeadThickness, HeadLengthInner);
        final Vec3f[] Points =
            new Vec3f[]
              {
                new Vec3f(0.0f, 1.0f, 0.0f),
                new Vec3f(HeadThickness, 1.0f - HeadLengthOuter, 0.0f),
                new Vec3f(BodyThickness, 1.0f - HeadLengthInner, 0.0f),
                new Vec3f(BodyThickness, BaseBevel - 1.0f, 0.0f),
                new Vec3f(BodyThickness - BaseBevel, -0.98f, 0.0f),
                  /* y-coord of -1.0 seems to produce gaps in rendering when base
                    is face-on to viewer */
                new Vec3f(0.0f, -1.0f, 0.0f),
              };
        final Vec3f[] Normals =
            new Vec3f[]
              {
                new Vec3f(OuterTiltSin, OuterTiltCos, 0.0f), /* tip */
                new Vec3f(InnerTiltSin, - InnerTiltCos, 0.0f), /* head */
                new Vec3f(1.0f, 0.0f, 0.0f), /* body */
                new Vec3f
                  (
                    (float)Math.sqrt(0.5f),
                    -(float)Math.sqrt(0.5f),
                    0.0f
                  ), /* bevel */
                new Vec3f(0.0f, -1.0f, 0.0f), /* base */
              };
        Arrow = Lathe.Make
          (
            /*Shaded =*/ true,
            /*Points =*/
                new Lathe.VertexFunc()
                  {
                    public Vec3f Get
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
                    public Vec3f Get
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
                            Vec3f.circle * SectorIndex / NrSectors;
                        final Vec3f OrigNormal =
                            Normals[PointIndex - (Upper ? 0 : 1)];
                        return
                            new Vec3f
                              (
                                OrigNormal.x * (float)Math.cos(FaceAngle),
                                OrigNormal.y,
                                OrigNormal.x * (float)Math.sin(FaceAngle)
                              );
                      } /*Get*/
                  } /*VectorFunc*/,
            /*TexCoord = */ null,
            /*VertexColor =*/ null,
            /*NrSectors =*/ NrSectors,
            /*Uniforms =*/ null,
            /*VertexColorCalc =*/
                "    vec3 arrow_color = vec3(0.6, 0.6, 0.6);\n" +
                "    vec3 light_direction = vec3(-0.7, 0.7, 0.0);\n" +
                "    float light_brightness = 1.0;\n" +
                "    float light_contrast = 0.5;\n" +
                "    float attenuate = 1.2 - 0.4 * gl_Position.z;\n" +
                "    frag_color = vec4\n" +
                "      (\n" +
                "            arrow_color\n" +
                "        *\n" +
                "            attenuate\n" +
                "        *\n" +
                "            (\n" +
                "                light_brightness\n" +
                "            -\n" +
                "                light_contrast\n" +
                "            +\n" +
                "                    light_contrast\n" +
                "                *\n" +
                "                    dot\n" +
                "                      (\n" +
                "                        normalize(model_view * vec4(vertex_normal, 1.0)).xyz,\n" +
                "                        normalize(light_direction)\n" +
                "                      )\n" +
                "            ),\n" +
                "        1.0\n" +
                "      );\n" +
              /* simpleminded non-specular lighting */
                "    back_color = vec4(vec3(0.5, 0.5, 0.5) * attenuate, 1.0);\n",
            /*BindNow =*/ BindNow
          );
      } /*Compass*/

    public void Draw
      (
        Mat4f ProjectionMatrix,
        Mat4f ModelViewmatrix
      )
      /* draws the compass arrow through the specified transformations. */
      {
        Arrow.Draw
          (
            /*ProjectionMatrix =*/ ProjectionMatrix,
            /*ModelViewMatrix =*/ ModelViewmatrix,
            /*Uniforms =*/ null
          );
      } /*Draw*/

    public void Unbind
      (
        boolean Release
          /* true iff GL context still valid, so explicitly free up allocated resources.
            false means GL context has gone (or is going), so simply forget allocated
            GL resources without making any GL calls. */
      )
      /* frees up GL resources associated with this object. */
      {
        Arrow.Unbind(Release);
      } /*Release*/

  } /*Compass*/;
