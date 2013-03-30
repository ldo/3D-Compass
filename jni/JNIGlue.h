static inline jint JNGetVersion(JNIEnv * env)
  {
    return
        (**env).GetVersion(env);
  } /*GetVersion*/

static inline jclass JNDefineClass(JNIEnv* env, const char* a1, jobject a2, const jbyte* a3, jsize a4)
  {
    return
        (**env).DefineClass(env, a1, a2, a3, a4);
  } /*DefineClass*/

static inline jclass JNFindClass(JNIEnv* env, const char* a1)
  {
    return
        (**env).FindClass(env, a1);
  } /*FindClass*/

static inline jmethodID JNFromReflectedMethod(JNIEnv* env, jobject a1)
  {
    return
        (**env).FromReflectedMethod(env, a1);
  } /*FromReflectedMethod*/

static inline jfieldID JNFromReflectedField(JNIEnv* env, jobject a1)
  {
    return
        (**env).FromReflectedField(env, a1);
  } /*FromReflectedField*/

static inline jobject JNToReflectedMethod(JNIEnv* env, jclass a1, jmethodID a2, jboolean a3)
  {
    return
        (**env).ToReflectedMethod(env, a1, a2, a3);
  } /*ToReflectedMethod*/

static inline jclass JNGetSuperclass(JNIEnv* env, jclass a1)
  {
    return
        (**env).GetSuperclass(env, a1);
  } /*GetSuperclass*/

static inline jboolean JNIsAssignableFrom(JNIEnv* env, jclass a1, jclass a2)
  {
    return
        (**env).IsAssignableFrom(env, a1, a2);
  } /*IsAssignableFrom*/

static inline jobject JNToReflectedField(JNIEnv* env, jclass a1, jfieldID a2, jboolean a3)
  {
    return
        (**env).ToReflectedField(env, a1, a2, a3);
  } /*ToReflectedField*/

static inline jint JNThrow(JNIEnv* env, jthrowable a1)
  {
    return
        (**env).Throw(env, a1);
  } /*Throw*/

static inline jint JNThrowNew(JNIEnv * env, jclass a1, const char * a2)
  {
    return
        (**env).ThrowNew(env, a1, a2);
  } /*ThrowNew*/

static inline jthrowable JNExceptionOccurred(JNIEnv* env)
  {
    return
        (**env).ExceptionOccurred(env);
  } /*ExceptionOccurred*/

static inline void JNExceptionDescribe(JNIEnv* env)
  {
    (**env).ExceptionDescribe(env);
  } /*ExceptionDescribe*/

static inline void JNExceptionClear(JNIEnv* env)
  {
    (**env).ExceptionClear(env);
  } /*ExceptionClear*/

static inline void JNFatalError(JNIEnv* env, const char* a1)
  {
    (**env).FatalError(env, a1);
  } /*FatalError*/

static inline jint JNPushLocalFrame(JNIEnv* env, jint a1)
  {
    return
        (**env).PushLocalFrame(env, a1);
  } /*PushLocalFrame*/

static inline jobject JNPopLocalFrame(JNIEnv* env, jobject a1)
  {
    return
        (**env).PopLocalFrame(env, a1);
  } /*PopLocalFrame*/

static inline jobject JNNewGlobalRef(JNIEnv* env, jobject a1)
  {
    return
        (**env).NewGlobalRef(env, a1);
  } /*NewGlobalRef*/

static inline void JNDeleteGlobalRef(JNIEnv* env, jobject a1)
  {
    (**env).DeleteGlobalRef(env, a1);
  } /*DeleteGlobalRef*/

static inline void JNDeleteLocalRef(JNIEnv* env, jobject a1)
  {
    (**env).DeleteLocalRef(env, a1);
  } /*DeleteLocalRef*/

static inline jboolean JNIsSameObject(JNIEnv* env, jobject a1, jobject a2)
  {
    return
        (**env).IsSameObject(env, a1, a2);
  } /*IsSameObject*/

static inline jobject JNNewLocalRef(JNIEnv* env, jobject a1)
  {
    return
        (**env).NewLocalRef(env, a1);
  } /*NewLocalRef*/

static inline jint JNEnsureLocalCapacity(JNIEnv* env, jint a1)
  {
    return
        (**env).EnsureLocalCapacity(env, a1);
  } /*EnsureLocalCapacity*/

static inline jobject JNAllocObject(JNIEnv* env, jclass a1)
  {
    return
        (**env).AllocObject(env, a1);
  } /*AllocObject*/

static inline jobject JNNewObject(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jobject const result = (**env).NewObjectV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*NewObject*/

static inline jobject JNNewObjectV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).NewObjectV(env, a1, a2, a3);
  } /*NewObjectV*/

static inline jobject JNNewObjectA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).NewObjectA(env, a1, a2, a3);
  } /*NewObjectA*/

static inline jclass JNGetObjectClass(JNIEnv* env, jobject a1)
  {
    return
        (**env).GetObjectClass(env, a1);
  } /*GetObjectClass*/

static inline jboolean JNIsInstanceOf(JNIEnv* env, jobject a1, jclass a2)
  {
    return
        (**env).IsInstanceOf(env, a1, a2);
  } /*IsInstanceOf*/

static inline jmethodID JNGetMethodID(JNIEnv* env, jclass a1, const char* a2, const char* a3)
  {
    return
        (**env).GetMethodID(env, a1, a2, a3);
  } /*GetMethodID*/

static inline jobject JNCallObjectMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jobject const result = (**env).CallObjectMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallObjectMethod*/

static inline jobject JNCallObjectMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallObjectMethodV(env, a1, a2, a3);
  } /*CallObjectMethodV*/

static inline jobject JNCallObjectMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallObjectMethodA(env, a1, a2, a3);
  } /*CallObjectMethodA*/

static inline jboolean JNCallBooleanMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jboolean const result = (**env).CallBooleanMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallBooleanMethod*/

static inline jboolean JNCallBooleanMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallBooleanMethodV(env, a1, a2, a3);
  } /*CallBooleanMethodV*/

static inline jboolean JNCallBooleanMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallBooleanMethodA(env, a1, a2, a3);
  } /*CallBooleanMethodA*/

static inline jbyte JNCallByteMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jbyte const result = (**env).CallByteMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallByteMethod*/

static inline jbyte JNCallByteMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallByteMethodV(env, a1, a2, a3);
  } /*CallByteMethodV*/

static inline jbyte JNCallByteMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallByteMethodA(env, a1, a2, a3);
  } /*CallByteMethodA*/

static inline jchar JNCallCharMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jchar const result = (**env).CallCharMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallCharMethod*/

static inline jchar JNCallCharMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallCharMethodV(env, a1, a2, a3);
  } /*CallCharMethodV*/

static inline jchar JNCallCharMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallCharMethodA(env, a1, a2, a3);
  } /*CallCharMethodA*/

static inline jshort JNCallShortMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jshort const result = (**env).CallShortMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallShortMethod*/

static inline jshort JNCallShortMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallShortMethodV(env, a1, a2, a3);
  } /*CallShortMethodV*/

static inline jshort JNCallShortMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallShortMethodA(env, a1, a2, a3);
  } /*CallShortMethodA*/

static inline jint JNCallIntMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jint const result = (**env).CallIntMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallIntMethod*/

static inline jint JNCallIntMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallIntMethodV(env, a1, a2, a3);
  } /*CallIntMethodV*/

static inline jint JNCallIntMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallIntMethodA(env, a1, a2, a3);
  } /*CallIntMethodA*/

static inline jlong JNCallLongMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jlong const result = (**env).CallLongMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallLongMethod*/

static inline jlong JNCallLongMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallLongMethodV(env, a1, a2, a3);
  } /*CallLongMethodV*/

static inline jlong JNCallLongMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallLongMethodA(env, a1, a2, a3);
  } /*CallLongMethodA*/

static inline jfloat JNCallFloatMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jfloat const result = (**env).CallFloatMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallFloatMethod*/

static inline jfloat JNCallFloatMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallFloatMethodV(env, a1, a2, a3);
  } /*CallFloatMethodV*/

static inline jfloat JNCallFloatMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallFloatMethodA(env, a1, a2, a3);
  } /*CallFloatMethodA*/

static inline jdouble JNCallDoubleMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jdouble const result = (**env).CallDoubleMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallDoubleMethod*/

static inline jdouble JNCallDoubleMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallDoubleMethodV(env, a1, a2, a3);
  } /*CallDoubleMethodV*/

static inline jdouble JNCallDoubleMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallDoubleMethodA(env, a1, a2, a3);
  } /*CallDoubleMethodA*/

static inline void JNCallVoidMethod(JNIEnv* env, jobject a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    (**env).CallVoidMethodV(env, a1, a2, va);
    va_end(va);
  } /*CallVoidMethod*/

static inline void JNCallVoidMethodV(JNIEnv* env, jobject a1, jmethodID a2, va_list a3)
  {
    (**env).CallVoidMethodV(env, a1, a2, a3);
  } /*CallVoidMethodV*/

static inline void JNCallVoidMethodA(JNIEnv* env, jobject a1, jmethodID a2, jvalue* a3)
  {
    (**env).CallVoidMethodA(env, a1, a2, a3);
  } /*CallVoidMethodA*/

static inline jobject JNCallNonvirtualObjectMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jobject const result = (**env).CallNonvirtualObjectMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualObjectMethod*/

static inline jobject JNCallNonvirtualObjectMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualObjectMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualObjectMethodV*/

static inline jobject JNCallNonvirtualObjectMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualObjectMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualObjectMethodA*/

static inline jboolean JNCallNonvirtualBooleanMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jboolean const result = (**env).CallNonvirtualBooleanMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualBooleanMethod*/

static inline jboolean JNCallNonvirtualBooleanMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualBooleanMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualBooleanMethodV*/

static inline jboolean JNCallNonvirtualBooleanMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualBooleanMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualBooleanMethodA*/

static inline jbyte JNCallNonvirtualByteMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jbyte const result = (**env).CallNonvirtualByteMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualByteMethod*/

static inline jbyte JNCallNonvirtualByteMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualByteMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualByteMethodV*/

static inline jbyte JNCallNonvirtualByteMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualByteMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualByteMethodA*/

static inline jchar JNCallNonvirtualCharMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jchar const result = (**env).CallNonvirtualCharMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualCharMethod*/

static inline jchar JNCallNonvirtualCharMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualCharMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualCharMethodV*/

static inline jchar JNCallNonvirtualCharMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualCharMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualCharMethodA*/

static inline jshort JNCallNonvirtualShortMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jshort const result = (**env).CallNonvirtualShortMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualShortMethod*/

static inline jshort JNCallNonvirtualShortMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualShortMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualShortMethodV*/

static inline jshort JNCallNonvirtualShortMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualShortMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualShortMethodA*/

static inline jint JNCallNonvirtualIntMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jint const result = (**env).CallNonvirtualIntMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualIntMethod*/

static inline jint JNCallNonvirtualIntMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualIntMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualIntMethodV*/

static inline jint JNCallNonvirtualIntMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualIntMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualIntMethodA*/

static inline jlong JNCallNonvirtualLongMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jlong const result = (**env).CallNonvirtualLongMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualLongMethod*/

static inline jlong JNCallNonvirtualLongMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualLongMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualLongMethodV*/

static inline jlong JNCallNonvirtualLongMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualLongMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualLongMethodA*/

static inline jfloat JNCallNonvirtualFloatMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jfloat const result = (**env).CallNonvirtualFloatMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualFloatMethod*/

static inline jfloat JNCallNonvirtualFloatMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualFloatMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualFloatMethodV*/

static inline jfloat JNCallNonvirtualFloatMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualFloatMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualFloatMethodA*/

static inline jdouble JNCallNonvirtualDoubleMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    jdouble const result = (**env).CallNonvirtualDoubleMethodV(env, a1, a2, a3, va);
    va_end(va);
    return
        result;
  } /*CallNonvirtualDoubleMethod*/

static inline jdouble JNCallNonvirtualDoubleMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    return
        (**env).CallNonvirtualDoubleMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualDoubleMethodV*/

static inline jdouble JNCallNonvirtualDoubleMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    return
        (**env).CallNonvirtualDoubleMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualDoubleMethodA*/

static inline void JNCallNonvirtualVoidMethod(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, ...)
  {
    va_list va;
    va_start(va, a3);
    (**env).CallNonvirtualVoidMethodV(env, a1, a2, a3, va);
    va_end(va);
  } /*CallNonvirtualVoidMethod*/

static inline void JNCallNonvirtualVoidMethodV(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, va_list a4)
  {
    (**env).CallNonvirtualVoidMethodV(env, a1, a2, a3, a4);
  } /*CallNonvirtualVoidMethodV*/

static inline void JNCallNonvirtualVoidMethodA(JNIEnv* env, jobject a1, jclass a2, jmethodID a3, jvalue* a4)
  {
    (**env).CallNonvirtualVoidMethodA(env, a1, a2, a3, a4);
  } /*CallNonvirtualVoidMethodA*/

static inline jfieldID JNGetFieldID(JNIEnv* env, jclass a1, const char* a2, const char* a3)
  {
    return
        (**env).GetFieldID(env, a1, a2, a3);
  } /*GetFieldID*/

static inline jobject JNGetObjectField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetObjectField(env, a1, a2);
  } /*GetObjectField*/

static inline jboolean JNGetBooleanField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetBooleanField(env, a1, a2);
  } /*GetBooleanField*/

static inline jbyte JNGetByteField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetByteField(env, a1, a2);
  } /*GetByteField*/

static inline jchar JNGetCharField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetCharField(env, a1, a2);
  } /*GetCharField*/

static inline jshort JNGetShortField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetShortField(env, a1, a2);
  } /*GetShortField*/

static inline jint JNGetIntField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetIntField(env, a1, a2);
  } /*GetIntField*/

static inline jlong JNGetLongField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetLongField(env, a1, a2);
  } /*GetLongField*/

static inline jfloat JNGetFloatField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetFloatField(env, a1, a2);
  } /*GetFloatField*/

static inline jdouble JNGetDoubleField(JNIEnv* env, jobject a1, jfieldID a2)
  {
    return
        (**env).GetDoubleField(env, a1, a2);
  } /*GetDoubleField*/

static inline void JNSetObjectField(JNIEnv* env, jobject a1, jfieldID a2, jobject a3)
  {
    (**env).SetObjectField(env, a1, a2, a3);
  } /*SetObjectField*/

static inline void JNSetBooleanField(JNIEnv* env, jobject a1, jfieldID a2, jboolean a3)
  {
    (**env).SetBooleanField(env, a1, a2, a3);
  } /*SetBooleanField*/

static inline void JNSetByteField(JNIEnv* env, jobject a1, jfieldID a2, jbyte a3)
  {
    (**env).SetByteField(env, a1, a2, a3);
  } /*SetByteField*/

static inline void JNSetCharField(JNIEnv* env, jobject a1, jfieldID a2, jchar a3)
  {
    (**env).SetCharField(env, a1, a2, a3);
  } /*SetCharField*/

static inline void JNSetShortField(JNIEnv* env, jobject a1, jfieldID a2, jshort a3)
  {
    (**env).SetShortField(env, a1, a2, a3);
  } /*SetShortField*/

static inline void JNSetIntField(JNIEnv* env, jobject a1, jfieldID a2, jint a3)
  {
    (**env).SetIntField(env, a1, a2, a3);
  } /*SetIntField*/

static inline void JNSetLongField(JNIEnv* env, jobject a1, jfieldID a2, jlong a3)
  {
    (**env).SetLongField(env, a1, a2, a3);
  } /*SetLongField*/

static inline void JNSetFloatField(JNIEnv* env, jobject a1, jfieldID a2, jfloat a3)
  {
    (**env).SetFloatField(env, a1, a2, a3);
  } /*SetFloatField*/

static inline void JNSetDoubleField(JNIEnv* env, jobject a1, jfieldID a2, jdouble a3)
  {
    (**env).SetDoubleField(env, a1, a2, a3);
  } /*SetDoubleField*/

static inline jmethodID JNGetStaticMethodID(JNIEnv* env, jclass a1, const char* a2, const char* a3)
  {
    return
        (**env).GetStaticMethodID(env, a1, a2, a3);
  } /*GetStaticMethodID*/

static inline jobject JNCallStaticObjectMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jobject const result = (**env).CallStaticObjectMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticObjectMethod*/

static inline jobject JNCallStaticObjectMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticObjectMethodV(env, a1, a2, a3);
  } /*CallStaticObjectMethodV*/

static inline jobject JNCallStaticObjectMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticObjectMethodA(env, a1, a2, a3);
  } /*CallStaticObjectMethodA*/

static inline jboolean JNCallStaticBooleanMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jboolean const result = (**env).CallStaticBooleanMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticBooleanMethod*/

static inline jboolean JNCallStaticBooleanMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticBooleanMethodV(env, a1, a2, a3);
  } /*CallStaticBooleanMethodV*/

static inline jboolean JNCallStaticBooleanMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticBooleanMethodA(env, a1, a2, a3);
  } /*CallStaticBooleanMethodA*/

static inline jbyte JNCallStaticByteMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jbyte const result = (**env).CallStaticByteMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticByteMethod*/

static inline jbyte JNCallStaticByteMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticByteMethodV(env, a1, a2, a3);
  } /*CallStaticByteMethodV*/

static inline jbyte JNCallStaticByteMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticByteMethodA(env, a1, a2, a3);
  } /*CallStaticByteMethodA*/

static inline jchar JNCallStaticCharMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jchar const result = (**env).CallStaticCharMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticCharMethod*/

static inline jchar JNCallStaticCharMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticCharMethodV(env, a1, a2, a3);
  } /*CallStaticCharMethodV*/

static inline jchar JNCallStaticCharMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticCharMethodA(env, a1, a2, a3);
  } /*CallStaticCharMethodA*/

static inline jshort JNCallStaticShortMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jshort const result = (**env).CallStaticShortMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticShortMethod*/

static inline jshort JNCallStaticShortMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticShortMethodV(env, a1, a2, a3);
  } /*CallStaticShortMethodV*/

static inline jshort JNCallStaticShortMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticShortMethodA(env, a1, a2, a3);
  } /*CallStaticShortMethodA*/

static inline jint JNCallStaticIntMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jint const result = (**env).CallStaticIntMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticIntMethod*/

static inline jint JNCallStaticIntMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticIntMethodV(env, a1, a2, a3);
  } /*CallStaticIntMethodV*/

static inline jint JNCallStaticIntMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticIntMethodA(env, a1, a2, a3);
  } /*CallStaticIntMethodA*/

static inline jlong JNCallStaticLongMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jlong const result = (**env).CallStaticLongMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticLongMethod*/

static inline jlong JNCallStaticLongMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticLongMethodV(env, a1, a2, a3);
  } /*CallStaticLongMethodV*/

static inline jlong JNCallStaticLongMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticLongMethodA(env, a1, a2, a3);
  } /*CallStaticLongMethodA*/

static inline jfloat JNCallStaticFloatMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jfloat const result = (**env).CallStaticFloatMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticFloatMethod*/

static inline jfloat JNCallStaticFloatMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticFloatMethodV(env, a1, a2, a3);
  } /*CallStaticFloatMethodV*/

static inline jfloat JNCallStaticFloatMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticFloatMethodA(env, a1, a2, a3);
  } /*CallStaticFloatMethodA*/

static inline jdouble JNCallStaticDoubleMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    jdouble const result = (**env).CallStaticDoubleMethodV(env, a1, a2, va);
    va_end(va);
    return
        result;
  } /*CallStaticDoubleMethod*/

static inline jdouble JNCallStaticDoubleMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    return
        (**env).CallStaticDoubleMethodV(env, a1, a2, a3);
  } /*CallStaticDoubleMethodV*/

static inline jdouble JNCallStaticDoubleMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    return
        (**env).CallStaticDoubleMethodA(env, a1, a2, a3);
  } /*CallStaticDoubleMethodA*/

static inline void JNCallStaticVoidMethod(JNIEnv* env, jclass a1, jmethodID a2, ...)
  {
    va_list va;
    va_start(va, a2);
    (**env).CallStaticVoidMethodV(env, a1, a2, va);
    va_end(va);
  } /*CallStaticVoidMethod*/

static inline void JNCallStaticVoidMethodV(JNIEnv* env, jclass a1, jmethodID a2, va_list a3)
  {
    (**env).CallStaticVoidMethodV(env, a1, a2, a3);
  } /*CallStaticVoidMethodV*/

static inline void JNCallStaticVoidMethodA(JNIEnv* env, jclass a1, jmethodID a2, jvalue* a3)
  {
    (**env).CallStaticVoidMethodA(env, a1, a2, a3);
  } /*CallStaticVoidMethodA*/

static inline jfieldID JNGetStaticFieldID(JNIEnv* env, jclass a1, const char* a2, const char* a3)
  {
    return
        (**env).GetStaticFieldID(env, a1, a2, a3);
  } /*GetStaticFieldID*/

static inline jobject JNGetStaticObjectField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticObjectField(env, a1, a2);
  } /*GetStaticObjectField*/

static inline jboolean JNGetStaticBooleanField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticBooleanField(env, a1, a2);
  } /*GetStaticBooleanField*/

static inline jbyte JNGetStaticByteField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticByteField(env, a1, a2);
  } /*GetStaticByteField*/

static inline jchar JNGetStaticCharField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticCharField(env, a1, a2);
  } /*GetStaticCharField*/

static inline jshort JNGetStaticShortField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticShortField(env, a1, a2);
  } /*GetStaticShortField*/

static inline jint JNGetStaticIntField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticIntField(env, a1, a2);
  } /*GetStaticIntField*/

static inline jlong JNGetStaticLongField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticLongField(env, a1, a2);
  } /*GetStaticLongField*/

static inline jfloat JNGetStaticFloatField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticFloatField(env, a1, a2);
  } /*GetStaticFloatField*/

static inline jdouble JNGetStaticDoubleField(JNIEnv* env, jclass a1, jfieldID a2)
  {
    return
        (**env).GetStaticDoubleField(env, a1, a2);
  } /*GetStaticDoubleField*/

static inline void JNSetStaticObjectField(JNIEnv* env, jclass a1, jfieldID a2, jobject a3)
  {
    (**env).SetStaticObjectField(env, a1, a2, a3);
  } /*SetStaticObjectField*/

static inline void JNSetStaticBooleanField(JNIEnv* env, jclass a1, jfieldID a2, jboolean a3)
  {
    (**env).SetStaticBooleanField(env, a1, a2, a3);
  } /*SetStaticBooleanField*/

static inline void JNSetStaticByteField(JNIEnv* env, jclass a1, jfieldID a2, jbyte a3)
  {
    (**env).SetStaticByteField(env, a1, a2, a3);
  } /*SetStaticByteField*/

static inline void JNSetStaticCharField(JNIEnv* env, jclass a1, jfieldID a2, jchar a3)
  {
    (**env).SetStaticCharField(env, a1, a2, a3);
  } /*SetStaticCharField*/

static inline void JNSetStaticShortField(JNIEnv* env, jclass a1, jfieldID a2, jshort a3)
  {
    (**env).SetStaticShortField(env, a1, a2, a3);
  } /*SetStaticShortField*/

static inline void JNSetStaticIntField(JNIEnv* env, jclass a1, jfieldID a2, jint a3)
  {
    (**env).SetStaticIntField(env, a1, a2, a3);
  } /*SetStaticIntField*/

static inline void JNSetStaticLongField(JNIEnv* env, jclass a1, jfieldID a2, jlong a3)
  {
    (**env).SetStaticLongField(env, a1, a2, a3);
  } /*SetStaticLongField*/

static inline void JNSetStaticFloatField(JNIEnv* env, jclass a1, jfieldID a2, jfloat a3)
  {
    (**env).SetStaticFloatField(env, a1, a2, a3);
  } /*SetStaticFloatField*/

static inline void JNSetStaticDoubleField(JNIEnv* env, jclass a1, jfieldID a2, jdouble a3)
  {
    (**env).SetStaticDoubleField(env, a1, a2, a3);
  } /*SetStaticDoubleField*/

static inline jstring JNNewString(JNIEnv* env, const jchar* a1, jsize a2)
  {
    return
        (**env).NewString(env, a1, a2);
  } /*NewString*/

static inline jsize JNGetStringLength(JNIEnv* env, jstring a1)
  {
    return
        (**env).GetStringLength(env, a1);
  } /*GetStringLength*/

static inline const jchar* JNGetStringChars(JNIEnv* env, jstring a1, jboolean* a2)
  {
    return
        (**env).GetStringChars(env, a1, a2);
  } /*GetStringChars*/

static inline void JNReleaseStringChars(JNIEnv* env, jstring a1, const jchar* a2)
  {
    (**env).ReleaseStringChars(env, a1, a2);
  } /*ReleaseStringChars*/

static inline jstring JNNewStringUTF(JNIEnv* env, const char* a1)
  {
    return
        (**env).NewStringUTF(env, a1);
  } /*NewStringUTF*/

static inline jsize JNGetStringUTFLength(JNIEnv* env, jstring a1)
  {
    return
        (**env).GetStringUTFLength(env, a1);
  } /*GetStringUTFLength*/

static inline const char* JNGetStringUTFChars(JNIEnv* env, jstring a1, jboolean* a2)
  {
    return
        (**env).GetStringUTFChars(env, a1, a2);
  } /*GetStringUTFChars*/

static inline void JNReleaseStringUTFChars(JNIEnv* env, jstring a1, const char* a2)
  {
    (**env).ReleaseStringUTFChars(env, a1, a2);
  } /*ReleaseStringUTFChars*/

static inline jsize JNGetArrayLength(JNIEnv* env, jarray a1)
  {
    return
        (**env).GetArrayLength(env, a1);
  } /*GetArrayLength*/

static inline jobjectArray JNNewObjectArray(JNIEnv* env, jsize a1, jclass a2, jobject a3)
  {
    return
        (**env).NewObjectArray(env, a1, a2, a3);
  } /*NewObjectArray*/

static inline jobject JNGetObjectArrayElement(JNIEnv* env, jobjectArray a1, jsize a2)
  {
    return
        (**env).GetObjectArrayElement(env, a1, a2);
  } /*GetObjectArrayElement*/

static inline void JNSetObjectArrayElement(JNIEnv* env, jobjectArray a1, jsize a2, jobject a3)
  {
    (**env).SetObjectArrayElement(env, a1, a2, a3);
  } /*SetObjectArrayElement*/

static inline jbooleanArray JNNewBooleanArray(JNIEnv* env, jsize a1)
  {
    return
        (**env).NewBooleanArray(env, a1);
  } /*NewBooleanArray*/

static inline jbyteArray JNNewByteArray(JNIEnv* env, jsize a1)
  {
    return
        (**env).NewByteArray(env, a1);
  } /*NewByteArray*/

static inline jcharArray JNNewCharArray(JNIEnv* env, jsize a1)
  {
    return
        (**env).NewCharArray(env, a1);
  } /*NewCharArray*/

static inline jshortArray JNNewShortArray(JNIEnv* env, jsize a1)
  {
    return
        (**env).NewShortArray(env, a1);
  } /*NewShortArray*/

static inline jintArray JNNewIntArray(JNIEnv* env, jsize a1)
  {
    return
        (**env).NewIntArray(env, a1);
  } /*NewIntArray*/

static inline jlongArray JNNewLongArray(JNIEnv* env, jsize a1)
  {
    return
        (**env).NewLongArray(env, a1);
  } /*NewLongArray*/

static inline jfloatArray JNNewFloatArray(JNIEnv* env, jsize a1)
  {
    return
        (**env).NewFloatArray(env, a1);
  } /*NewFloatArray*/

static inline jdoubleArray JNNewDoubleArray(JNIEnv* env, jsize a1)
  {
    return
        (**env).NewDoubleArray(env, a1);
  } /*NewDoubleArray*/

static inline jboolean* JNGetBooleanArrayElements(JNIEnv* env, jbooleanArray a1, jboolean* a2)
  {
    return
        (**env).GetBooleanArrayElements(env, a1, a2);
  } /*GetBooleanArrayElements*/

static inline jbyte* JNGetByteArrayElements(JNIEnv* env, jbyteArray a1, jboolean* a2)
  {
    return
        (**env).GetByteArrayElements(env, a1, a2);
  } /*GetByteArrayElements*/

static inline jchar* JNGetCharArrayElements(JNIEnv* env, jcharArray a1, jboolean* a2)
  {
    return
        (**env).GetCharArrayElements(env, a1, a2);
  } /*GetCharArrayElements*/

static inline jshort* JNGetShortArrayElements(JNIEnv* env, jshortArray a1, jboolean* a2)
  {
    return
        (**env).GetShortArrayElements(env, a1, a2);
  } /*GetShortArrayElements*/

static inline jint* JNGetIntArrayElements(JNIEnv* env, jintArray a1, jboolean* a2)
  {
    return
        (**env).GetIntArrayElements(env, a1, a2);
  } /*GetIntArrayElements*/

static inline jlong* JNGetLongArrayElements(JNIEnv* env, jlongArray a1, jboolean* a2)
  {
    return
        (**env).GetLongArrayElements(env, a1, a2);
  } /*GetLongArrayElements*/

static inline jfloat* JNGetFloatArrayElements(JNIEnv* env, jfloatArray a1, jboolean* a2)
  {
    return
        (**env).GetFloatArrayElements(env, a1, a2);
  } /*GetFloatArrayElements*/

static inline jdouble* JNGetDoubleArrayElements(JNIEnv* env, jdoubleArray a1, jboolean* a2)
  {
    return
        (**env).GetDoubleArrayElements(env, a1, a2);
  } /*GetDoubleArrayElements*/

static inline void JNReleaseBooleanArrayElements(JNIEnv* env, jbooleanArray a1, jboolean* a2, jint a3)
  {
    (**env).ReleaseBooleanArrayElements(env, a1, a2, a3);
  } /*ReleaseBooleanArrayElements*/

static inline void JNReleaseByteArrayElements(JNIEnv* env, jbyteArray a1, jbyte* a2, jint a3)
  {
    (**env).ReleaseByteArrayElements(env, a1, a2, a3);
  } /*ReleaseByteArrayElements*/

static inline void JNReleaseCharArrayElements(JNIEnv* env, jcharArray a1, jchar* a2, jint a3)
  {
    (**env).ReleaseCharArrayElements(env, a1, a2, a3);
  } /*ReleaseCharArrayElements*/

static inline void JNReleaseShortArrayElements(JNIEnv* env, jshortArray a1, jshort* a2, jint a3)
  {
    (**env).ReleaseShortArrayElements(env, a1, a2, a3);
  } /*ReleaseShortArrayElements*/

static inline void JNReleaseIntArrayElements(JNIEnv* env, jintArray a1, jint* a2, jint a3)
  {
    (**env).ReleaseIntArrayElements(env, a1, a2, a3);
  } /*ReleaseIntArrayElements*/

static inline void JNReleaseLongArrayElements(JNIEnv* env, jlongArray a1, jlong* a2, jint a3)
  {
    (**env).ReleaseLongArrayElements(env, a1, a2, a3);
  } /*ReleaseLongArrayElements*/

static inline void JNReleaseFloatArrayElements(JNIEnv* env, jfloatArray a1, jfloat* a2, jint a3)
  {
    (**env).ReleaseFloatArrayElements(env, a1, a2, a3);
  } /*ReleaseFloatArrayElements*/

static inline void JNReleaseDoubleArrayElements(JNIEnv* env, jdoubleArray a1, jdouble* a2, jint a3)
  {
    (**env).ReleaseDoubleArrayElements(env, a1, a2, a3);
  } /*ReleaseDoubleArrayElements*/

static inline void JNGetBooleanArrayRegion(JNIEnv* env, jbooleanArray a1, jsize a2, jsize a3, jboolean* a4)
  {
    (**env).GetBooleanArrayRegion(env, a1, a2, a3, a4);
  } /*GetBooleanArrayRegion*/

static inline void JNGetByteArrayRegion(JNIEnv* env, jbyteArray a1, jsize a2, jsize a3, jbyte* a4)
  {
    (**env).GetByteArrayRegion(env, a1, a2, a3, a4);
  } /*GetByteArrayRegion*/

static inline void JNGetCharArrayRegion(JNIEnv* env, jcharArray a1, jsize a2, jsize a3, jchar* a4)
  {
    (**env).GetCharArrayRegion(env, a1, a2, a3, a4);
  } /*GetCharArrayRegion*/

static inline void JNGetShortArrayRegion(JNIEnv* env, jshortArray a1, jsize a2, jsize a3, jshort* a4)
  {
    (**env).GetShortArrayRegion(env, a1, a2, a3, a4);
  } /*GetShortArrayRegion*/

static inline void JNGetIntArrayRegion(JNIEnv* env, jintArray a1, jsize a2, jsize a3, jint* a4)
  {
    (**env).GetIntArrayRegion(env, a1, a2, a3, a4);
  } /*GetIntArrayRegion*/

static inline void JNGetLongArrayRegion(JNIEnv* env, jlongArray a1, jsize a2, jsize a3, jlong* a4)
  {
    (**env).GetLongArrayRegion(env, a1, a2, a3, a4);
  } /*GetLongArrayRegion*/

static inline void JNGetFloatArrayRegion(JNIEnv* env, jfloatArray a1, jsize a2, jsize a3, jfloat* a4)
  {
    (**env).GetFloatArrayRegion(env, a1, a2, a3, a4);
  } /*GetFloatArrayRegion*/

static inline void JNGetDoubleArrayRegion(JNIEnv* env, jdoubleArray a1, jsize a2, jsize a3, jdouble* a4)
  {
    (**env).GetDoubleArrayRegion(env, a1, a2, a3, a4);
  } /*GetDoubleArrayRegion*/

static inline void JNSetBooleanArrayRegion(JNIEnv* env, jbooleanArray a1, jsize a2, jsize a3, const jboolean* a4)
  {
    (**env).SetBooleanArrayRegion(env, a1, a2, a3, a4);
  } /*SetBooleanArrayRegion*/

static inline void JNSetByteArrayRegion(JNIEnv* env, jbyteArray a1, jsize a2, jsize a3, const jbyte* a4)
  {
    (**env).SetByteArrayRegion(env, a1, a2, a3, a4);
  } /*SetByteArrayRegion*/

static inline void JNSetCharArrayRegion(JNIEnv* env, jcharArray a1, jsize a2, jsize a3, const jchar* a4)
  {
    (**env).SetCharArrayRegion(env, a1, a2, a3, a4);
  } /*SetCharArrayRegion*/

static inline void JNSetShortArrayRegion(JNIEnv* env, jshortArray a1, jsize a2, jsize a3, const jshort* a4)
  {
    (**env).SetShortArrayRegion(env, a1, a2, a3, a4);
  } /*SetShortArrayRegion*/

static inline void JNSetIntArrayRegion(JNIEnv* env, jintArray a1, jsize a2, jsize a3, const jint* a4)
  {
    (**env).SetIntArrayRegion(env, a1, a2, a3, a4);
  } /*SetIntArrayRegion*/

static inline void JNSetLongArrayRegion(JNIEnv* env, jlongArray a1, jsize a2, jsize a3, const jlong* a4)
  {
    (**env).SetLongArrayRegion(env, a1, a2, a3, a4);
  } /*SetLongArrayRegion*/

static inline void JNSetFloatArrayRegion(JNIEnv* env, jfloatArray a1, jsize a2, jsize a3, const jfloat* a4)
  {
    (**env).SetFloatArrayRegion(env, a1, a2, a3, a4);
  } /*SetFloatArrayRegion*/

static inline void JNSetDoubleArrayRegion(JNIEnv* env, jdoubleArray a1, jsize a2, jsize a3, const jdouble* a4)
  {
    (**env).SetDoubleArrayRegion(env, a1, a2, a3, a4);
  } /*SetDoubleArrayRegion*/

static inline jint JNRegisterNatives(JNIEnv* env, jclass a1, const JNINativeMethod* a2, jint a3)
  {
    return
        (**env).RegisterNatives(env, a1, a2, a3);
  } /*RegisterNatives*/

static inline jint JNUnregisterNatives(JNIEnv* env, jclass a1)
  {
    return
        (**env).UnregisterNatives(env, a1);
  } /*UnregisterNatives*/

static inline jint JNMonitorEnter(JNIEnv* env, jobject a1)
  {
    return
        (**env).MonitorEnter(env, a1);
  } /*MonitorEnter*/

static inline jint JNMonitorExit(JNIEnv* env, jobject a1)
  {
    return
        (**env).MonitorExit(env, a1);
  } /*MonitorExit*/

static inline jint JNGetJavaVM(JNIEnv* env, JavaVM** a1)
  {
    return
        (**env).GetJavaVM(env, a1);
  } /*GetJavaVM*/

static inline void JNGetStringRegion(JNIEnv* env, jstring a1, jsize a2, jsize a3, jchar* a4)
  {
    (**env).GetStringRegion(env, a1, a2, a3, a4);
  } /*GetStringRegion*/

static inline void JNGetStringUTFRegion(JNIEnv* env, jstring a1, jsize a2, jsize a3, char* a4)
  {
    (**env).GetStringUTFRegion(env, a1, a2, a3, a4);
  } /*GetStringUTFRegion*/

static inline void* JNGetPrimitiveArrayCritical(JNIEnv* env, jarray a1, jboolean* a2)
  {
    return
        (**env).GetPrimitiveArrayCritical(env, a1, a2);
  } /*GetPrimitiveArrayCritical*/

static inline void JNReleasePrimitiveArrayCritical(JNIEnv* env, jarray a1, void* a2, jint a3)
  {
    (**env).ReleasePrimitiveArrayCritical(env, a1, a2, a3);
  } /*ReleasePrimitiveArrayCritical*/

static inline const jchar* JNGetStringCritical(JNIEnv* env, jstring a1, jboolean* a2)
  {
    return
        (**env).GetStringCritical(env, a1, a2);
  } /*GetStringCritical*/

static inline void JNReleaseStringCritical(JNIEnv* env, jstring a1, const jchar* a2)
  {
    (**env).ReleaseStringCritical(env, a1, a2);
  } /*ReleaseStringCritical*/

static inline jweak JNNewWeakGlobalRef(JNIEnv* env, jobject a1)
  {
    return
        (**env).NewWeakGlobalRef(env, a1);
  } /*NewWeakGlobalRef*/

static inline void JNDeleteWeakGlobalRef(JNIEnv* env, jweak a1)
  {
    (**env).DeleteWeakGlobalRef(env, a1);
  } /*DeleteWeakGlobalRef*/

static inline jboolean JNExceptionCheck(JNIEnv* env)
  {
    return
        (**env).ExceptionCheck(env);
  } /*ExceptionCheck*/

static inline jobject JNNewDirectByteBuffer(JNIEnv* env, void* a1, jlong a2)
  {
    return
        (**env).NewDirectByteBuffer(env, a1, a2);
  } /*NewDirectByteBuffer*/

static inline void* JNGetDirectBufferAddress(JNIEnv* env, jobject a1)
  {
    return
        (**env).GetDirectBufferAddress(env, a1);
  } /*GetDirectBufferAddress*/

static inline jlong JNGetDirectBufferCapacity(JNIEnv* env, jobject a1)
  {
    return
        (**env).GetDirectBufferCapacity(env, a1);
  } /*GetDirectBufferCapacity*/

static inline jobjectRefType JNGetObjectRefType(JNIEnv* env, jobject a1)
  {
    return
        (**env).GetObjectRefType(env, a1);
  } /*GetObjectRefType*/

