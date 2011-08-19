package nz.gen.geek_central.Compass3D;

import android.util.FloatMath;

public class Vec3f
  /* 3D vectors */
  {
    public final float x, y, z, w;

    public Vec3f
      (
        float x,
        float y,
        float z
      )
      {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1.0f;
      } /*Vec3f*/

    public Vec3f
      (
        float x,
        float y,
        float z,
        float w
      )
      {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
      } /*Vec3f*/

    public Vec3f
      (
        float[] v
      )
      {
        if (v.length != 3 && v.length != 4)
          {
            throw new RuntimeException("need 3 or 4 floats to make a vector");
          } /*if*/
        x = v[0];
        y = v[1];
        z = v[2];
        w = v.length == 4 ? v[3] : 1.0f;
      } /*Vec3f*/

    public float[] to_floats
      (
        int nrelts /* 3 or 4 */
      )
      {
        float[] v;
        switch (nrelts)
          {
        case 3:
            v = new float[] {x, y, z};
        break;
        case 4:
            v = new float[] {x, y, z, w};
        break;
        default:
            throw new RuntimeException("vector can only convert to 3 or 4 floats");
      /* break; */
          } /*switch*/
        return
            v;
      } /*to_floats*/

    public static Vec3f zero()
      {
        return
            new Vec3f(0.0f, 0.0f, 0.0f);
      } /*zero*/

    public Vec3f neg()
      {
        return
            new Vec3f(-x, -y, -z, w);
      } /*neg*/

    public Vec3f add
      (
        Vec3f v
      )
      {
        return
            new Vec3f(x + v.x, y + v.y, z + v.z);
      } /*add*/

    public Vec3f sub
      (
        Vec3f v
      )
      {
        return
            new Vec3f(x - v.x, y - v.y, z - v.z);
      } /*sub*/

    public Vec3f mul
      (
        float s
      )
      {
        return
            new Vec3f(x * s, y * s, z * s, w);
      } /*mul*/

    public float dot
      (
        Vec3f v
      )
      {
        return
            v.x * this.x + v.y * this.y + v.z * this.z;
      } /*dot*/

    public Vec3f cross
      (
        Vec3f v
      )
      {
        return
            new Vec3f
              (
                this.y * v.z - v.y * this.z,
                this.z * v.x - v.z * this.x,
                this.x * v.y - v.x * this.y
              );
      } /*cross*/

    public float azimuth()
        /* returns the angle between the x-axis and the line from the origin to the point. */
      {
        return
            (float)Math.atan2(y, x);
      } /*azimuth*/

    public float elevation()
        /* returns the angle between the x-y plane and the line from the origin to the point. */
      {
        return
            (float)Math.atan2(z, FloatMath.sqrt(x * x + y * y));
      } /*elevation*/

    public float abs()
        /* returns the distance between the point and the origin. */
      {
        return
            android.util.FloatMath.sqrt(x * x + y * y + z * z);
      } /*abs*/

    public Vec3f unit()
      {
        final float abs = this.abs();
        return
            new Vec3f(x / abs, y / abs, z / abs);
      } /*unit*/

  } /*Vec3f*/
