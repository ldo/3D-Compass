package nz.gen.geek_central.Compass3D;
/*
    Useful camera-related stuff.

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

public class CameraUseful
  {

    public static int NV21DataSize
      (
        int Width,
        int Height
      )
      /* returns the size of a data buffer to hold an NV21-encoded image
        of the specified dimensions. */
      {
        return
                Width * Height
            +
                ((Width + 1) / 2) * ((Height + 1) / 2);
      } /*NV21DataSize*/

    public static void DecodeNV21
      (
        int Width,
        int Height,
        byte[] Data, /* length = NV21DataSize(Width, Height) */
        int Alpha,
        int[] Pixels /* length = Width * Height */
      )
      /* decodes NV21-encoded image data, which is the default camera preview image format. */
      {
        final int AlphaMask = Alpha << 24;
        int dst = 0;
        for (int row = 0; row < Height; ++row)
          {
            for (int col = 0; col < Width; ++col)
              {
                final int Y = 0xff & (int)Data[row * Width + col]; /* [0 .. 255] */
              /* U/V data follows entire luminance block, downsampled to half luminance
                resolution both horizontally and vertically */
              /* decoding follows algorithm shown at
                <http://www.mail-archive.com/android-developers@googlegroups.com/msg14558.html>,
                except it gets red and blue the wrong way round */
                final int Cr =
                    (0xff & (int)Data[Height * Width + row / 2 * Width + col / 2 * 2]) - 128;
                      /* [-128 .. +127] */
                final int Cb =
                    (0xff & (int)Data[Height * Width + row / 2 * Width + col / 2 * 2 + 1]) - 128;
                      /* [-128 .. +127] */
                Pixels[dst++] =
                        AlphaMask
                    |
                            Math.max
                              (
                                Math.min
                                  (
                                    (int)(
                                            Y
                                        +
                                            Cr
                                        +
                                            (Cr >> 1)
                                        +
                                            (Cr >> 2)
                                        +
                                            (Cr >> 6)
                                    ),
                                    255
                                  ),
                                  0
                              )
                        <<
                            16 /* red */
                    |
                            Math.max
                              (
                                Math.min
                                  (
                                    (int)(
                                            Y
                                        -
                                            (Cr >> 2)
                                        +
                                            (Cr >> 4)
                                        +
                                            (Cr >> 5)
                                        -
                                            (Cb >> 1)
                                        +
                                            (Cb >> 3)
                                        +
                                            (Cb >> 4)
                                        +
                                            (Cb >> 5)
                                    ),
                                    255
                                  ),
                                0
                              )
                        <<
                            8 /* green */
                    |
                        Math.max
                          (
                            Math.min
                              (
                                (int)(
                                        Y
                                    +
                                        Cb
                                    +
                                        (Cb >> 2)
                                    +
                                        (Cb >> 3)
                                    +
                                        (Cb >> 5)
                                ),
                                255
                              ),
                            0
                          ); /* blue */
              } /*for*/
          } /*for*/
      } /*DecodeNV21*/

    public static android.graphics.Point GetSmallestPreviewSizeAtLeast
      (
        android.hardware.Camera TheCamera,
        int MinWidth,
        int MinHeight
      )
      /* returns smallest supported preview size which is of at least
        the given dimensions. */
      {
        android.hardware.Camera.Size BestSize = null;
        for
          (
            android.hardware.Camera.Size ThisSize :
                TheCamera.getParameters().getSupportedPreviewSizes()
          )
          {
            if (ThisSize.width >= MinWidth && ThisSize.height >= MinHeight)
              {
                if
                  (
                        BestSize == null
                    ||
                        BestSize.width > ThisSize.width
                    ||
                        BestSize.height > ThisSize.height
                  )
                  {
                    BestSize = ThisSize;
                  } /*if*/
              } /*if*/
          } /*for*/
        if (BestSize == null)
          {
          /* none big enough, pick first, which seems to be biggest */
            BestSize = TheCamera.getParameters().getSupportedPreviewSizes().get(0);
          } /*if*/
        return
            new android.graphics.Point(BestSize.width, BestSize.height);
      } /*GetSmallestPreviewSizeAtLeast*/

  } /*CameraUseful*/
