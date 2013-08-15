package nz.gen.geek_central.GLUseful;
/*
    Replacement subclass for GLSurfaceView.Renderer which gets rid of useless
    GL10 arg and provides a Synchronize method to allow UI thread to
    synchronize with GL thread.

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
import javax.microedition.khronos.egl.EGLConfig;
import android.opengl.GLSurfaceView;

public abstract class BaseRenderer implements GLSurfaceView.Renderer
  {
    public final GLSurfaceView Parent;

    public BaseRenderer
      (
        GLSurfaceView Parent
      )
      {
        super();
        this.Parent = Parent;
      } /*BaseRenderer*/

    public void Synchronize
      (
        final Runnable Task
      )
      /* runs Task on the renderer thread and waits for it to complete. */
      {
        final Object Sync = new Object();
        synchronized (Sync)
          {
            Parent.queueEvent
              (
                new Runnable()
                  {
                    public void run()
                      {
                        Task.run();
                        synchronized (Sync)
                          {
                            Sync.notify();
                          } /*synchronized*/
                      } /*run*/
                  } /*Runnable*/
              );
            for (;;)
              {
                try
                  {
                    Sync.wait();
                    break;
                  }
                catch (InterruptedException HoHum)
                  {
                  /* keep waiting */
                  } /*try*/
              } /*for*/
          } /*synchronized*/
      } /*Synchronize*/

    public interface RunnableFunc<ResultType>
      {
        public ResultType Run();
      } /*RunnableFunc*/;

    public <ResultType> ResultType Synchronize
      (
        final RunnableFunc<ResultType> Task
      )
      /* runs Task on the renderer thread, waits for it to complete,
        and returns its Run function result. */
      {
        class Container
          {
            public ResultType Contents;
          } /*Container*/;
        final Container Result = new Container();
        Synchronize
          (
            new Runnable()
              {
                public void run()
                  {
                    Result.Contents = Task.Run();
                  } /*run*/
              } /*Runnable*/
          );
        return
            Result.Contents;
      } /*Synchronize*/

    public abstract void OnDrawFrame();
      /* replacement for onDrawFrame. */

    public abstract void OnSurfaceChanged
      (
        int ViewWidth,
        int ViewHeight
      );
      /* replacement for onSurfaceChanged. */

    public abstract void OnSurfaceCreated
      (
        EGLConfig Config
      );
      /* replacement for onSurfaceCreated. */

    public void onDrawFrame
      (
        GL10 _gl
      )
      {
        OnDrawFrame();
      } /*onDrawFrame*/

    public void onSurfaceChanged
      (
        GL10 _gl,
        int ViewWidth,
        int ViewHeight
      )
      {
        OnSurfaceChanged(ViewWidth, ViewHeight);
      } /*onSurfaceChanged*/

    public void onSurfaceCreated
      (
        GL10 _gl,
        EGLConfig Config
      )
      {
        OnSurfaceCreated(Config);
      } /*onSurfaceCreated*/

  } /*BaseRenderer*/;
