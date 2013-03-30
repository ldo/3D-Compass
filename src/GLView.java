package nz.gen.geek_central.GLUseful;
/*
    Display of a Canvas-rendered image within an OpenGL view.

    Copyright 2012 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

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

import android.graphics.Bitmap;

public class GLView
  {
    static final android.opengl.GLES20 gl = GLUseful.gl; /* for easier references */

    public final android.graphics.Canvas Draw;
      /* do your drawing into here before calling the Draw method to push it to the GL display */
    public final Bitmap Bits;
    private boolean SendBits;

    public final int BitsWidth, BitsHeight;

    private final GLUseful.Program ViewProg;
    private final int TextureID;
    private final int MappingVar, VertexPositionVar;
    private final GLUseful.FixedVec2Buffer ViewCorners;
    private final GLUseful.VertIndexBuffer ViewIndices;

    public GLView
      (
        int BitsWidth, /* dimensions of the Bitmap to create */
        int BitsHeight
      )
      {
        this.BitsWidth = BitsWidth;
        this.BitsHeight = BitsHeight;
        Bits = Bitmap.createBitmap
          (
            /*width =*/ BitsWidth,
            /*height =*/ BitsHeight,
            /*config =*/ Bitmap.Config.ARGB_8888
          );
        Draw = new android.graphics.Canvas(Bits);
        ViewProg = new GLUseful.Program
          (
          /* vertex shader: */
            "uniform mat4 mapping;\n" +
            "attribute vec2 vertex_position;\n" +
            "varying vec2 image_coord;\n" +
            "\n" +
            "void main()\n" +
            "  {\n" +
            "    gl_Position = mapping * vec4(2.0 * vertex_position.x - 1.0, 2.0 * vertex_position.y - 1.0, 0.0, 1.0);\n" +
            "    image_coord = vec2(vertex_position.x, 1.0 - vertex_position.y);\n" +
              /* Y-coordinate inversion because default Canvas coordinates has Y increasing
                downwards, while OpenGL has Y increasing upwards */
            "  }/*main*/\n",
          /* fragment shader: */
            String.format
              (
                GLUseful.StdLocale,
                "precision mediump float;\n" +
                "uniform sampler2D view_image;\n" +
                "varying vec2 image_coord;\n" +
                "\n" +
                "void main()\n" +
                "  {\n" +
                "    gl_FragColor.rgba = texture2D(view_image, image_coord).%s;\n" +
                "  }/*main*/\n",
                /*IsBigEndian()*/false ? /* fixme: will this ever be true for Android? */
                    "abgr"
                :
                    "rgba"
              )
          );
          {
            final int[] TextureIDs = new int[1];
            gl.glGenTextures(1, TextureIDs, 0);
            TextureID = TextureIDs[0];
          }
        gl.glBindTexture(gl.GL_TEXTURE_2D, TextureID);
        GLUseful.CheckError("binding current texture for view");
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
        gl.glTexParameteri(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
        gl.glBindTexture(gl.GL_TEXTURE_2D, 0);
        ViewProg.Use();
        gl.glUniform1i(ViewProg.GetUniform("view_image", true), 0);
        GLUseful.CheckError("setting view texture sampler");
        MappingVar = ViewProg.GetUniform("mapping", true);
        VertexPositionVar = ViewProg.GetAttrib("vertex_position", true);
          {
            final java.util.ArrayList<GLUseful.Vec2f> Temp = new java.util.ArrayList<GLUseful.Vec2f>();
            for
              (
                GLUseful.Vec2f Vec :
                    new GLUseful.Vec2f[]
                        {
                            new GLUseful.Vec2f(0.0f, 0.0f),
                            new GLUseful.Vec2f(1.0f, 0.0f),
                            new GLUseful.Vec2f(1.0f, 1.0f),
                            new GLUseful.Vec2f(0.0f, 1.0f),
                        }
              )
              {
                Temp.add(Vec);
              } /*for*/
            ViewCorners = new GLUseful.FixedVec2Buffer(Temp);
          }
          {
            final java.util.ArrayList<Integer> Temp = new java.util.ArrayList<Integer>();
            for (int i : new int[] {0, 1, 3, 2})
              {
                Temp.add(i);
              } /*for*/
            ViewIndices = new GLUseful.VertIndexBuffer(Temp, gl.GL_TRIANGLE_STRIP);
          }
        SendBits = true;
      } /*GLView*/

    public void DrawChanged()
      /* call this to indicate that the bitmap has been changed and
        must be re-sent to the texture object. */
      {
        Bits.prepareToDraw();
        SendBits = true;
      } /*DrawChanged*/

    public void Draw
      (
        Mat4f Mapping
          /* should map opposite corners at (-1, -1, 0) .. (+1, +1, 0) into
            desired bounds at desired depth */
      )
      /* renders the bitmap into the current GL context. */
      {
        GLUseful.ClearError();
        ViewProg.Use();
        GLUseful.UniformMatrix4(MappingVar, Mapping);
        ViewCorners.Apply(VertexPositionVar, true);
        gl.glActiveTexture(gl.GL_TEXTURE0);
        GLUseful.CheckError("setting current texture for view");
        gl.glBindTexture(gl.GL_TEXTURE_2D, TextureID);
        GLUseful.CheckError("binding current texture for view");
        if (SendBits)
          {
            android.opengl.GLUtils.texImage2D
              (
                /*target =*/ gl.GL_TEXTURE_2D,
                /*level =*/ 0,
                /*bitmap =*/ Bits,
                /*border =*/ 0
              );
            GLUseful.CheckError("sending view texture image");
            SendBits = false;
          } /*if*/
        gl.glEnable(gl.GL_BLEND);
        gl.glBlendFunc(gl.GL_ONE, gl.GL_ONE_MINUS_SRC_ALPHA);
        ViewIndices.Draw();
        gl.glBlendFunc(gl.GL_ONE, gl.GL_ZERO);
        gl.glDisable(gl.GL_BLEND);
        gl.glBindTexture(gl.GL_TEXTURE_2D, 0);
      } /*Draw*/

    public void Draw
      (
        Mat4f Projection,
        float Left,
        float Bottom,
        float Right,
        float Top,
        float Depth
      )
      {
        Draw
          (
            (
                Projection
            ).mul(
                Mat4f.map_cuboid
                  (
                    /*src_lo =*/ new Vec3f(-1.0f, -1.0f, 0.0f),
                    /*src_hi =*/ new Vec3f(1.0f, 1.0f, 1.0f),
                    /*dst_lo =*/ new Vec3f(Left, Bottom, Depth),
                    /*dst_hi =*/ new Vec3f(Right, Top, Depth + 1.0f)
                  )
            )
          );
      } /*Draw*/

    public void Release()
      /* call this to free up GL and Bitmap resources. */
      {
        ViewProg.Release();
        gl.glDeleteTextures(1, new int[] {TextureID}, 0);
        Bits.recycle();
      } /*Release*/

  } /*GLView*/
