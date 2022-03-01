package com.bedirhandag.harcapaylas.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bedirhandag.harcapaylas.R
import com.bedirhandag.harcapaylas.model.GroupMemberDetail

fun Context.showToast(message: String) =
    Toast.makeText(this, message, LENGTH_SHORT).show()

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

var alert: AlertDialog? = null
fun showAlert(
    context: Context,
    title: String? = null,
    msg: String,
    iconResId: Int? = null,
    block: (() -> Unit)? = null
) {
    val act = context as Activity
    if (!act.isFinishing && !act.isDestroyed) {
        val dialog = AlertDialog.Builder(context)
        title?.let {
            dialog.setTitle(title)
        }
        dialog.setMessage(msg)
        iconResId?.let {
            dialog.setIcon(iconResId)
        }
        dialog.setCancelable(false)
        dialog.setPositiveButton(context.getString(R.string.ok)) { d, _ ->
            block?.let {
                block()
            } ?: run {
                d.cancel()
            }
        }
        when {
            msg.isNotEmpty() -> {
                alert = dialog.create()
                dialog.show()
            }
        }

    }
}

fun showConfirmAlert(
    context: Context,
    title: String = "",
    msg: String,
    iconResId: Int? = null,
    codeBlock: ((b:Boolean) -> Unit)?
) {
    val dialog: AlertDialog.Builder = AlertDialog.Builder(context)
    with(dialog) {
        setCancelable(false)
        if (title.isNotEmpty())
            setTitle(title)
        setMessage(msg)
        iconResId?.let {
            dialog.setIcon(iconResId)
        }
        setPositiveButton(
            context.getString(R.string.ok)
        ) { _, _ ->
            codeBlock?.let { it(true) }
        }
        setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
            codeBlock?.let { it(false) }
        }
        create()
        if (msg.isNotEmpty())
            show()
    }
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun ArrayList<HashMap<String,String>>.convertToTransactionDetailList(): ArrayList<GroupMemberDetail> {
    val reportArrayList = arrayListOf<GroupMemberDetail>()
    forEach {
        GroupMemberDetail(
            it["userId"],
            it["username"],
            it["price"]
        ).also { _model ->
            reportArrayList.add(_model)
        }
    }
    return reportArrayList
}