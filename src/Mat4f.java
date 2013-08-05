package nz.gen.geek_central.GLUseful;
/*
    Functional 3D matrix operations. Matrix elements are in row-major order.
    Vectors are treated as column vectors and premultiplied, i.e.

        [x']   [m11 m12 m13 m14]   [x]
        [y'] = [m21 m22 m23 m24] × [y]
        [z']   [m31 m32 m33 m34]   [z]
        [w']   [m41 m42 m43 m44]   [w]

    Q: Why not use the android.opengl.Matrix class?
    A: Because that is procedural (updates state in an existing
       object), this is functional (computes values from
       expressions). The procedural approach requires you to perform a
       sequence of calls on intermediate variables to set up a complex
       transformation, whereas the functional approach allows you to
       write it as a single expression, closer to the way it is
       formulated mathematically. For an example in this source file,
       see below how general rotation about an arbitrary axis is
       composed out of a sequence of simpler rotation components.
    Q: But doesn't that involve a whole lot of extra heap allocations?
    A: Isn't that the point of using Java?

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

public class Mat4f
  /* 4x4 matrices */
  {
    public final float
        m11, m12, m13, m14,
        m21, m22, m23, m24,
        m31, m32, m33, m34,
        m41, m42, m43, m44;

    public Mat4f
      (
        float m11,
        float m12,
        float m13,
        float m14,
        float m21,
        float m22,
        float m23,
        float m24,
        float m31,
        float m32,
        float m33,
        float m34,
        float m41,
        float m42,
        float m43,
        float m44
      )
      {
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m14 = m14;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m24 = m24;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        this.m34 = m34;
        this.m41 = m41;
        this.m42 = m42;
        this.m43 = m43;
        this.m44 = m44;
      } /*Mat4f*/

    public Mat4f
      (
        float[] m
      )
      {
        if (m.length == 16)
          {
            m11 = m[0];
            m21 = m[1];
            m31 = m[2];
            m41 = m[3];
            m12 = m[4];
            m22 = m[5];
            m32 = m[6];
            m42 = m[7];
            m13 = m[8];
            m23 = m[9];
            m33 = m[10];
            m43 = m[11];
            m14 = m[12];
            m24 = m[13];
            m34 = m[14];
            m44 = m[15];
          }
        else if (m.length == 9)
          {
            m11 = m[0];
            m21 = m[1];
            m31 = m[2];
            m41 = 0.0f;
            m12 = m[3];
            m22 = m[4];
            m32 = m[5];
            m42 = 0.0f;
            m13 = m[6];
            m23 = m[7];
            m33 = m[8];
            m43 = 0.0f;
            m14 = 0.0f;
            m24 = 0.0f;
            m34 = 0.0f;
            m44 = 1.0f;
          }
        else
          {
            throw new RuntimeException("need 9 or 16 array elements");
          } /*if*/
      } /*Mat4f*/

    public float[] to_floats
      (
        boolean by_cols, /* false to go by rows */
        int nrelts /* 9 or 16 */
      )
      {
        if (nrelts != 9 && nrelts != 16)
          {
            throw new RuntimeException("must return 9 or 16 array elements");
          } /*if*/
        final float[] m = new float[nrelts];
        switch (nrelts)
          {
        case 9:
            if (by_cols)
              {
                m[0] = m11;
                m[1] = m21;
                m[2] = m31;
                m[3] = m12;
                m[4] = m22;
                m[5] = m32;
                m[6] = m13;
                m[7] = m23;
                m[8] = m33;
              }
            else
              {
                m[0] = m11;
                m[1] = m12;
                m[2] = m13;
                m[3] = m21;
                m[4] = m22;
                m[5] = m23;
                m[6] = m31;
                m[7] = m32;
                m[8] = m33;
              } /*if*/
        break;
        case 16:
            if (by_cols)
              {
                m[0] = m11;
                m[1] = m21;
                m[2] = m31;
                m[3] = m41;
                m[4] = m12;
                m[5] = m22;
                m[6] = m32;
                m[7] = m42;
                m[8] = m13;
                m[9] = m23;
                m[10] = m33;
                m[11] = m43;
                m[12] = m14;
                m[13] = m24;
                m[14] = m34;
                m[15] = m44;
              }
            else
              {
                m[0] = m11;
                m[1] = m12;
                m[2] = m13;
                m[3] = m14;
                m[4] = m21;
                m[5] = m22;
                m[6] = m23;
                m[7] = m24;
                m[8] = m31;
                m[9] = m32;
                m[10] = m33;
                m[11] = m34;
                m[12] = m41;
                m[13] = m42;
                m[14] = m43;
                m[15] = m44;
              } /*if*/
        break;
          } /*switch*/
        return
            m;
      } /*to_floats*/

    public static Mat4f zero()
      {
        return
            new Mat4f
              (
                0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 0.0f
              );
      } /*Mat4f*/

    public static Mat4f identity()
      {
        return
            new Mat4f
              (
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
              );
      } /*identity*/

    public boolean eq
      (
        Mat4f m
      )
      /* equality to another Matrix. */
      {
        return
                m11 == m.m11
            &&
                m12 == m.m12
            &&
                m13 == m.m13
            &&
                m14 == m.m14
            &&
                m21 == m.m21
            &&
                m22 == m.m22
            &&
                m23 == m.m23
            &&
                m24 == m.m24
            &&
                m31 == m.m31
            &&
                m32 == m.m32
            &&
                m33 == m.m33
            &&
                m34 == m.m34
            &&
                m41 == m.m41
            &&
                m42 == m.m42
            &&
                m43 == m.m43
            &&
                m44 == m.m44;
      } /*eq*/

    public Mat4f trn()
      /* returns the transpose. */
      {
        return new Mat4f
          (
            m11, m21, m31, m41,
            m12, m22, m32, m42,
            m13, m23, m33, m43,
            m14, m24, m34, m44
          );
      } /*trn*/

    public Mat4f mul
      (
        Mat4f m
      )
      {
        return
            new Mat4f
              (
                m11 * m.m11 + m12 * m.m21 + m13 * m.m31 + m14 * m.m41,
                m11 * m.m12 + m12 * m.m22 + m13 * m.m32 + m14 * m.m42,
                m11 * m.m13 + m12 * m.m23 + m13 * m.m33 + m14 * m.m43,
                m11 * m.m14 + m12 * m.m24 + m13 * m.m34 + m14 * m.m44,

                m21 * m.m11 + m22 * m.m21 + m23 * m.m31 + m24 * m.m41,
                m21 * m.m12 + m22 * m.m22 + m23 * m.m32 + m24 * m.m42,
                m21 * m.m13 + m22 * m.m23 + m23 * m.m33 + m24 * m.m43,
                m21 * m.m14 + m22 * m.m24 + m23 * m.m34 + m24 * m.m44,

                m31 * m.m11 + m32 * m.m21 + m33 * m.m31 + m34 * m.m41,
                m31 * m.m12 + m32 * m.m22 + m33 * m.m32 + m34 * m.m42,
                m31 * m.m13 + m32 * m.m23 + m33 * m.m33 + m34 * m.m43,
                m31 * m.m14 + m32 * m.m24 + m33 * m.m34 + m34 * m.m44,

                m41 * m.m11 + m42 * m.m21 + m43 * m.m31 + m44 * m.m41,
                m41 * m.m12 + m42 * m.m22 + m43 * m.m32 + m44 * m.m42,
                m41 * m.m13 + m42 * m.m23 + m43 * m.m33 + m44 * m.m43,
                m41 * m.m14 + m42 * m.m24 + m43 * m.m34 + m44 * m.m44
              );
      } /*mul*/

    public float det()
      /* returns determinant. */
      {
      /* compute a suitable set of sub-determinants which can each be used twice: */
        final float[] m = to_floats(true, 16); /* easier this way */
        final float[] det2 =
            {
                m[10] * m[15] - m[11] * m[14],
                m[6] * m[15] - m[7] * m[14],
                m[6] * m[11] - m[7] * m[10],
                m[2] * m[15] - m[3] * m[14],
                m[2] * m[11] - m[3] * m[10],
                m[2] * m[7] - m[3] * m[6],
            };
        return
                    m[0]
                *
                    (
                        m[5] * det2[0]
                    -
                        m[9] * det2[1]
                    +
                        m[13] * det2[2]
                    )
            -
                    m[4]
                *
                    (
                        m[1] * det2[0]
                    -
                        m[9] * det2[3]
                    +
                        m[13] * det2[4]
                    )
            +
                    m[8]
                *
                    (
                        m[1] * det2[1]
                    -
                        m[5] * det2[3]
                    +
                        m[13] * det2[5]
                    )
            -
                    m[12]
                *
                    (
                        m[1] * det2[2]
                    -
                        m[5] * det2[4]
                    +
                        m[9] * det2[5]
                    );
      } /*det*/

    public Mat4f adj()
      /* returns adjoint. */
      {
      /* compute a suitable set of sub-determinants which can each be used four times: */
        final float[] m = to_floats(true, 16); /* easier this way */
        final float[] det2 =
            {
                m[10] * m[15] - m[11] * m[14],
                m[6] * m[15] - m[7] * m[14],
                m[6] * m[11] - m[7] * m[10],
                m[2] * m[15] - m[3] * m[14],
                m[2] * m[11] - m[3] * m[10],
                m[2] * m[7] - m[3] * m[6],
                m[8] * m[13] - m[9] * m[12],
                m[4] * m[13] - m[5] * m[12],
                m[4] * m[9] - m[5] * m[8],
                m[0] * m[13] - m[1] * m[12],
                m[0] * m[9] - m[1] * m[8],
                m[0] * m[5] - m[1] * m[4],
            };
        return
            new Mat4f
              (
                m[5] * det2[0] - m[9] * det2[1] + m[13] * det2[2],
                - m[4] * det2[0] + m[8] * det2[1] - m[12] * det2[2],
                m[7] * det2[6] - m[11] * det2[7] + m[15] * det2[8],
                - m[6] * det2[6] + m[10] * det2[7] - m[14] * det2[8],

                - m[1] * det2[0] + m[9] * det2[3] - m[13] * det2[4],
                m[0] * det2[0] - m[8] * det2[3] + m[12] * det2[4],
                - m[3] * det2[6] + m[11] * det2[9] - m[15] * det2[10],
                m[2] * det2[6] - m[10] * det2[9] + m[14] * det2[10],

                m[1] * det2[1] - m[5] * det2[3] + m[13] * det2[5],
                - m[0] * det2[1] + m[4] * det2[3] - m[12] * det2[5],
                m[3] * det2[7] - m[7] * det2[9] + m[15] * det2[11],
                - m[2] * det2[7] + m[6] * det2[9] - m[14] * det2[11],

                - m[1] * det2[2] + m[5] * det2[4] - m[9] * det2[5],
                m[0] * det2[2] - m[4] * det2[4] + m[8] * det2[5],
                - m[3] * det2[8] + m[7] * det2[10] - m[11] * det2[11],
                m[2] * det2[8] - m[6] * det2[10] + m[10] * det2[11]
              );
      } /*adj*/

    public Mat4f inv()
      /* returns inverse, or null if none. */
      {
        Mat4f m;
        final float det = this.det();
        if (det != 0.0f)
          {
            final float[] adj = this.adj().to_floats(false, 16);
            m = new Mat4f
              (
                adj[0] / det,
                adj[1] / det,
                adj[2] / det,
                adj[3] / det,
                adj[4] / det,
                adj[5] / det,
                adj[6] / det,
                adj[7] / det,
                adj[8] / det,
                adj[9] / det,
                adj[10] / det,
                adj[11] / det,
                adj[12] / det,
                adj[13] / det,
                adj[14] / det,
                adj[15] / det
              );
          }
        else
          {
            m = null;
          } /*if*/
        return
            m;
      } /*inv*/

    public static Mat4f translation
      (
        Vec3f v
      )
      /* returns a matrix that will translate by the specified vector. */
      {
        return
            new Mat4f
              (
                1.0f, 0.0f, 0.0f, v.x,
                0.0f, 1.0f, 0.0f, v.y,
                0.0f, 0.0f, 1.0f, v.z,
                0.0f, 0.0f, 0.0f, v.w
              );
      } /*translation*/

    public static Mat4f translation
      (
        float dx,
        float dy,
        float dz
      )
      /* returns a matrix that will translate by the specified amounts. */
      {
        return
            new Mat4f
              (
                1.0f, 0.0f, 0.0f, dz,
                0.0f, 1.0f, 0.0f, dy,
                0.0f, 0.0f, 1.0f, dz,
                0.0f, 0.0f, 0.0f, 1.0f
              );
      } /*translation*/

    public static Mat4f scaling
      (
        Vec3f v
      )
      /* returns a matrix that will scale about the origin by the specified vector. */
      {
        return
            new Mat4f
              (
                v.x, 0.0f, 0.0f, 0.0f,
                0.0f, v.y, 0.0f, 0.0f,
                0.0f, 0.0f, v.z, 0.0f,
                0.0f, 0.0f, 0.0f, v.w
              );
      } /*scaling*/

    public static Mat4f scaling
      (
        float sx,
        float sy,
        float sz
      )
      /* returns a matrix that will scale about the origin by the specified factors. */
      {
        return
            new Mat4f
              (
                sx, 0.0f, 0.0f, 0.0f,
                0.0f, sy, 0.0f, 0.0f,
                0.0f, 0.0f, sz, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
              );
      } /*scaling*/

    public static Mat4f scaling
      (
        Vec3f v,
        Vec3f origin
      )
      /* returns a matrix that will scale about the specified point by the specified vector. */
      {
        return
            translation(origin).mul(scaling(v)).mul(translation(origin.neg()));
      } /*scaling*/

    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    public static Mat4f rotation
      (
        int axis, /* AXIS_X, AXIS_Y or AXIS_Z */
        float radians
      )
      {
        final float cos = (float)Math.cos(radians);
        final float sin = (float)Math.sin(radians);
        Mat4f m;
        switch (axis)
          {
        case AXIS_X:
            m = new Mat4f
              (
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, cos, - sin, 0.0f,
                0.0f, sin, cos, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
              );
        break;
        case AXIS_Y:
            m = new Mat4f
              (
                cos, 0.0f, sin, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                - sin, 0.0f, cos, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
              );
        break;
        case AXIS_Z:
            m = new Mat4f
              (
                cos, - sin, 0.0f, 0.0f,
                sin, cos, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
              );
        break;
        default:
            throw new RuntimeException("invalid rotation axis");
      /* break; */
          } /*switch*/
        return
            m;
      } /*rotation*/

    public static Mat4f rotation
      (
        int axis, /* AXIS_X, AXIS_Y or AXIS_Z */
        float radians,
        Vec3f origin
      )
      {
        return
            translation(origin).mul(rotation(axis, radians)).mul(translation(origin.neg()));
      } /*rotation*/

    public static Mat4f rotation
      (
        Vec3f axis, /* assumed unit vector */
        float radians
      )
      /* generates a rotation about an arbitrary axis passing through the origin. */
      {
        final float zangle = (float)Math.atan2(axis.y, axis.x);
          /* rotate about z into x-z plane */
        final float yangle = (float)Math.atan2
          (
            axis.z,
            Math.sqrt(axis.y * axis.y + axis.x * axis.x)
          );
          /* rotate within x-z plane about y to align with x-axis */
        return
                rotation(AXIS_Z, zangle)
            .mul(
                rotation(AXIS_Y, - yangle)
            ).mul(
                rotation(AXIS_X, radians)
            ).mul(
                rotation(AXIS_Y, yangle)
            ).mul(
                rotation(AXIS_Z, - zangle)
            );
      } /*rotation*/

    public static Mat4f rotation
      (
        Vec3f axis,
        float radians,
        Vec3f origin
      )
      /* generates a rotation about an arbitrary axis. */
      {
        return
                translation(origin)
            .mul(
                rotation(axis.sub(origin), radians))
            .mul(
                translation(origin.neg())
            );
      } /*rotation*/

    public static Mat4f rotate_align
      (
        Vec3f src,
        Vec3f dst
      )
      /* generates a rotation matrix which, when applied to src, turns it into dst. */
      {
        return
            rotation(src.cross(dst).unit(), (float)Math.acos(src.unit().dot(dst.unit())));
      } /*rotate_align*/

    public static Mat4f frustum
      (
        float L,
        float R,
        float B,
        float T,
        float N,
        float F
      )
      {
        return
            new Mat4f
              (
                2 * N / (R - L), 0.0f, (R + L) / (R - L), 0.0f,
                0.0f, 2 * N / (T - B), (T + B) / (T - B), 0.0f,
                0.0f, 0.0f, - (F + N) / (F - N), - 2 * F * N / (F - N),
                0.0f, 0.0f, -1.0f, 0.0f
              );
      } /*frustum*/

    public static Mat4f ortho
      (
        float L,
        float R,
        float B,
        float T,
        float N,
        float F
      )
      {
        return
            new Mat4f
              (
                2 / (R - L), 0.0f, 0.0f, - (R + L) / (R - L),
                0.0f, 2 / (T - B), 0.0f, - (T + B) / (T - B),
                0.0f, 0.0f, -2 / (F - N), - (F + N) / (F - N),
                0.0f, 0.0f, 0.0f, 1.0f
              );
      } /*ortho*/

    public static Mat4f map_cuboid
      (
        Vec3f src_lo,
        Vec3f src_hi,
        Vec3f dst_lo,
        Vec3f dst_hi
      )
      /* returns a matrix that maps the axis-aligned cuboid defined by
        opposite corners src_lo and src_hi to the one with opposite
        corners dst_lo and dst_hi. */
      {
        return
            (
                translation(dst_lo)
            ).mul(
                scaling(dst_hi.sub(dst_lo))
            ).mul(
                scaling(src_hi.sub(src_lo).recip())
            ).mul(
                translation(src_lo.neg())
            );
      } /*map_cuboid*/

    public Vec3f xform
      (
        Vec3f v
      )
      /* returns the transformation of v by the matrix. */
      {
        return
            new Vec3f
              (
                v.x * m11 + v.y * m12 + v.z * m13 + v.w * m14,
                v.x * m21 + v.y * m22 + v.z * m23 + v.w * m24,
                v.x * m31 + v.y * m32 + v.z * m33 + v.w * m34,
                v.x * m41 + v.y * m42 + v.z * m43 + v.w * m44
              );
      } /*xform*/

    public Vec3f[] xform
      (
        Vec3f[] v
      )
      /* returns the transformation of the elements of v by the matrix. */
      {
        final java.util.ArrayList<Vec3f> result = new java.util.ArrayList<Vec3f>();
        for (Vec3f elt : v)
          {
            result.add(xform(elt));
          } /*for*/
        return
            result.toArray(new Vec3f[v.length]);
      } /*xform*/

    public Vec3f dxform
      (
        Vec3f v
      )
      /* returns the transformation of v by the matrix, excluding translation. */
      {
        return
            new Vec3f
              (
                v.x * m11 + v.y * m12 + v.z * m13,
                v.x * m21 + v.y * m22 + v.z * m23,
                v.x * m31 + v.y * m32 + v.z * m33
              );
      } /*dxform*/

    public Vec3f[] dxform
      (
        Vec3f[] v
      )
      /* returns the transformation of the elements of v by the matrix, excluding translation. */
      {
        final java.util.ArrayList<Vec3f> result = new java.util.ArrayList<Vec3f>();
        for (Vec3f elt : v)
          {
            result.add(dxform(elt));
          } /*for*/
        return
            result.toArray(new Vec3f[v.length]);
      } /*dxform*/

  } /*Mat4f*/
