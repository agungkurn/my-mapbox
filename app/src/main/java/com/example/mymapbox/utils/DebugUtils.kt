package com.example.mymapbox.utils

import android.util.Log

fun Any.debugLog(message: String, instance: Any? = null) {
	if (instance == null) {
		Log.d(this::class.java.simpleName, message)
	} else {
		Log.d(instance::class.java.simpleName, message)
	}
}

fun Any.errorLog(message: String, instance: Any? = null) {
	if (instance == null) {
		Log.e(this::class.java.simpleName, message)
	} else {
		Log.e(instance::class.java.simpleName, message)
	}
}