package nz.gen.geek_central.GLUseful;
/*
    Easy construction of objects with a single axis of rotational symmetry,
    building on GeomBuilder.

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

public class Lathe
  {
    public interface VertexFunc
      {
        public GeomBuilder.Vec3f Get
          (
            int PointIndex
          );
      }

    public interface VectorFunc
      {
        public GeomBuilder.Vec3f Get
          (
            int PointIndex,
            int SectorIndex, /* 0 .. NrSectors - 1 */
            boolean Upper
              /* indicates which of two calls for each point (except for
                start and end points, which only get one call each) to allow
                for discontiguous shading */
          );
      } /*VectorFunc*/

    public interface ColorFunc
      {
        public GeomBuilder.Color Get
          (
            int PointIndex,
            int SectorIndex, /* 0 .. NrSectors - 1 */
            boolean Upper
              /* indicates which of two calls for each point (except for
                start and end points, which only get one call each) to allow
                for discontiguous shading */
          );
      } /*ColorFunc*/

    public static GeomBuilder.Obj Make
      (
        VertexFunc Point,
          /* returns outline of object, must be at least 3 points, all Z coords must be
            zero, all X coords non-negative, and X coord of first and last point
            must be zero. Order the points by decreasing Y coord if you want
            anticlockwise face vertex ordering, or increasing if you want clockwise. */
        int NrPoints, /* valid values for arg to Point.Get are [0 .. NrPoints - 1] */
        VectorFunc Normal, /* optional to compute normal vector at each point */
        VectorFunc TexCoord, /* optional to compute texture coordinate at each point */
        ColorFunc VertexColor, /* optional to compute colour at each point */
        int NrSectors /* must be at least 3 */
      )
      /* rotates Points around Y axis with NrSectors steps, invoking the
        specified callbacks to obtain normal vectors, texture coordinates
        and vertex colours as appropriate, and returns the constructed
        geometry object. */
      {
        final GeomBuilder Geom = new GeomBuilder
          (
            /*GotNormals =*/ Normal != null,
            /*GotTexCoords =*/ TexCoord != null,
            /*GotColors =*/ VertexColor != null
          );
        final int[] PrevInds = new int[NrPoints * 2 - 2];
        final int[] FirstInds = new int[NrPoints * 2 - 2];
        final int[] TheseInds = new int[NrPoints * 2 - 2];
        for (int i = 0;;)
          {
            if (i < NrSectors)
              {
                final float Angle = (float)(2.0 * Math.PI * i / NrSectors);
                final float Cos = android.util.FloatMath.cos(Angle);
                final float Sin = android.util.FloatMath.sin(Angle);
                for (int j = 0; j < NrPoints; ++j)
                  {
                    final GeomBuilder.Vec3f Vertex = Point.Get(j);
                    final GeomBuilder.Vec3f ThisPoint = new GeomBuilder.Vec3f
                      (
                        Vertex.x * Cos,
                        Vertex.y,
                        Vertex.x * Sin
                      );
                    if (j < NrPoints - 1)
                      {
                        final GeomBuilder.Vec3f ThisNormal =
                            Normal != null ?
                                Normal.Get(j, i, true)
                            :
                                null;
                        final GeomBuilder.Vec3f ThisTexCoord =
                            TexCoord != null ?
                                TexCoord.Get(j, i, true)
                            :
                                null;
                        final GeomBuilder.Color ThisColor =
                            VertexColor != null ?
                                VertexColor.Get(j, i, true)
                            :
                                null;
                        TheseInds[j * 2] =
                            Geom.Add(ThisPoint, ThisNormal, ThisTexCoord, ThisColor);
                      } /*if*/
                    if (j > 0)
                      {
                        final GeomBuilder.Vec3f ThisNormal =
                            Normal != null ?
                                Normal.Get(j, i, false)
                            :
                                null;
                        final GeomBuilder.Vec3f ThisTexCoord =
                            TexCoord != null ?
                                TexCoord.Get(j, i, false)
                            :
                                null;
                        final GeomBuilder.Color ThisColor =
                            VertexColor != null ?
                                VertexColor.Get(j, i, false)
                            :
                                null;
                        TheseInds[j * 2 - 1] =
                            Geom.Add(ThisPoint, ThisNormal, ThisTexCoord, ThisColor);
                      } /*if*/
                  } /*for*/
              }
            else
              {
                for (int j = 0; j < TheseInds.length; ++j)
                  {
                    TheseInds[j] = FirstInds[j];
                  } /*for*/
              } /*if*/
            if (i != 0)
              {
                Geom.AddTri(PrevInds[1], TheseInds[0], TheseInds[1]);
                for (int j = 1; j < NrPoints - 1; ++j)
                  {
                    Geom.AddQuad
                      (
                        PrevInds[j * 2 + 1],
                        PrevInds[j * 2],
                        TheseInds[j * 2],
                        TheseInds[j * 2 + 1]
                      );
                  } /*for*/
                Geom.AddTri
                  (
                    PrevInds[TheseInds.length - 2],
                    TheseInds[TheseInds.length - 2],
                    TheseInds[TheseInds.length - 1]
                  );
              }
            else
              {
                for (int j = 0; j < TheseInds.length; ++j)
                  {
                    FirstInds[j] = TheseInds[j];
                  } /*for*/
              } /*if*/
            for (int j = 0; j < TheseInds.length; ++j)
              {
                PrevInds[j] = TheseInds[j];
              } /*for*/
            if (i == NrSectors)
                break;
            ++i;
          } /*for*/
        return 
            Geom.MakeObj();
      } /*Make*/

  } /*Lathe*/
