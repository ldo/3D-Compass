package nz.gen.geek_central.Compass3D;
/*
    Graphical display of sensor data.
*/

import javax.microedition.khronos.opengles.GL10;

class GraphicsUseful
  {

    public static android.graphics.Paint FillWithColor
      (
        int TheColor
      )
      /* returns a Paint that will fill with a solid colour. */
      {
        final android.graphics.Paint ThePaint = new android.graphics.Paint();
        ThePaint.setStyle(android.graphics.Paint.Style.FILL);
        ThePaint.setColor(TheColor);
        return
            ThePaint;
      } /*FillWithColor*/

  } /*GraphicsUseful*/

public class VectorView extends android.opengl.GLSurfaceView
  {
    float Azi, Elev, Roll;

    class VectorViewRenderer implements Renderer
      {
        private final java.nio.IntBuffer VertexBuffer;
        private final java.nio.ByteBuffer IndexBuffer;

        public VectorViewRenderer()
          {
            super();
            final int one = 0x10000;
            final int vertices[] =
              {
                -one, -one, -one,
                one, -one, -one,
                one, one, -one,
                -one, one, -one,
                -one, -one, one,
                one, -one, one,
                one, one, one,
                -one, one, one,
              };
            final byte indices[] =
              {
                0, 4, 5,    0, 5, 1,
                1, 5, 6,    1, 6, 2,
                2, 6, 7,    2, 7, 3,
                3, 7, 4,    3, 4, 0,
                4, 7, 6,    4, 6, 5,
                3, 0, 1,    3, 1, 2,
              };
          /* Need to use allocateDirect to allocate buffers so garbage
            collector won't move them. Also make sure byte order is
            always native. But direct-allocation and order-setting methods
            are only available for ByteBuffer. Which is why buffers
            are allocated as ByteBuffers and then converted to more
            appropriate types. */
            VertexBuffer =
                java.nio.ByteBuffer.allocateDirect(vertices.length * 4)
                .order(java.nio.ByteOrder.nativeOrder())
                .asIntBuffer();
            VertexBuffer.put(vertices);
            VertexBuffer.position(0);
            IndexBuffer = java.nio.ByteBuffer.allocateDirect(indices.length);
            IndexBuffer.put(indices);
            IndexBuffer.position(0);
          } /*VectorViewRenderer*/

        public void onDrawFrame
          (
            GL10 gl
          )
          {
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            // TBD how to fill background arc?
            final float Radius = 125.0f;
            // Draw.translate(Radius, Radius);
            // Draw.drawArc /* background */
              // (
                // /*oval =*/ new android.graphics.RectF(-Radius, -Radius, Radius, Radius),
                // /*startAngle =*/ 0.0f,
                // /*sweepAngle =*/ 360.0f,
                // /*useCenter =*/ false,
                // /*paint =*/ GraphicsUseful.FillWithColor(0xffffffa2)
              // );
            // TBD
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glLightfv
              (
                /*light =*/ GL10.GL_LIGHT0,
                /*pname =*/ GL10.GL_POSITION,
                /*params =*/ new float[] {2.0f, 0.0f, 2.0f, 1.0f},
                /*offset =*/ 0
              );
            gl.glLightfv
              (
                /*light =*/ GL10.GL_LIGHT0,
                /*pname =*/ GL10.GL_AMBIENT,
                /*params =*/ new float[] {0.2f, 0.2f, 0.2f, 1.0f},
                /*offset =*/ 0
              );
            gl.glLightfv
              (
                /*light =*/ GL10.GL_LIGHT0,
                /*pname =*/ GL10.GL_SPECULAR,
                /*params =*/ new float[] {1.0f, 1.0f, 1.0f, 1.0f},
                /*offset =*/ 0
              );
            gl.glTranslatef(0, 0, -3.0f);
            gl.glRotatef(Roll, 0, 1, 0);
            gl.glRotatef(Elev, 1, 0, 0);
            gl.glRotatef(Azi, 0, 0, 1);
          /* TBD temp */
            gl.glFrontFace(GL10.GL_CW);
            gl.glVertexPointer(3, GL10.GL_FIXED, 0, VertexBuffer);
            gl.glNormalPointer(GL10.GL_FIXED, 0, VertexBuffer);
            gl.glColor4f(0.5f, 0.5f, 0.5f, 0.5f);
            gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, IndexBuffer);
          /* more TBD */
            // final android.graphics.Path V = new android.graphics.Path();
            // final float BaseWidth = 5.0f;
            // final float EndWidth = BaseWidth * (1.0f + D.z);
              // /* taper to simulate perspective foreshortening */
            // V.moveTo(0.0f, 0.0f);
            // V.lineTo(+ BaseWidth * D.y, - BaseWidth * D.x);
            // V.lineTo
              // (
                // + EndWidth * D.y + Radius * D.x,
                // - EndWidth * D.x + Radius * D.y
              // );
            // V.lineTo
              // (
                // - EndWidth * D.y + Radius * D.x,
                // + EndWidth * D.x + Radius * D.y
              // );
            // V.lineTo(- BaseWidth * D.y, + BaseWidth * D.x);
            // V.close();
            // Draw.drawPath(V, GraphicsUseful.FillWithColor(0xff285c87));
            // TBD
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
            gl.glClearColor(1.0f, 1.0f, 0.635f, 1.0f);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
              /* GL_FLAT produces discontinuities across triangles making
                up each face--probably need better normals or texture coordinates */
            gl.glEnable(GL10.GL_LIGHTING);
            gl.glEnable(GL10.GL_LIGHT0);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
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
        Elev = NewData[1];
        Roll = NewData[2];
        requestRender();
      } /*SetData*/

  } /*VectorView*/
