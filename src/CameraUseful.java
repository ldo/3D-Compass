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

import android.hardware.Camera;

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
                ((Width + 1) / 2) * ((Height + 1) / 2) * 2;
      } /*NV21DataSize*/

    public static void DecodeNV21
      (
        int SrcWidth, /* dimensions of image before rotation */
        int SrcHeight,
        byte[] Data, /* length = NV21DataSize(SrcWidth, SrcHeight) */
        int Rotate, /* [0 .. 3], angle is 90° * Rotate clockwise */
        int Alpha, /* set as alpha for all decoded pixels */
        int[] Pixels /* length = Width * Height */
      )
      /* decodes NV21-encoded image data, which is the default camera preview image format. */
      {
        final int AlphaMask = Alpha << 24;
      /* Rotation involves accessing either the source or destination pixels in a
        non-sequential fashion. Since the source is smaller, I figure it's less
        cache-unfriendly to go jumping around that. */
        final int DstWidth = (Rotate & 1) != 0 ? SrcHeight : SrcWidth;
        final int DstHeight = (Rotate & 1) != 0 ? SrcWidth : SrcHeight;
        final boolean DecrementRow = Rotate > 1;
        final boolean DecrementCol = Rotate == 1 || Rotate == 2;
        final int LumaRowStride = (Rotate & 1) != 0 ? 1 : SrcWidth;
        final int LumaColStride = (Rotate & 1) != 0 ? SrcWidth : 1;
        final int ChromaRowStride = (Rotate & 1) != 0 ? 2 : SrcWidth;
        final int ChromaColStride = (Rotate & 1) != 0 ? SrcWidth : 2;
        int dst = 0;
        for (int row = DecrementRow ? DstHeight : 0;;)
          {
            if (row == (DecrementRow ? 0 : DstHeight))
                break;
            if (DecrementRow)
              {
                --row;
              } /*if*/
            for (int col = DecrementCol ? DstWidth : 0;;)
              {
                if (col == (DecrementCol ? 0 : DstWidth))
                    break;
                if (DecrementCol)
                  {
                    --col;
                  } /*if*/
                final int Y = 0xff & (int)Data[row * LumaRowStride + col * LumaColStride]; /* [0 .. 255] */
              /* U/V data follows entire luminance block, downsampled to half luminance
                resolution both horizontally and vertically */
              /* decoding follows algorithm shown at
                <http://www.mail-archive.com/android-developers@googlegroups.com/msg14558.html>,
                except it gets red and blue the wrong way round (decoding NV12 rather than NV21) */
              /* see also good overview of YUV-family formats at <http://wiki.videolan.org/YUV> */
                final int Cr =
                    (0xff & (int)Data[SrcHeight * SrcWidth + row / 2 * ChromaRowStride + col / 2 * ChromaColStride]) - 128;
                      /* [-128 .. +127] */
                final int Cb =
                    (0xff & (int)Data[SrcHeight * SrcWidth + row / 2 * ChromaRowStride + col / 2 * ChromaColStride + 1]) - 128;
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
                if (!DecrementCol)
                  {
                    ++col;
                  } /*if*/
              } /*for*/
            if (!DecrementRow)
              {
                ++row;
              } /*if*/
          } /*for*/
      } /*DecodeNV21*/

    public static android.graphics.Point GetSmallestPreviewSizeAtLeast
      (
        Camera TheCamera,
        int MinWidth,
        int MinHeight
      )
      /* returns smallest supported preview size which is of at least
        the given dimensions. */
      {
        Camera.Size BestSize = null;
        for
          (
            Camera.Size ThisSize : TheCamera.getParameters().getSupportedPreviewSizes()
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

    public static android.graphics.Point GetLargestPreviewSizeAtMost
      (
        Camera TheCamera,
        int MaxWidth,
        int MaxHeight
      )
      /* returns largest supported preview size which is of at most
        the given dimensions. */
      {
        Camera.Size BestSize = null;
        for
          (
            Camera.Size ThisSize : TheCamera.getParameters().getSupportedPreviewSizes()
          )
          {
            if (ThisSize.width <= MaxWidth && ThisSize.height <= MaxHeight)
              {
                if
                  (
                        BestSize == null
                    ||
                        BestSize.width < ThisSize.width
                    ||
                        BestSize.height < ThisSize.height
                  )
                  {
                    BestSize = ThisSize;
                  } /*if*/
              } /*if*/
          } /*for*/
        if (BestSize == null)
          {
          /* none big enough, pick last, which seems to be smallest */
            final java.util.List<Camera.Size> PreviewSizes =
                TheCamera.getParameters().getSupportedPreviewSizes();
            BestSize = PreviewSizes.get(PreviewSizes.size() - 1);
          } /*if*/
        return
            new android.graphics.Point(BestSize.width, BestSize.height);
      } /*GetLargestPreviewSizeAtMost*/

  } /*CameraUseful*/
