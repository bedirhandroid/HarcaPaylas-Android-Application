package com.bedirhandag.harcapaylas.util

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bedirhandag.harcapaylas.R

fun Context.showToast(message: String) =
    Toast.makeText(this, message, LENGTH_LONG).show()

fun RecyclerView.applyDivider() {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        R.drawable.divider
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}