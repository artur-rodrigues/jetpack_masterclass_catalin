package com.example.dogs.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(message: String, time: Int? = null) {
    Toast.makeText(this, message, time ?: Toast.LENGTH_LONG).show()
}

fun Context.toast(@StringRes message: Int, time: Int? = null) {
    Toast.makeText(this, message, time ?: Toast.LENGTH_LONG).show()
}