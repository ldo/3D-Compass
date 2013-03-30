package nz.gen.geek_central.GLUseful;
/*
    Easy construction and application of buffers needed for OpenGL-ES drawing.
    This version is for OpenGL-ES 2.0 and allows customization of the vertex
    shader for control of material properties, lighting etc.

    Copyright 2011, 2012 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

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

import java.util.ArrayList;

public class GeomBuilder
  /*
    Helper class for easier construction of geometrical
    objects. Instantiate this and tell it whether each vertex will
    also have a normal vector, a texture-coordinate vector or a
    colour. Then call Add to add vertex definitions (using class Vec3f
    to define points, and Color to define colours), and use the
    returned vertex indices to construct faces with AddTri and
    AddQuad. Finally, call MakeObj to obtain a GeomBuilder.Obj that
    has a Draw method that will render the resulting geometry into a
    specified GL context.
  */
  {
    static final android.opengl.GLES20 gl = GLUseful.gl; /* for easier references */

    private final boolean Shaded;
    private final ArrayList<Vec3f> Points;
    private final ArrayList<Vec3f> PointNormals;
    private final ArrayList<Vec3f> PointTexCoords;
    private final ArrayList<GLUseful.Color> PointColors;
    private final ArrayList<Integer> Faces;
    private Vec3f BoundMin, BoundMax;

    public GeomBuilder
      (
        boolean Shaded, /* false for wireframe */
        boolean GotNormals, /* vertices will have normals specified */
        boolean GotTexCoords, /* vertices will have texture coordinates specified */
        boolean GotColors /* vertices will have colours specified */
      )
      {
        this.Shaded = Shaded;
        Points = new ArrayList<Vec3f>();
        PointNormals = GotNormals ? new ArrayList<Vec3f>() : null;
        PointTexCoords = GotTexCoords ? new ArrayList<Vec3f>() : null;
        PointColors = GotColors ? new ArrayList<GLUseful.Color>() : null;
        Faces = new ArrayList<Integer>();
        BoundMin = null;
        BoundMax = null;
      } /*GeomBuilder*/

    public int Add
      (
        Vec3f Vertex,
      /* following args are either mandatory or must be null, depending
        on respective flags passed to constructor */
        Vec3f Normal,
        Vec3f TexCoord,
        GLUseful.Color VertexColor
      )
      /* adds a new vertex, and returns its index for use in constructing faces. */
      {
        if
          (
                (PointNormals == null) != (Normal == null)
            ||
                (PointColors == null) != (VertexColor == null)
            ||
                (PointTexCoords == null) != (TexCoord == null)
          )
          {
            throw new RuntimeException("missing or redundant args specified");
          } /*if*/
        final int Result = Points.size();
        Points.add(Vertex);
        if (PointNormals != null)
          {
            PointNormals.add(Normal);
          } /*if*/
        if (PointTexCoords != null)
          {
            PointTexCoords.add(TexCoord);
          } /*if*/
        if (PointColors != null)
          {
            PointColors.add(VertexColor);
          } /*if*/
        if (BoundMin != null)
          {
            BoundMin =
                new Vec3f
                  (
                    Math.min(BoundMin.x, Vertex.x),
                    Math.min(BoundMin.y, Vertex.y),
                    Math.min(BoundMin.z, Vertex.z)
                  );
          }
        else
          {
            BoundMin = Vertex;
          } /*if*/
        if (BoundMax != null)
          {
            BoundMax =
                new Vec3f
                  (
                    Math.max(BoundMax.x, Vertex.x),
                    Math.max(BoundMax.y, Vertex.y),
                    Math.max(BoundMax.z, Vertex.z)
                  );
          }
        else
          {
            BoundMax = Vertex;
          } /*if*/
        return
            Result;
      } /*Add*/

    public void AddTri
      (
        int V1,
        int V2,
        int V3
      )
      /* defines a triangular face. Args are indices as previously returned from calls to Add. */
      {
        if (Shaded)
          {
            Faces.add(V1);
            Faces.add(V2);
            Faces.add(V3);
          }
        else
          {
            Faces.add(V1);
            Faces.add(V2);
            Faces.add(V2);
            Faces.add(V3);
            Faces.add(V3);
            Faces.add(V1);
          } /*if*/
      } /*AddTri*/

    public void AddQuad
      (
        int V1,
        int V2,
        int V3,
        int V4
      )
      /* Defines a quadrilateral face. Args are indices as previously returned from calls to Add. */
      {
        if (Shaded)
          {
            AddTri(V1, V2, V3);
            AddTri(V4, V1, V3);
          }
        else
          {
            Faces.add(V1);
            Faces.add(V2);
            Faces.add(V2);
            Faces.add(V3);
            Faces.add(V3);
            Faces.add(V4);
            Faces.add(V4);
            Faces.add(V1);
          } /*if*/
      } /*AddQuad*/

    public void AddPoly
      (
        int[] V
      )
      /* Defines a polygonal face. Array elements are indices as previously
        returned from calls to Add. */
      {
        if (Shaded)
          {
            for (int i = 1; i < V.length - 1; ++i)
              {
                AddTri(V[0], V[i], V[i + 1]);
              } /*for*/
          }
        else
          {
            for (int i = 0; i < V.length; ++i)
              {
                Faces.add(V[i]);
                Faces.add(V[(i + 1) % V.length]);
              } /*for*/
          } /*if*/
      } /*AddPoly*/

    public enum ShaderVarTypes
      {
        FLOAT,
        VEC3,
        COLOR3,
        COLOR4,
      } /*ShaderVarTypes*/;

    public static class ShaderVarDef
      /* definition of a user shader variable */
      {
        public final String Name;
        public final ShaderVarTypes Type;

        public ShaderVarDef
          (
            String Name,
            ShaderVarTypes Type
          )
          {
            this.Name = Name.intern();
            this.Type = Type;
          } /*ShaderVarDef*/

      } /*ShaderVarDef*/;

    public static class ShaderVarVal
      /* specification of the value for a user shader variable */
      {
        public final String Name;
        public final Object Value;
          /* Float for FLOAT, array of 3 floats for VEC3, GLUseful.Color for COLOR3 or COLOR4 */

        public ShaderVarVal
          (
            String Name,
            Object Value
          )
          {
            this.Name = Name.intern();
            this.Value = Value;
          } /*ShaderVarVal*/

      } /*ShaderVarVal*/;

    public static class Obj
      /* representation of complete object geometry. */
      {
        private final boolean Shaded;
        private final GLUseful.FixedVec3Buffer VertexBuffer;
        private final GLUseful.FixedVec3Buffer NormalBuffer;
        private final GLUseful.FixedVec3Buffer TexCoordBuffer;
        private final GLUseful.ByteColorBuffer ColorBuffer;
        private final GLUseful.VertIndexBuffer IndexBuffer;
        private final GLUseful.Program Render;
        public final Vec3f BoundMin, BoundMax;

        private final int ModelViewTransformVar, ProjectionTransformVar;
        private final int VertexPositionVar, VertexNormalVar, VertexColorVar;

        private static class UniformInfo
          {
            public final ShaderVarTypes Type;
            public final int Loc;

            public UniformInfo
              (
                ShaderVarTypes Type,
                int Loc
              )
              {
                this.Type = Type;
                this.Loc = Loc;
              } /*UniformInfo*/

          } /*UniformInfo*/;

        private final java.util.HashMap<String, UniformInfo> UniformLocs;

        private Obj
          (
            boolean Shaded, /* false for wireframe */
            GLUseful.FixedVec3Buffer VertexBuffer,
            GLUseful.FixedVec3Buffer NormalBuffer, /* optional */
            GLUseful.FixedVec3Buffer TexCoordBuffer, /* optional, NYI */
            GLUseful.ByteColorBuffer ColorBuffer, /* optional */
            GLUseful.VertIndexBuffer IndexBuffer,
            ShaderVarDef[] Uniforms,
              /* optional additional uniform variable definitions for vertex shader */
            String VertexColorCalc,
              /* optional, compiled as part of vertex shader to implement lighting etc, must
                assign value to "frag_color" variable */
            Vec3f BoundMin,
            Vec3f BoundMax
          )
          {
            this.Shaded = Shaded;
            this.VertexBuffer = VertexBuffer;
            this.NormalBuffer = NormalBuffer;
            this.TexCoordBuffer = TexCoordBuffer;
            this.ColorBuffer = ColorBuffer;
            this.IndexBuffer = IndexBuffer;
            this.BoundMin = BoundMin;
            this.BoundMax = BoundMax;
            final StringBuilder VS = new StringBuilder();
            VS.append("uniform mat4 model_view, projection;\n");
            VS.append("attribute vec3 vertex_position;\n");
            if (NormalBuffer != null)
              {
                VS.append("attribute vec3 vertex_normal;\n");
              } /*if*/
            if (TexCoordBuffer != null)
              {
                VS.append("attribute vec3 vertex_texcoord;\n");
              } /*if*/
            if (ColorBuffer != null)
              {
                VS.append("attribute vec3 vertex_color;\n");
              } /*if*/
            if (Uniforms != null)
              {
                for (ShaderVarDef VarDef : Uniforms)
                  {
                    VS.append("uniform ");
                    switch (VarDef.Type)
                      {
                    case FLOAT:
                        VS.append("float");
                    break;
                    case VEC3:
                    case COLOR3:
                        VS.append("vec3");
                    break;
                    case COLOR4:
                        VS.append("vec4");
                    break;
                      } /*switch*/
                    VS.append(" ");
                    VS.append(VarDef.Name);
                    VS.append(";\n");
                  } /*for*/
              } /*if*/
            if (Shaded)
              {
                VS.append("varying vec4 frag_color, back_color;\n");
              }
            else
              {
                VS.append("varying vec4 frag_color;\n");
              } /*if*/
            VS.append("\n");
            VS.append("void main()\n");
            VS.append("  {\n");
            VS.append("    gl_Position = projection * model_view * vec4(vertex_position, 1.0);\n");
            if (Shaded)
              {
                VS.append("    back_color = vec4(0.5, 0.5, 0.5, 1.0);\n");
                  /* default if not overridden in VertexColorCalc */
              } /*if*/
            if (VertexColorCalc != null)
              {
                VS.append(VertexColorCalc);
              }
            else
              {
                VS.append
                  (
                    String.format
                      (
                        GLUseful.StdLocale,
                        "    frag_color = %s;\n",
                        ColorBuffer != null ?
                            "vertex_color"
                        :
                            "vec4(0.5, 0.5, 0.5, 1.0)"
                      )
                  );
              } /*if*/
            VS.append("  } /*main*/\n");
          /* use of vertex_texcoord NYI */
            Render = new GLUseful.Program
              (
              /* vertex shader: */
                VS.toString(),
              /* fragment shader: */
                    "precision mediump float;\n"
                +
                    (Shaded ?
                        "varying vec4 frag_color, back_color;\n"
                    :
                        "varying vec4 frag_color;\n"
                    )
                +
                    "\n" +
                    "void main()\n" +
                    "  {\n"
                +
                    (Shaded ?
                        "    if (gl_FrontFacing)\n" +
                        "        gl_FragColor = frag_color;\n" +
                        "    else\n" +
                        "        gl_FragColor = back_color;\n"
                    :
                        "    gl_FragColor = frag_color;\n"
                    )
                +
                    "  } /*main*/\n"
              );
            ModelViewTransformVar = Render.GetUniform("model_view", true);
            ProjectionTransformVar = Render.GetUniform("projection", true);
            VertexPositionVar = Render.GetAttrib("vertex_position", true);
            VertexNormalVar = Render.GetAttrib("vertex_normal", false);
            VertexColorVar = Render.GetAttrib("vertex_color", false);
            if (Uniforms != null)
              {
                UniformLocs = new java.util.HashMap<String, UniformInfo>();
                for (ShaderVarDef VarDef : Uniforms)
                  {
                    UniformLocs.put
                      (
                        VarDef.Name,
                        new UniformInfo(VarDef.Type, Render.GetUniform(VarDef.Name, false))
                      );
                  } /*for*/
              }
            else
              {
                UniformLocs = null;
              } /*if*/
          } /*Obj*/

        public void Draw
          (
            Mat4f ProjectionMatrix,
            Mat4f ModelViewMatrix,
            ShaderVarVal[] Uniforms /* optional additional values for uniforms */
          )
          /* actually renders the geometry into the current GL context. */
          {
            Render.Use();
            GLUseful.UniformMatrix4(ProjectionTransformVar, ProjectionMatrix);
            GLUseful.UniformMatrix4(ModelViewTransformVar, ModelViewMatrix);
            if ((Uniforms != null) != (UniformLocs != null))
              {
                throw new RuntimeException("uniform defs/vals mismatch");
              } /*if*/
            VertexBuffer.Apply(VertexPositionVar, true);
            if (NormalBuffer != null)
              {
                NormalBuffer.Apply(VertexNormalVar, true);
              } /*if*/
            if (ColorBuffer != null)
              {
                ColorBuffer.Apply(VertexColorVar, true);
              } /*if*/
            if (Uniforms != null)
              {
                for (ShaderVarVal VarRef : Uniforms)
                  {
                    final UniformInfo VarInfo = UniformLocs.get(VarRef.Name);
                    if (VarInfo == null)
                      {
                        throw new RuntimeException("no such uniform variable “" + VarRef.Name + "”");
                      } /*if*/
                    switch (VarInfo.Type)
                      {
                    case FLOAT:
                        gl.glUniform1f(VarInfo.Loc, (Float)VarRef.Value);
                    break;
                    case VEC3:
                          {
                            final float[] Value = (float[])VarRef.Value;
                            gl.glUniform3f(VarInfo.Loc, Value[0], Value[1], Value[2]);
                          }
                    break;
                    case COLOR3:
                          {
                            final GLUseful.Color TheColor = (GLUseful.Color)VarRef.Value;
                            gl.glUniform3f(VarInfo.Loc, TheColor.r, TheColor.g, TheColor.b);
                          }
                    break;
                    case COLOR4:
                          {
                            final GLUseful.Color TheColor = (GLUseful.Color)VarRef.Value;
                            gl.glUniform4f(VarInfo.Loc, TheColor.r, TheColor.g, TheColor.b, TheColor.a);
                          }
                    break;
                      } /*switch*/
                  } /*for*/
              } /*if*/
            IndexBuffer.Draw();
            Render.Unuse();
          } /*Draw*/

        public void Release()
          /* frees up GL resources associated with this object. */
          {
            Render.Release();
          } /*Release*/

      } /*Obj*/;

    public Obj MakeObj
      (
        ShaderVarDef[] Uniforms,
          /* optional additional uniform variable definitions for vertex shader */
        String VertexColorCalc
          /* optional, compiled as part of vertex shader to implement lighting etc, must
            assign value to "frag_color" variable */
      )
      /* constructs and returns the final geometry ready for rendering. */
      {
        if (Points.size() == 0)
          {
            throw new RuntimeException("GeomBuilder: empty object");
          } /*if*/
        return
            new Obj
              (
                /*Shaded =*/ Shaded,
                /*VertexBuffer =*/ new GLUseful.FixedVec3Buffer(Points),
                /*NormalBuffer =*/
                    PointNormals != null ?
                        new GLUseful.FixedVec3Buffer(PointNormals)
                    :
                        null,
                /*TexCoordBuffer =*/
                    PointTexCoords != null ?
                        new GLUseful.FixedVec3Buffer(PointTexCoords)
                    :
                        null,
                /*ColorBuffer =*/
                    PointColors != null ?
                        new GLUseful.ByteColorBuffer(PointColors)
                    :
                        null,
                /*IndexBuffer =*/
                    new GLUseful.VertIndexBuffer
                      (
                        /*FromArray =*/ Faces,
                        /*Mode =*/ Shaded ? gl.GL_TRIANGLES : gl.GL_LINES
                      ),
                Uniforms,
                VertexColorCalc,
                BoundMin,
                BoundMax
              );
      } /*MakeObj*/

  } /*GeomBuilder*/
