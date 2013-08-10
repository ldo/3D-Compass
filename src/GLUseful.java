package nz.gen.geek_central.GLUseful;
/*
    Useful OpenGL-ES-2.0-related definitions.

    Copyright 2012, 2013 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

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

import java.util.Collection;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.ByteOrder;

public class GLUseful
  {
    public static final android.opengl.GLES20 gl = new android.opengl.GLES20(); /* for easier references */
    public static final java.util.Locale StdLocale = java.util.Locale.US; /* for when I don't actually want a locale */

    static
      {
        System.loadLibrary("gl_useful");
      } /*static*/

/*
    Error checking
*/

    public static void ThrowError
      (
        int Err,
        String DoingWhat
      )
      {
        throw new RuntimeException
          (
            String.format
              (
                StdLocale,
                "OpenGL error %d %s",
                Err,
                DoingWhat
              )
          );
      } /*ThrowError*/

    public static void ThrowError
      (
        String DoingWhat
      )
      {
        ThrowError(gl.glGetError(), DoingWhat);
      } /*ThrowError*/

    public static void ThrowError
      (
        String DoingWhat,
        Object... FmtArgs
      )
      {
        ThrowError(String.format(StdLocale, DoingWhat, FmtArgs));
      } /*ThrowError*/

    public static void CheckError
      (
        String DoingWhat
      )
      {
        int Err = gl.glGetError();
        if (Err != 0)
          {
            ThrowError(Err, DoingWhat);
          } /*if*/
      } /*CheckError*/

    public static void CheckError
      (
        String DoingWhat,
        Object... FmtArgs
      )
      {
        CheckError(String.format(StdLocale, DoingWhat, FmtArgs));
      } /*CheckError*/

    public static void ClearError()
      /* ensures there is no pending GL error condition. */
      {
        gl.glGetError(); /* and discard result */
      } /*ClearError*/

    public static native String GetShaderInfoLog
      (
        int ShaderID
      );
      /* needed because android.opengl.GLES20.glGetShaderInfoLog doesn't return anything,
        at least on Android 2.2. */

/*
    Utility types
*/

    public static class Color
      /* RGB colours with transparency. This class makes no GL calls. */
      {
        public final float r, g, b, a;

        public Color
          (
            float r,
            float g,
            float b,
            float a
          )
          {
            this.r = r;
            this.b = b;
            this.g = g;
            this.a = a;
          } /*Color*/

        public Color
          (
            int TheColor /* standard Android format */
          )
          {
            a = (TheColor >> 24 & 255) / 255.0f;
            r = (TheColor >> 16 & 255) / 255.0f;
            g = (TheColor >> 8 & 255) / 255.0f;
            b = (TheColor & 255) / 255.0f;
          } /*Color*/

        public int ToInt()
          /* converts to standard Android format. */
          {
            return
                    Math.round(a * 255) << 24
                |
                    Math.round(r * 255) << 16
                |
                    Math.round(g * 255) << 8
                |
                    Math.round(b * 255);
          } /*ToInt*/

        public float[] ToFloats
          (
            int NrComponents /* 3 or 4 */
          )
          {
            return
                NrComponents == 4 ?
                    new float[] {r, g, b, a}
                : NrComponents == 3 ?
                    new float[] {r, g, b}
                :
                    null;
          } /*ToFloats*/

      } /*Color*/;

    public static void ClearColor
      (
        Color UseColor
      )
      {
        gl.glClearColor(UseColor.r, UseColor.g, UseColor.b, UseColor.a);
      } /*ClearColor*/

    public static void UniformMatrix4
      (
        int VarLoc,
        Mat4f TheMatrix
      )
      /* passes TheMatrix as the value of the specified shader uniform. */
      {
        gl.glUniformMatrix4fv(VarLoc, 1, false, TheMatrix.to_floats(true, 16), 0);
      } /*UniformMatrix4*/

/*
    Vertex arrays

    Need to use allocateDirect to allocate buffers so garbage
    collector won't move them. Also make sure byte order is
    always native. But direct-allocation and order-setting methods
    are only available for ByteBuffer. Which is why buffers
    are allocated as ByteBuffers and then converted to more
    appropriate types.

    These classes only make GL calls in their Apply methods.
*/

    public static final int Fixed1 = 0x10000; /* for converting between float & fixed values */

    public static class FixedVec3Buffer
      /* converts a Collection of vectors to a vertex-attribute buffer. */
      {
        private final IntBuffer Buf;

        public FixedVec3Buffer
          (
            Collection<Vec3f> FromArray
          )
          {
            final int[] Vals = new int[FromArray.size() * 3];
            int jv = 0;
            for (Vec3f Vec : FromArray)
              {
                Vals[jv++] = (int)(Vec.x * Fixed1);
                Vals[jv++] = (int)(Vec.y * Fixed1);
                Vals[jv++] = (int)(Vec.z * Fixed1);
              } /*for*/
            Buf =
                ByteBuffer.allocateDirect(Vals.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(Vals);
            Buf.position(0);
          } /*FixedVec3Buffer*/

        public void Apply
          (
            int AttribLoc,
            boolean Enable
          )
          {
            if (Enable)
              {
                gl.glEnableVertexAttribArray(AttribLoc);
              } /*if*/
            gl.glVertexAttribPointer(AttribLoc, 3, gl.GL_FIXED, true, 0, Buf);
          } /*Apply*/

      } /*FixedVec3Buffer*/;

    public static class Vec2f
      {
        public final float x, y;

        public Vec2f
          (
            float x,
            float y
          )
          {
            this.x = x;
            this.y = y;
          } /*Vec2f*/

      } /*Vec2f*/;

    public static class FixedVec2Buffer
      /* converts a Collection of vectors to a vertex-attribute buffer. */
      {
        private final IntBuffer Buf;

        public FixedVec2Buffer
          (
            Collection<Vec2f> FromArray
          )
          {
            final int[] Vals = new int[FromArray.size() * 2];
            int jv = 0;
            for (Vec2f Vec : FromArray)
              {
                Vals[jv++] = (int)(Vec.x * Fixed1);
                Vals[jv++] = (int)(Vec.y * Fixed1);
              } /*for*/
            Buf =
                ByteBuffer.allocateDirect(Vals.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(Vals);
            Buf.position(0);
          } /*FixedVec2Buffer*/

        public void Apply
          (
            int AttribLoc,
            boolean Enable
          )
          {
            if (Enable)
              {
                gl.glEnableVertexAttribArray(AttribLoc);
              } /*if*/
            gl.glVertexAttribPointer(AttribLoc, 2, gl.GL_FIXED, true, 0, Buf);
          } /*Apply*/

      } /*FixedVec2Buffer*/;

    public static class ByteColorBuffer
      /* converts a Collection of colours to a vertex-attribute buffer. */
      {
        private final ByteBuffer Buf;

        public ByteColorBuffer
          (
            Collection<Color> FromArray
          )
          {
            final byte[] Vals = new byte[FromArray.size() * 4];
            int jv = 0;
            for (Color Val : FromArray)
              {
                Vals[jv++] = (byte)(Val.r * 255);
                Vals[jv++] = (byte)(Val.g * 255);
                Vals[jv++] = (byte)(Val.b * 255);
                Vals[jv++] = (byte)(Val.a * 255);
              } /*for*/
            Buf =
                ByteBuffer.allocateDirect(Vals.length)
                .order(ByteOrder.nativeOrder())
                .put(Vals);
            Buf.position(0);
          } /*ByteColorBuffer*/

        public void Apply
          (
            int AttribLoc,
            boolean Enable
          )
          {
            if (Enable)
              {
                gl.glEnableVertexAttribArray(AttribLoc);
              } /*if*/
            gl.glVertexAttribPointer(AttribLoc, 4, gl.GL_UNSIGNED_BYTE, true, 0, Buf);
          } /*Apply*/
      } /*ByteColorBuffer*/;

    public static class VertIndexBuffer
      /* converts a Collection of vertex indices to a buffer that can be passed to
        glDrawElements. Only the Draw method makes GL calls. */
      {
        private final ShortBuffer Buf;
        private final int Mode;

        public VertIndexBuffer
          (
            Collection<Integer> FromArray,
            int Mode
          )
          {
            this.Mode = Mode;
            final short[] Indices = new short[FromArray.size()];
            int i = 0;
            for (Integer v : FromArray)
              {
                Indices[i++] = (short)(int)v;
              } /*for*/
            Buf =
                ByteBuffer.allocateDirect(Indices.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(Indices);
            Buf.position(0);
          } /*VertIndexBuffer*/

        public void Draw()
          {
            gl.glDrawElements
              (
                /*mode =*/ Mode,
                /*count =*/ Buf.limit(),
                /*type =*/ gl.GL_UNSIGNED_SHORT,
                /*indices =*/ Buf
              );
          } /*Draw*/

      } /*VertIndexBuffer*/;

/*
    Shader programs

    Shader and Program objects can be set up even before the GL context becomes valid,
    and they can be retained after a GL context goes away and reused when another
    one appears. GL calls can be deferred from the constructor to explicit Bind
    methods, and Unbind methods can be used to tell the objects that the GL context
    has gone away.
*/

    public static class Shader
      {
        public final int Type;
        public final String Source;
        private int id;

        public Shader
          (
            int Type,
            String Source,
            boolean BindNow
              /* true to do GL calls now, false to defer to later explicit call to Bind */
          )
          {
            this.Type = Type;
            this.Source = Source;
            this.id = 0;
            if (BindNow)
              {
                Bind();
              } /*if*/
          } /*Shader*/

        public void Bind()
          /* actually allocates GL resources and compiles the shader, if not already done so. */
          {
            if (id == 0)
              {
                ClearError();
                id = gl.glCreateShader(Type);
                if (id == 0)
                  {
                    ThrowError("creating shader");
                  } /*if*/
                gl.glShaderSource(id, Source);
                CheckError("setting shader %d source", id);
                gl.glCompileShader(id);
                CheckError("compiling shader %d source", id);
                int[] Status = new int[1];
                gl.glGetShaderiv(id, gl.GL_COMPILE_STATUS, Status, 0);
                if (Status[0] == gl.GL_FALSE)
                  {
                    System.err.println
                      (
                            "GLUseful failed to compile shader source:\n"
                        +
                            Source
                        +
                            "\n"
                      ); /* debug */
                    throw new RuntimeException
                      (
                            "Error compiling shader: "
                        +
                            GetShaderInfoLog(id)
                      );
                  } /*if*/
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
            if (id > 0)
              {
                if (Release)
                  {
                    gl.glDeleteShader(id);
                  } /*if*/
                id = 0;
              } /*if*/
          } /*Unbind*/

        public int GetID()
          /* only valid if Bind has been called! */
          {
            return
                id;
          } /*GetID*/

      } /*Shader*/;

    public static class Program
      {
        private int id;
        private final Shader VertexShader, FragmentShader;
        private final boolean OwnShaders;

        public Program
          (
            Shader VertexShader,
            Shader FragmentShader,
            boolean OwnShaders, /* call Unbind on shaders on my own Unbind */
            boolean BindNow
          )
          {
            this.VertexShader = VertexShader;
            this.FragmentShader = FragmentShader;
            this.OwnShaders = OwnShaders;
            this.id = 0;
            if (BindNow)
              {
                Bind();
              } /*if*/
          } /*Program*/

        public Program
          (
            String VertexShaderSource,
            String FragmentShaderSource,
            boolean BindNow
          )
          {
            this
              (
                new Shader(gl.GL_VERTEX_SHADER, VertexShaderSource, BindNow),
                new Shader(gl.GL_FRAGMENT_SHADER, FragmentShaderSource, BindNow),
                true,
                BindNow
              );
          } /*Program*/

        public void Bind()
          /* actually allocates GL resources, if not already done so. */ 
          {
            if (id == 0)
              {
                VertexShader.Bind();
                FragmentShader.Bind();
                ClearError();
                id = gl.glCreateProgram();
                if (id == 0)
                  {
                    ThrowError("creating program");
                  } /*if*/
                gl.glAttachShader(id, VertexShader.GetID());
                CheckError("attaching vertex shader to program %d", id);
                gl.glAttachShader(id, FragmentShader.GetID());
                CheckError("attaching fragment shader to program %d", id);
                gl.glLinkProgram(id);
                int[] Status = new int[1];
                gl.glGetProgramiv(id, gl.GL_LINK_STATUS, Status, 0);
                if (Status[0] == gl.GL_FALSE)
                  {
                    throw new RuntimeException
                      (
                            "Error linking program: "
                        +
                            gl.glGetProgramInfoLog(id)
                      );
                  } /*if*/
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
            if (id > 0)
              {
                if (Release)
                  {
                    gl.glDetachShader(id, VertexShader.id);
                    gl.glDetachShader(id, FragmentShader.id);
                    gl.glDeleteProgram(id);
                  } /*if*/
                id = 0;
                if (OwnShaders)
                  {
                    VertexShader.Unbind(Release);
                    FragmentShader.Unbind(Release);
                  } /*if*/
              } /*if*/
          } /*Unbind*/

      /* following calls all require a valid GL context */

        public void Validate()
          {
            Bind();
            gl.glValidateProgram(id);
            int[] Status = new int[1];
            gl.glGetProgramiv(id, gl.GL_VALIDATE_STATUS, Status, 0);
            if (Status[0] == gl.GL_FALSE)
              {
                throw new RuntimeException
                  (
                        "Error validating program: "
                    +
                        gl.glGetProgramInfoLog(id)
                  );
              } /*if*/
          } /*Validate*/

        public int GetUniform
          (
            String Name,
            boolean MustExist
          )
          {
            Bind();
            final int Result = gl.glGetUniformLocation(id, Name);
            if (MustExist && Result < 0)
              {
                throw new RuntimeException("no location for uniform “" + Name + "”");
              } /*if*/
            return Result;
          } /*GetUniform*/

        public int GetAttrib
          (
            String Name,
            boolean MustExist
          )
          {
            Bind();
            final int Result = gl.glGetAttribLocation(id, Name);
            if (MustExist && Result < 0)
              {
                throw new RuntimeException("no location for attribute “" + Name + "”");
              } /*if*/
            return Result;
          } /*GetAttrib*/

        public void Use()
          {
            Bind();
            gl.glUseProgram(id);
          } /*Use*/

        public void Unuse()
          {
            if (id == 0)
              {
                throw new IllegalStateException("GLUseful program not in use");
              } /*if*/
            gl.glUseProgram(0);
          } /*Unuse*/

      } /*Program*/;

/*
    Shader variable management

    Of the calls below, only GetUniformLocs and SetUniformVals require a valid GL context.
*/

    public enum ShaderVarTypes
      {
        FLOAT,
        FLOAT_ARRAY,
        VEC3,
        COLOR3,
        COLOR4,
      } /*ShaderVarTypes*/;

    public static class ShaderVarDef
      /* definition of a user shader variable */
      {
        public final String Name;
        public final ShaderVarTypes Type;
        public final int NrElts; /* for array only */
        public final String ArraySizeName; /* for array only */

        public ShaderVarDef
          (
            String Name,
            ShaderVarTypes Type,
            int NrElts,
            String ArraySizeName /* optional name to be defined as const int equal to NrElts */
          )
          {
            this.Name = Name.intern();
            this.Type = Type;
            this.NrElts = NrElts;
            this.ArraySizeName = ArraySizeName != null ? ArraySizeName.intern() : null;
            if (Type != ShaderVarTypes.FLOAT_ARRAY && NrElts != 1)
              {
                throw new RuntimeException("only FLOAT_ARRAY can have NrElts ≠ 1");
              } /*if*/
            if (Type != ShaderVarTypes.FLOAT_ARRAY && ArraySizeName != null)
              {
                throw new RuntimeException("only FLOAT_ARRAY associated ArraySizeName");
              } /*if*/
          } /*ShaderVarDef*/

        public ShaderVarDef
          (
            String Name,
            ShaderVarTypes Type
          )
          {
            this(Name, Type, 1, null);
          } /*ShaderVarDef*/

      } /*ShaderVarDef*/;

    public static class ShaderVarVal
      /* specification of the value for a user shader variable */
      {
        public final String Name;
        public final Object Value;
          /* Float for FLOAT, array of floats for FLOAT_ARRAY,
            array of 3 floats for VEC3, Color for COLOR3 or COLOR4 */

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

    public static void DefineUniforms
      (
        Appendable Out,
        ShaderVarDef[] Uniforms
      )
      /* writes GLSL to Out defining the specified uniform variables. Doesn't make any GL calls. */
      {
        try
          {
            for (ShaderVarDef VarDef : Uniforms)
              {
                switch (VarDef.Type)
                  {
                case FLOAT_ARRAY:
                    if (VarDef.ArraySizeName != null)
                      {
                        Out.append
                          (
                            String.format
                              (
                                StdLocale,
                                "const int %s = %d;\n",
                                VarDef.ArraySizeName,
                                VarDef.NrElts
                              )
                          );
                      } /*if*/
                break;
                  } /*switch*/
              } /*for*/
            for (ShaderVarDef VarDef : Uniforms)
              {
                Out.append("uniform ");
                switch (VarDef.Type)
                  {
                case FLOAT:
                case FLOAT_ARRAY:
                    Out.append("float");
                break;
                case VEC3:
                case COLOR3:
                    Out.append("vec3");
                break;
                case COLOR4:
                    Out.append("vec4");
                break;
                  } /*switch*/
                Out.append(" ");
                Out.append(VarDef.Name);
                if (VarDef.Type == ShaderVarTypes.FLOAT_ARRAY)
                  {
                    Out.append
                      (
                        VarDef.ArraySizeName != null ?
                            "[" + VarDef.ArraySizeName + "]"
                        :
                            String.format(StdLocale, "[%d]", VarDef.NrElts)
                      );
                  } /*if*/
                Out.append(";\n");
              } /*for*/
          }
        catch (java.io.IOException Fail)
          {
            throw new RuntimeException(Fail.toString());
          } /*try*/
      } /*DefineUniforms*/

    public static class UniformLocInfo
      /* pre-fetching uniform variable locations--does this matter? */
      {
        public final ShaderVarTypes Type;
        public final int Loc;

        public UniformLocInfo
          (
            ShaderVarTypes Type,
            int Loc
          )
          {
            this.Type = Type;
            this.Loc = Loc;
          } /*UniformLocInfo*/

      } /*UniformLocInfo*/;

    public static java.util.Map<String, UniformLocInfo> GetUniformLocs
      (
        ShaderVarDef[] Uniforms,
        Program ForProgram
      )
      /* returns a mapping of uniform names to locations in the specified program. */
      {
        final java.util.HashMap<String, UniformLocInfo> Result =
            new java.util.HashMap<String, UniformLocInfo>();
        for (ShaderVarDef VarDef : Uniforms)
          {
            Result.put
              (
                VarDef.Name,
                new UniformLocInfo(VarDef.Type, ForProgram.GetUniform(VarDef.Name, false))
              );
          } /*for*/
        return
            Result;
      } /*GetUniformLocs*/

    public static void SetUniformVals
      (
        ShaderVarVal[] Uniforms,
        java.util.Map<String, UniformLocInfo> UniformLocs /* as previously returned from GetUniformLocs */
      )
      /* makes glUniformxx calls defining the specified values for the uniforms of
        the current shader program. */
      {
        for (ShaderVarVal VarRef : Uniforms)
          {
            final UniformLocInfo VarInfo = UniformLocs.get(VarRef.Name);
            if (VarInfo == null)
              {
                throw new RuntimeException("no such uniform variable “" + VarRef.Name + "”");
              } /*if*/
            switch (VarInfo.Type)
              {
            case FLOAT:
                gl.glUniform1f(VarInfo.Loc, (Float)VarRef.Value);
            break;
            case FLOAT_ARRAY:
                  {
                    final float[] Value = (float[])VarRef.Value;
                    gl.glUniform1fv(VarInfo.Loc, Value.length, Value, 0);
                  }
            break;
            case VEC3:
                  {
                    final float[] Value = (float[])VarRef.Value;
                    gl.glUniform3f(VarInfo.Loc, Value[0], Value[1], Value[2]);
                  }
            break;
            case COLOR3:
                  {
                    final Color TheColor = (Color)VarRef.Value;
                    gl.glUniform3f(VarInfo.Loc, TheColor.r, TheColor.g, TheColor.b);
                  }
            break;
            case COLOR4:
                  {
                    final Color TheColor = (Color)VarRef.Value;
                    gl.glUniform4f(VarInfo.Loc, TheColor.r, TheColor.g, TheColor.b, TheColor.a);
                  }
            break;
              } /*switch*/
          } /*for*/
      } /*SetUniformVals*/

  } /*GLUseful*/;
