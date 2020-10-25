#include "myltl2ba.h"
#include "rwth_i2_ltl2ba4j_internal_jnibridge_BAJni.h"

JNIEXPORT void JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_init
(JNIEnv *env, jobject obj) {
  tl_out = stderr;
  tl_verbose = 0;
  bstates = 0;
}

JNIEXPORT void JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_setform
(JNIEnv *env, jobject obj, jstring jstr) {
  const char *formula;

  formula = (*env)->GetStringUTFChars(env,jstr,NULL);
  if (formula == NULL) {
    return; /* oom */
  }
  hasuform = strlen(formula);
  strncpy(uform,formula,hasuform);
  (*env)->ReleaseStringUTFChars(env,jstr,formula);
  cnt = 0;
  sym_id = 0;
  node_id = 1;
  return ;
}

JNIEXPORT void JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_tl_1parse
(JNIEnv *env, jobject obj) {
  if (!(uform == NULL)) {
    tl_parse();
  } else {
    fprintf(stderr,"tl_parse(): arf arf!\n");
  }
}

JNIEXPORT jlong JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_bstates
(JNIEnv *env, jobject obj) {
  return (jlong)bstates;
}

JNIEXPORT jint JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_b_1accept
(JNIEnv *env, jobject obj) {
  return b_accept;
}

JNIEXPORT void JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_print_1buchi
(JNIEnv *env, jobject obj, jlong p) {
  if (!((BState*)p == NULL)) {
    print_buchi((BState*)p);
  } else {
    fprintf(stderr,"print_buchi(): arf arf!\n");
  }
}

JNIEXPORT jint JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_get_1sym_1id
(JNIEnv *env, jobject obj) {
  return sym_id;
}

/*
 * Class:     BAJni
 * Method:    get_sym_size
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_get_1sym_1size
(JNIEnv *env, jobject obj) {
  return sym_size;
}

/*
 * Class:     BAJni
 * Method:    get_sym_table
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_get_1sym_1table
(JNIEnv *env, jobject obj) {
  jobjectArray jstra;
  jclass strArrCls = (*env)->FindClass(env,"java/lang/String");
  int i;
  if (strArrCls == NULL) {
    return NULL; /**/
  }
  jstra = (*env)->NewObjectArray(env,sym_id,strArrCls,NULL);
  for(i=0;i<sym_id;i++) {
    jstring s = (*env)->NewStringUTF(env,sym_table[i]);
    if (s == NULL) {
      return NULL; /* oom */
    }
    (*env)->SetObjectArrayElement(env,jstra,i,s);
    (*env)->DeleteLocalRef(env,s);
  }
  return jstra;
}


JNIEXPORT jlong JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_bstateNxt
(JNIEnv *env, jobject obj, jlong p) {
  return (jlong)((BState *)p)->nxt;
}

JNIEXPORT jint JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_bstateFinal
(JNIEnv *env, jobject obj, jlong p){
  return ((BState *)p)->final;
}

/*
 * Class:     BAJni
 * Method:    bstateId
 * Signature: (J)I
 */
JNIEXPORT jint JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_bstateId
(JNIEnv *env, jobject obj, jlong p){
  return ((BState *)p)->id;
}

/*
 * Class:     BAJni
 * Method:    bstateFirstTrans
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_bstateFirstTrans
(JNIEnv *env, jobject obj, jlong p){
  return (jlong)((BState *)p)->trans;
}

/* TRANS */

JNIEXPORT jlong JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_btransNxt
(JNIEnv *env, jobject obj, jlong p) {
  return (jlong)((BTrans*)p)->nxt;
}

/*
 * Class:     BAJni
 * Method:    btransTo
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_btransTo
(JNIEnv *env, jobject obj, jlong p) {
  return (jlong)((BTrans*)p)->to;
}

/*
 * Class:     BAJni
 * Method:    btransPos
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_btransPos
(JNIEnv *env, jobject obj, jlong p) {
  return *(((BTrans*)p)->pos);
}

/*
 * Class:     BAJni
 * Method:    btransNeg
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_rwth_i2_ltl2ba4j_internal_jnibridge_BAJni_btransNeg
(JNIEnv *env, jobject obj, jlong p) {
  return *(((BTrans*)p)->neg);
}
