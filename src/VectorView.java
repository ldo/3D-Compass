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
        private final java.nio.IntBuffer NormalBuffer;
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
          /* adds two triangles to represent a quadrilateral face. */
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
            final int Vertices[], Normals[];
            final short Indices[];
              {
                final int one = 0x10000;
                final float BodyThickness = 0.15f;
                final float HeadThickness = 0.3f;
                final float HeadLengthOuter = 0.7f;
                final float HeadLengthInner = 0.4f;
                final int NrSegments = 12;
                final ArrayList<Vec3f> Points = new ArrayList<Vec3f>();
                final ArrayList<Vec3f> PointNormals = new ArrayList<Vec3f>();
                final ArrayList<Integer> Faces = new ArrayList<Integer>();
                final Vec3f BaseNormal = new Vec3f(0.0f, -1.0f, 0.0f);
                final int Base = Points.size();
                Points.add(new Vec3f(0.0f, -1.0f, 0.0f));
                PointNormals.add(BaseNormal);
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
                        final Vec3f TipNormal =
                            new Vec3f
                              (
                                FaceCos * OuterTiltSin,
                                OuterTiltCos,
                                FaceSin * OuterTiltSin
                              );
                        final Vec3f HeadNormal =
                            new Vec3f
                              (
                                - FaceCos * InnerTiltSin,
                                - InnerTiltCos,
                                - FaceSin * InnerTiltSin
                              );
                        final Vec3f BodyNormal = new Vec3f(FaceCos, 0.0f, FaceSin);
                        ThisTip = Points.size();
                        Points.add
                          (
                            new Vec3f(0.0f, 1.0f, 0.0f)
                          );
                        PointNormals.add(TipNormal);
                        final Vec3f HeadPoint =
                            new Vec3f(HeadThickness * Cos, 1.0f - HeadLengthOuter, HeadThickness * Sin);
                        ThisHead1 = Points.size();
                        Points.add(HeadPoint);
                        PointNormals.add(TipNormal);
                        ThisHead2 = Points.size();
                        Points.add(HeadPoint);
                        PointNormals.add(HeadNormal);
                        final Vec3f BodyTopPoint =
                            new Vec3f(BodyThickness * Cos, 1.0f - HeadLengthInner, BodyThickness * Sin);
                        ThisBodyTop1 = Points.size();
                        Points.add(BodyTopPoint);
                        PointNormals.add(HeadNormal);
                        ThisBodyTop2 = Points.size();
                        Points.add(BodyTopPoint);
                        PointNormals.add(BodyNormal);
                        final Vec3f BodyBottomPoint =
                            new Vec3f(BodyThickness * Cos, -1.0f, BodyThickness * Sin);
                        ThisBodyBottom1 = Points.size();
                        Points.add(BodyBottomPoint);
                        PointNormals.add(BodyNormal);
                        ThisBodyBottom2 = Points.size();
                        Points.add(BodyBottomPoint);
                        PointNormals.add(BaseNormal);
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
                        Faces.add(PrevHead1);
                        Faces.add(ThisTip);
                        Faces.add(ThisHead1);
                        AddQuad(Faces, PrevBodyTop1, PrevHead2, ThisHead2, ThisBodyTop1);
                        AddQuad(Faces, PrevBodyBottom1, PrevBodyTop2, ThisBodyTop2, ThisBodyBottom1);
                        Faces.add(PrevBodyBottom2);
                        Faces.add(ThisBodyBottom2);
                        Faces.add(Base);
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
                final ArrayList<Integer> VertsFixed = new ArrayList<Integer>();
                final ArrayList<Integer> NormalsFixed = new ArrayList<Integer>();
                for (int i = 0; i < Points.size(); ++i)
                  {
                    final Vec3f Point = Points.get(i);
                    VertsFixed.add(new Integer((int)(Point.x * one)));
                    VertsFixed.add(new Integer((int)(Point.y * one)));
                    VertsFixed.add(new Integer((int)(Point.z * one)));
                    final Vec3f PointNormal = PointNormals.get(i);
                    NormalsFixed.add(new Integer((int)(PointNormal.x * one)));
                    NormalsFixed.add(new Integer((int)(PointNormal.y * one)));
                    NormalsFixed.add(new Integer((int)(PointNormal.z * one)));
                  } /*for*/
                Vertices = new int[VertsFixed.size()];
                Normals = new int[VertsFixed.size()];
                for (int i = 0; i < Vertices.length; ++i)
                  {
                    Vertices[i] = VertsFixed.get(i);
                    Normals[i] = NormalsFixed.get(i);
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
            NormalBuffer =
                java.nio.ByteBuffer.allocateDirect(Normals.length * 4)
                .order(java.nio.ByteOrder.nativeOrder())
                .asIntBuffer();
            NormalBuffer.put(Normals);
            NormalBuffer.position(0);
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
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
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
            gl.glVertexPointer(3, GL10.GL_FIXED, 0, VertexBuffer);
            gl.glNormalPointer(GL10.GL_FIXED, 0, NormalBuffer);
          /* gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); */
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
            gl.glDrawElements(GL10.GL_TRIANGLES, NrIndexes, GL10.GL_UNSIGNED_SHORT, IndexBuffer);
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
