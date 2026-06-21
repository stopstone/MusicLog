package com.stopstone.musicplaylist.util

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.stopstone.musicplaylist.BuildConfig

private const val TAG = "MusicLog"

private fun buildLogMsg(message: String): String {
    val ste = Thread.currentThread().stackTrace[4]
    val fileName = ste.fileName.replace(".java", "").replace(".kt", "")
    return "[$fileName::${ste.methodName} (${ste.fileName}:${ste.lineNumber})]$message"
}

fun logd(message: String) {
    if (!BuildConfig.DEBUG) return
    Log.d(TAG, buildLogMsg(message))
}

fun logi(message: String) {
    if (!BuildConfig.DEBUG) return
    Log.i(TAG, buildLogMsg(message))
}

fun logw(message: String) {
    val logMessage = buildLogMsg(message)
    if (BuildConfig.DEBUG) {
        Log.w(TAG, logMessage)
    }
    FirebaseCrashlytics.getInstance().log(logMessage)
}

fun loge(message: String) {
    val logMessage = buildLogMsg(message)
    if (BuildConfig.DEBUG) {
        Log.e(TAG, logMessage)
    }
    FirebaseCrashlytics.getInstance().log(logMessage)
}

fun recordException(throwable: Throwable) {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, buildLogMsg("Exception: ${throwable.message}"), throwable)
    }
    FirebaseCrashlytics.getInstance().recordException(throwable)
}

fun recordException(
    message: String,
    throwable: Throwable,
) {
    val logMessage = buildLogMsg("$message: ${throwable.message}")
    if (BuildConfig.DEBUG) {
        Log.e(TAG, logMessage, throwable)
    }
    FirebaseCrashlytics.getInstance().apply {
        log(logMessage)
        recordException(throwable)
    }
}
