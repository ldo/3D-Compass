package nz.gen.geek_central.Compass3D;
/*
    Graphical display of sensor data.
*/

import java.util.ArrayList;
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
        private final java.nio.ShortBuffer IndexBuffer;
        final int NrIndexes;

        private void AddQuad
          (
            ArrayList<Integer> Faces,
            int Ind1,
            int Ind2,
            int Ind3,
            int Ind4
          )
          {
            Faces.add(Ind1);
            Faces.add(Ind2);
            Faces.add(Ind3);
            Faces.add(Ind4);
            Faces.add(Ind1);
            Faces.add(Ind3);
          } /*AddQuad*/

        public VectorViewRenderer()
          {
            super();
            final int Vertices[];
            final short Indices[];
              {
                final int one = 0x10000;
                final float BodyThickness = 0.05f;
                final float HeadThickness = 0.1f;
                final float HeadLength = 0.1f;
                final int NrSegments = 6; /* small value for testing */
                final ArrayList<Vec3f> Points = new ArrayList<Vec3f>();
                final ArrayList<Integer> Faces = new ArrayList<Integer>();
                Points.add(new Vec3f(0.0f, 1.0f, 0.0f));
                Points.add(new Vec3f(0.0f, -1.0f, -.0f));
                final int Tip = Points.size() - 2;
                final int Base = Points.size() - 1;
                int PrevHead = -1, PrevBodyTop = -1, PrevBodyBottom = -1;
                int FirstHead = -1, FirstBodyTop = -1, FirstBodyBottom = -1;
                for (int i = 0;;)
                  {
                    final int ThisHead, ThisBodyTop, ThisBodyBottom;
                    if (i < NrSegments)
                      {
                        final float Angle = (float)(2.0 * Math.PI * i / NrSegments);
                        final float Cos = android.util.FloatMath.cos(Angle);
                        final float Sin = android.util.FloatMath.sin(Angle);
                        Points.add
                          (
                            new Vec3f(HeadThickness * Cos, 1.0f - HeadLength, HeadThickness * Sin)
                          );
                        Points.add
                          (
                            new Vec3f(BodyThickness * Cos, 1.0f - HeadLength, BodyThickness * Sin)
                          );
                        Points.add
                          (
                            new Vec3f(BodyThickness * Cos, -1.0f, BodyThickness * Sin)
                          );
                        ThisHead = Points.size() - 3;
                        ThisBodyTop = Points.size() - 2;
                        ThisBodyBottom = Points.size() - 1;
                      }
                    else
                      {
                        ThisHead = FirstHead;
                        ThisBodyTop = FirstBodyTop;
                        ThisBodyBottom = FirstBodyBottom;
                      } /*if*/
                    if (i != 0)
                      {
                        Faces.add(PrevHead);
                        Faces.add(Tip);
                        Faces.add(ThisHead);
                        AddQuad(Faces, PrevBodyTop, PrevHead, ThisHead, ThisBodyTop);
                        AddQuad(Faces, PrevBodyBottom, PrevBodyTop, ThisBodyTop, ThisBodyBottom);
                        Faces.add(PrevBodyBottom);
                        Faces.add(ThisBodyBottom);
                        Faces.add(Base);
                      }
                    else
                      {
                        FirstHead = ThisHead;
                        FirstBodyTop = ThisBodyTop;
                        FirstBodyBottom = ThisBodyBottom;
                      } /*if*/
                    PrevHead = ThisHead;
                    PrevBodyTop = ThisBodyTop;
                    PrevBodyBottom = ThisBodyBottom;
                    if (i == NrSegments)
                        break;
                    ++i;
                  } /*for*/
                final ArrayList<Integer> VertsFixed = new ArrayList<Integer>();
                for (int i = 0; i < Points.size(); ++i)
                  {
                    final Vec3f Point = Points.get(i);
                    VertsFixed.add(new Integer((int)(Point.x * one)));
                    VertsFixed.add(new Integer((int)(Point.y * one)));
                    VertsFixed.add(new Integer((int)(Point.z * one)));
                  } /*for*/
                Vertices = new int[VertsFixed.size()];
                for (int i = 0; i < Vertices.length; ++i)
                  {
                    Vertices[i] = VertsFixed.get(i);
                  } /*for*/
                Indices = new short[Faces.size()];
                NrIndexes = Indices.length;
                for (int i = 0; i < NrIndexes; ++i)
                  {
                    Indices[i] = (short)(int)Faces.get(i);
                  } /*for*/
              }
          /* Need to use allocateDirect to allocate buffers so garbage
            collector won't move them. Also make sure byte order is
            always native. But direct-allocation and order-setting methods
            are only available for ByteBuffer. Which is why buffers
            are allocated as ByteBuffers and then converted to more
            appropriate types. */
            VertexBuffer =
                java.nio.ByteBuffer.allocateDirect(Vertices.length * 4)
                .order(java.nio.ByteOrder.nativeOrder())
                .asIntBuffer();
            VertexBuffer.put(Vertices);
            VertexBuffer.position(0);
            IndexBuffer =
                java.nio.ByteBuffer.allocateDirect(Indices.length * 2)
                .order(java.nio.ByteOrder.nativeOrder())
                .asShortBuffer();
            IndexBuffer.put(Indices);
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
            gl.glScalef(2.0f, 2.0f, 2.0f);
            gl.glFrontFace(GL10.GL_CW);
            gl.glVertexPointer(3, GL10.GL_FIXED, 0, VertexBuffer);
          /* gl.glNormalPointer(GL10.GL_FIXED, 0, VertexBuffer); */
            gl.glColor4f(0.5f, 0.5f, 0.5f, 1.0f);
            gl.glDrawElements(GL10.GL_TRIANGLES, NrIndexes, GL10.GL_UNSIGNED_SHORT, IndexBuffer);
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
          /* gl.glEnableClientState(GL10.GL_NORMAL_ARRAY); */
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
