/*
    Useful OpenGL stuff. This provides an alternative to the broken
    android.opengl.GLES20.glGetShaderInfoLog routine on older Android
    versions.

    Copyright 2012 by Lawrence D'Oliveiro <ldo@geek-central.gen.nz>.

    Licensed under the Apache License, Version 2.0 (the "License"); you
    may not use this file except in compliance with the License. You may
    obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
    implied. See the License for the specific language governing
    permissions and limitations under the License.
*/

#include <stdbool.h>
#include <stdint.h>
#include <stdlib.h>
#include <jni.h>
#include "JNIGlue.h"
#include <android/log.h> /* debug */
#include <GLES2/gl2.h>

static jstring get_shader_info_log
  (
    JNIEnv * env,
    jobject this,
    jint shader_id
  )
  /* needed because android.opengl.GLES20.glGetShaderInfoLog doesn't return anything,
    at least on Android 2.2. The bug appears to be that glGetShaderiv of GL_INFO_LOG_LENGTH
    returns 0, according to this bug report
    <http://code.google.com/p/android/issues/detail?id=9953>, so I must avoid using that. */
  {
    const size_t msgmax = 4096; /* should be plenty big enough */
    char * const msg = calloc(1, msgmax);
    glGetShaderInfoLog(shader_id, msgmax, 0, msg);
    const jstring result = JNNewStringUTF(env, msg);
    free(msg);
    return
        result;
  } /*get_shader_info_log*/

jint JNI_OnLoad
  (
    JavaVM * vm,
    void * reserved
  )
  {
    JNIEnv * env;
    int result = JNI_ERR;
    JNINativeMethod methods[] =
        {
            {
                .name = "GetShaderInfoLog",
                .signature = "(I)Ljava/lang/String;",
                .fnPtr = get_shader_info_log,
            },
        };
    do /*once*/
      {
        if ((**vm).GetEnv(vm, (void **)&env, JNI_VERSION_1_6) != JNI_OK)
            break;
        if
          (
                JNRegisterNatives
                  (
                    env,
                    JNFindClass(env, "nz/gen/geek_central/GLUseful/GLUseful"),
                    methods,
                    sizeof methods / sizeof(JNINativeMethod)
                  )
            !=
                0
          )
            break;
      /* all done */
        result = JNI_VERSION_1_6;
      }
    while (false);
    return
        result;
  } /*JNI_OnLoad*/
