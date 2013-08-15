package nz.gen.geek_central.GLUseful;
/*
    Display of an OpenGL texture within an on-screen view.

    Copyright 2013 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

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

import static nz.gen.geek_central.GLUseful.GLUseful.gl;

public class GLTextureView
  {
    protected final GLUseful.Program ViewProg;
    protected final int TextureTarget;
    protected boolean Bound;
    protected int TextureID;
    protected int MappingVar, VertexPositionVar;
    protected final GLUseful.FixedVec2Buffer ViewCorners;
    protected final GLUseful.VertIndexBuffer ViewIndices;

    public GLTextureView
      (
        String CustomFragShading,
          /* optional replacement for fragment shader calculation,
            defaults to "gl_FragColor = texture2D(view_image, image_coord);"
            if omitted */
        boolean InvertY,
          /* true if Y-coordinate of texture increases downwards (Canvas convention),
            false if it increases upwards (usual OpenGL convention) */
        boolean IsSurfaceTexture,
        boolean BindNow
          /* true to do GL calls now, false to defer to later call to Bind or Draw */
      )
      {
        final StringBuilder VS = new StringBuilder();
        VS.append
          (
            "uniform mat4 mapping;\n" +
            "attribute vec2 vertex_position;\n" +
            "varying vec2 image_coord;\n" +
            "\n" +
            "void main()\n" +
            "  {\n" +
            "    gl_Position = mapping * vec4(2.0 * vertex_position.x - 1.0, 2.0 * vertex_position.y - 1.0, 0.0, 1.0);\n" +
            "    image_coord = vec2(vertex_position.x, "
          );
        VS.append
          (
            InvertY ? "1.0 - " : ""
          );
        VS.append
          (
            "vertex_position.y);\n" +
            "  }/*main*/\n"
          );
        ViewProg = new GLUseful.Program
          (
          /* vertex shader: */
            VS.toString(),
          /* fragment shader: */
                "precision mediump float;\n"
            +
                (IsSurfaceTexture ?
                    "#extension GL_OES_EGL_image_external : require\n"
                :
                    ""
                )
            +
                "uniform " + (IsSurfaceTexture ? "samplerExternalOES" : "sampler2D") + " view_image;\n"
            +
                "varying vec2 image_coord;\n" +
                "\n" +
                "void main()\n" +
                "  {\n"
            +
                "    " +
                (CustomFragShading != null ?
                    CustomFragShading
                :
                    "gl_FragColor = texture2D(view_image, image_coord);"
                ) +
                "\n"
            +
                "  }/*main*/\n",
            BindNow
          );
        TextureTarget = IsSurfaceTexture ? android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES : gl.GL_TEXTURE_2D;
        Bound = false;
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
        if (BindNow)
          {
            Bind();
          } /*if*/
      } /*GLTextureView*/

    public void Bind()
      {
        if (!Bound)
          {
            GLUseful.ClearError();
              {
                final int[] TextureIDs = new int[1];
                gl.glGenTextures(1, TextureIDs, 0);
                TextureID = TextureIDs[0];
              }
            gl.glBindTexture(TextureTarget, TextureID);
            GLUseful.CheckError("binding current texture for view");
            gl.glTexParameteri(TextureTarget, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(TextureTarget, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);
            gl.glTexParameteri(TextureTarget, gl.GL_TEXTURE_MIN_FILTER, gl.GL_LINEAR);
            gl.glTexParameteri(TextureTarget, gl.GL_TEXTURE_MAG_FILTER, gl.GL_LINEAR);
            gl.glBindTexture(TextureTarget, 0);
            ViewProg.Use();
            gl.glUniform1i(ViewProg.GetUniform("view_image", true), 0);
            GLUseful.CheckError("setting view texture sampler");
            MappingVar = ViewProg.GetUniform("mapping", true);
            VertexPositionVar = ViewProg.GetAttrib("vertex_position", true);
            OnBind();
            Bound = true;
          } /*if*/
      } /*Bind*/

    public void Unbind
      (
        boolean Release
          /* true iff GL context still valid, so explicitly free up allocated resources.
            false means GL context has gone (or is going), so simply forget allocated
            GL resources without making any GL calls. */
      )
      /* frees up GL resources associated with this object. */
      {
        if (Bound)
          {
            ViewProg.Unbind(Release);
            if (Release)
              {
                gl.glDeleteTextures(1, new int[] {TextureID}, 0);
              } /*if*/
            OnUnbind(Release);
            Bound = false;
          } /*if*/
      } /*Unbind*/

    protected void OnBind()
      /* Override to do additional allocation of resources. */
      {
      } /*OnBind*/

    protected void OnUnbind
      (
        boolean Release
      )
      /* Override to do additional deallocation of resources. */
      {
      } /*OnUnbind*/

    public int GetTextureID()
      /* returns the texture ID to use for actually defining the texture. */
      {
        Bind();
        return
            TextureID;
      } /*GetTextureID*/

    public void Draw
      (
        Mat4f Mapping
          /* should map opposite corners at (-1, -1, 0) .. (+1, +1, 0) into
            desired bounds at desired depth */
      )
      /* renders the image into the current GL context. */
      {
        Bind();
        GLUseful.ClearError();
        ViewProg.Use();
        GLUseful.UniformMatrix4(MappingVar, Mapping);
        ViewCorners.Apply(VertexPositionVar, true);
        gl.glEnable(gl.GL_BLEND);
        gl.glBlendFunc(gl.GL_ONE, gl.GL_ONE_MINUS_SRC_ALPHA); /* for transparency */
        OnDefineTexture();
        ViewIndices.Draw();
        gl.glBindTexture(TextureTarget, 0);
        gl.glBlendFunc(gl.GL_ONE, gl.GL_ZERO); /* restore default */
        gl.glDisable(gl.GL_BLEND); /* restore default */
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
      /* renders the image into the current GL context. */
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

    protected void OnDefineTexture()
      /* Override to do additional texture setup. */
      {
        gl.glActiveTexture(gl.GL_TEXTURE0);
        GLUseful.CheckError("setting current texture for view");
        gl.glBindTexture(TextureTarget, TextureID);
        GLUseful.CheckError("binding current texture for view");
      } /*OnDefineTexture*/

  } /*GLTextureView*/;
