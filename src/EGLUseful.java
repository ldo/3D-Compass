package nz.gen.geek_central.Compass3D;
/*
    Useful EGL-related definitions.

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

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL11;

public class EGLUseful
  {
    public static final EGL10 EGL = GetEGL(); /* use this EGL10 instance for making EGL calls */

    public static class EGLException extends RuntimeException
      /* indicates a problem making an EGL call. */
      {

        public EGLException
          (
            String Message
          )
          {
            super(Message);
          } /*EGLException*/

      } /*EGLException*/

    public static EGL10 GetEGL()
      /* you probably don't need to call this. */
      {
        return
            (EGL10)EGLContext.getEGL();
      } /*GetEGL*/

    public static void Fail
      (
        String What
      )
      {
        throw new EGLException
          (
            String.format("EGL %s failed with error %d", What, EGL.eglGetError())
          );
      } /*Fail*/

    public static EGLDisplay NewDisplay()
      /* allocates and initializes a new display. */
      {
        final EGLDisplay Result = EGL.eglGetDisplay((EGLDisplay)EGL10.EGL_DEFAULT_DISPLAY);
          /* as far as I can tell, it makes no difference whether you pass
            EGL_DEFAULT_DISPLAY or EGL_NO_DISPLAY */
        if (Result == null)
          {
            throw new EGLException("failed to allocate a new display");
          } /*if*/
        if (!EGL.eglInitialize(Result, null))
          {
            Fail("initing new display");
          } /*if*/
        return
            Result;
      } /*NewDisplay*/

    public static EGLConfig[] GetConfigs
      (
        EGLDisplay ForDisplay
      )
      /* returns all available configs for the specified display. */
      {
        EGLConfig[] Configs = null;
        final int[] ConfigsNr = new int[1];
        for (;;)
          {
            final boolean Success = EGL.eglGetConfigs
              (
                /*display =*/ ForDisplay,
                /*configs =*/ Configs,
                /*config_size =*/ Configs != null ? Configs.length : 0,
                /*num_config =*/ ConfigsNr
              );
            if (!Success)
              {
                Fail("GetConfigs");
                break;
              } /*if*/
            if (Configs != null)
                break;
            Configs = new EGLConfig[ConfigsNr[0]];
          } /*for*/
        return
            Configs;
      } /*GetConfigs*/

    public static EGLConfig[] GetCompatConfigs
      (
        EGLDisplay ForDisplay,
        int[] MatchingAttribs
      )
      /* returns configs compatible with the specified attributes. */
      {
        EGLConfig[] Configs = null;
        final int[] ConfigsNr = new int[1];
        for (;;)
          {
            final boolean Success = EGL.eglChooseConfig
              (
                /*display =*/ ForDisplay,
                /*attrib =*/ MatchingAttribs,
                /*configs =*/ Configs,
                /*config_size =*/ Configs != null ? Configs.length : 0,
                /*num_config =*/ ConfigsNr
              );
            if (!Success)
              {
                Fail("GetCompatConfigs");
                break;
              } /*if*/
            if (Configs != null)
                break;
            Configs = new EGLConfig[ConfigsNr[0]];
          } /*for*/
        return
            Configs;
      } /*GetCompatConfigs*/

    public static int GetConfigAttrib
      (
        EGLDisplay ForDisplay,
        EGLConfig ForConfig,
        int Attrib
      )
      /* returns the value of the specified attribute of a config. */
      {
        final int[] AttrValue = new int[1];
        final boolean Success = EGL.eglGetConfigAttrib
          (
            /*dpy =*/ ForDisplay,
            /*config =*/ ForConfig,
            /*attribute =*/ Attrib,
            /*value =*/ AttrValue
          );
        if (!Success)
          {
            Fail(String.format("GetConfigAttrib %d", Attrib));
          } /*if*/
        return
            AttrValue[0];
      } /*GetConfigAttrib*/

    public static EGLSurface CreatePbufferSurface
      (
        EGLDisplay ForDisplay,
        EGLConfig WithConfig,
        int Width,
        int Height,
        boolean ExactSize /* false to allow allocating smaller Width/Height */
      )
      /* might return EGL_NO_SURFACE on failure, caller must check for error */
      {
        final int[] Attribs = new int[3 * 2 + 1];
          {
            int i = 0;
            Attribs[i++] = EGL10.EGL_WIDTH;
            Attribs[i++] = Width;
            Attribs[i++] = EGL10.EGL_HEIGHT;
            Attribs[i++] = Height;
            Attribs[i++] = EGL.EGL_LARGEST_PBUFFER;
            Attribs[i++] = ExactSize ? 0 : 1;
            Attribs[i++] = EGL10.EGL_NONE; /* marks end of list */
          }
        return
            EGL.eglCreatePbufferSurface
              (
                /*display =*/ ForDisplay,
                /*config =*/ WithConfig,
                /*attrib_list =*/ Attribs
              );
      } /*CreatePbufferSurface*/

    public static class SurfaceContext
      /* easy management of surface together with context */
      {
        public final EGLDisplay Display;
        public final EGLContext Context;
        public final EGLSurface Surface;

        public SurfaceContext
          (
            EGLDisplay Display,
            EGLContext Context,
            EGLSurface Surface
          )
          {
            this.Display = Display;
            this.Context = Context;
            this.Surface = Surface;
          } /*SurfaceContext*/

        public static SurfaceContext CreatePbuffer
          (
            EGLDisplay ForDisplay,
            EGLConfig[] TryConfigs, /* to be tried in turn */
            int Width,
            int Height,
            boolean ExactSize, /* false to allow allocating smaller Width/Height */
            EGLContext ShareContext /* pass null or EGL_NO_CONTEXT to not share existing context */
          )
          /* creates a Pbuffer surface and Context using one of the available
            configs. */
          {
            EGLSurface TheSurface = null;
            EGLContext UseContext = null;
            for (int i = 0;;)
              {
                if (i == TryConfigs.length)
                  {
                    Fail("creating Pbuffer surface");
                  } /*if*/
                TheSurface = CreatePbufferSurface
                  (
                    /*ForDisplay =*/ ForDisplay,
                    /*WithConfig =*/ TryConfigs[i],
                    /*Width =*/ Width,
                    /*Height =*/ Height,
                    /*ExactSize =*/ ExactSize
                  );
                if (TheSurface != EGL10.EGL_NO_SURFACE)
                  {
                    UseContext = EGL.eglCreateContext
                      (
                        /*display =*/ ForDisplay,
                        /*config =*/ TryConfigs[i],
                        /*share_context =*/ ShareContext == null ? EGL10.EGL_NO_CONTEXT : ShareContext,
                        /*attrib_list =*/ null
                      );
                    if (UseContext == EGL10.EGL_NO_CONTEXT)
                      {
                        Fail("creating context");
                      } /*if*/
                    break;
                  } /*if*/
                ++i;
              } /*for*/
            return
                new SurfaceContext(ForDisplay, UseContext, TheSurface);
          } /*CreatePbuffer*/

        public void SetCurrent()
          /* sets surface and context as current. */
          {
            if (!EGL.eglMakeCurrent(Display, Surface, Surface, Context))
              {
                Fail("setting current context");
              } /*if*/
          } /*SetCurrent*/

        public void ClearCurrent()
          /* clears current surface and context. */
          {
            EGL.eglMakeCurrent
              (
                Display,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT
              );
          } /*ClearCurrent*/

        public void Release()
          /* disposes of Surface and Context, but not Display. */
          {
            ClearCurrent(); /* if not already done */
            if (!EGL.eglDestroySurface(Display, Surface))
              {
                Fail("destroying surface");
              } /*if*/;
            if (!EGL.eglDestroyContext(Display, Context))
              {
                Fail("destroying context");
              } /*if*/;
          } /*Release*/

        public GL11 GetGL()
          {
            return
                (GL11)Context.getGL();
          } /*GetGL*/

      } /*SurfaceContext*/

  } /*EGLUseful*/
