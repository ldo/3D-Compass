package nz.gen.geek_central.Compass3D;

import javax.microedition.khronos.opengles.GL10;

public class MainView extends android.opengl.GLSurfaceView
  {
    public interface Handler
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

    private class MainViewRenderer implements Renderer
      {

        public void onDrawFrame
          (
            GL10 _gl
          )
          {
            if (HandleView != null)
              {
                HandleView.Draw();
              } /*if*/
          } /*onDrawFrame*/

        public void onSurfaceChanged
          (
            GL10 _gl,
            int ViewWidth,
            int ViewHeight
          )
          {
            if (HandleView != null)
              {
                HandleView.Setup(ViewWidth, ViewHeight);
              } /*if*/
          } /*onSurfaceChanged*/

        public void onSurfaceCreated
          (
            GL10 _gl,
            javax.microedition.khronos.egl.EGLConfig Config
          )
          {
          /* leave all actual work to onSurfaceChanged */
          } /*onSurfaceCreated*/

      } /*MainViewRenderer*/;

    final MainViewRenderer Render = new MainViewRenderer();

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
