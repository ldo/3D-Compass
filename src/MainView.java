package nz.gen.geek_central.Compass3D;
/*
    Generic management of an OpenGL display that is only updated on demand.
    Caller provides an instance of the Handler interface (see below) to do
    all the necessary OpenGL setup and drawing. (Re)drawing is triggered in
    the standard way, by calling GLSurfaceView.requestRender() on this View.

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

import javax.microedition.khronos.opengles.GL10;

public class MainView extends android.opengl.GLSurfaceView
  {
    public interface Handler
      /* caller must provide an instance of this to do all the work */
      {

        public void Setup
          (
            int ViewWidth,
            int ViewHeight
          );
          /* called when surface becomes available or its configuration changes,
            to do any necessary GL context setup that doesn't need to be done
            on every frame. */

        public void Draw();
          /* called to (re)draw the frame into the current GL context. */

        public void Release();
          /* called when the GL context is being lost. */

      } /*Handler*/;

    private Handler HandleView;

    public void SetHandler
      (
        Handler NewHandler
      )
      {
        HandleView = NewHandler;
      } /*SetHandler*/

    public class MainViewRenderer extends nz.gen.geek_central.GLUseful.BaseRenderer
      {

        public MainViewRenderer()
          {
            super(MainView.this);
          } /*MainViewRenderer*/

        public void OnDrawFrame()
          {
            if (HandleView != null)
              {
                HandleView.Draw();
              } /*if*/
          } /*OnDrawFrame*/

        public void OnSurfaceChanged
          (
            int ViewWidth,
            int ViewHeight
          )
          {
            if (HandleView != null)
              {
                HandleView.Setup(ViewWidth, ViewHeight);
              } /*if*/
          } /*OnSurfaceChanged*/

        public void OnSurfaceCreated
          (
            javax.microedition.khronos.egl.EGLConfig Config
          )
          {
          /* leave all actual work to onSurfaceChanged */
          } /*OnSurfaceCreated*/

      } /*MainViewRenderer*/;

    public final MainViewRenderer Render = new MainViewRenderer();

    public MainView
      (
        android.content.Context TheContext,
        android.util.AttributeSet TheAttributes
      )
      {
        super(TheContext, TheAttributes);
        setEGLContextClientVersion(2);
        setRenderer(Render);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
      } /*MainView*/

    @Override
    public void onPause()
      {
        super.onPause();
        if (HandleView != null)
          {
            HandleView.Release();
          } /*if*/
      } /*onPause*/

  } /*MainView*/;
