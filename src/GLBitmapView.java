package nz.gen.geek_central.GLUseful;
/*
    Display of a Canvas-rendered image within an OpenGL view.

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

import android.graphics.Bitmap;
import static nz.gen.geek_central.GLUseful.GLUseful.gl;

public class GLBitmapView extends GLTextureView
  {

    public final int BitsWidth, BitsHeight;

    public android.graphics.Canvas Draw;
      /* do your drawing into here before calling the Draw method to push it to the GL display */
    public Bitmap Bits;
    private boolean SendBits;

    public GLBitmapView
      (
        int BitsWidth, /* dimensions of the Bitmap to create */
        int BitsHeight,
        String CustomFragShading,
          /* optional replacement for fragment shader calculation,
            defaults to "gl_FragColor = texture2D(view_image, image_coord);"
            if omitted */
        boolean BindNow
          /* true to do GL calls now, false to defer to later call to Bind or Draw */
      )
      {
        super(CustomFragShading, true, false, false);
        this.BitsWidth = BitsWidth;
        this.BitsHeight = BitsHeight;
        if (BindNow)
          {
            Bind();
          } /*if*/
      } /*GLBitmapView*/

    public GLBitmapView
      (
        int BitsWidth,
        int BitsHeight,
        boolean BindNow
          /* true to do GL calls now, false to defer to later call to Bind or Draw */
      )
      {
        this(BitsWidth, BitsHeight, null, BindNow);
      } /*GLBitmapView*/

    @Override
    protected void OnBind()
      {
        Bits = Bitmap.createBitmap
          (
            /*width =*/ BitsWidth,
            /*height =*/ BitsHeight,
            /*config =*/ Bitmap.Config.ARGB_8888
          );
        Draw = new android.graphics.Canvas(Bits);
        SendBits = true;
      } /*OnBind*/

    @Override
    protected void OnUnbind
      (
        boolean Release
          /* true iff GL context still valid, so explicitly free up allocated resources.
            false means GL context has gone (or is going), so simply forget allocated
            GL resources without making any GL calls. */
      )
      /* frees up additional resources associated with this object. */
      {
        Bits.recycle();
        Bits = null;
      } /*OnUnbind*/

    public void DrawChanged()
      /* call this to indicate that the bitmap has been changed and
        must be re-sent to the texture object. */
      {
        Bits.prepareToDraw();
        SendBits = true;
      } /*DrawChanged*/

    @Override
    protected void OnDefineTexture()
      {
        super.OnDefineTexture();
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
      } /*OnDefineTexture*/

  } /*GLBitmapView*/;
