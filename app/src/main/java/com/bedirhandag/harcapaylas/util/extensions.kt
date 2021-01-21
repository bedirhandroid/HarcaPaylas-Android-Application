package com.bedirhandag.harcapaylas.util

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG

fun Context.showToast(message: String) =
    Toast.makeText(this, message, LENGTH_LONG).show()