package com.kronos.core.util

import android.content.Context
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import java.lang.ref.WeakReference

class ActionDialog {

    companion object {
        fun createActionDialog(context: Context, headerText: Int, bodyText: Int, mainActionText:Int,mainAction:SweetAlertDialog.OnSweetClickListener, secondaryActionText:Int, secondaryAction:SweetAlertDialog.OnSweetClickListener): SweetAlertDialog {
            var dialog = SweetAlertDialog(context, SweetAlertDialog.BUTTON_CONFIRM)
            dialog.titleText = context.getString(headerText)
            dialog.contentText = context.getString(bodyText)
            dialog.confirmText = context.getString(mainActionText)
            dialog.cancelText = context.getString(secondaryActionText)
            dialog.setConfirmClickListener(mainAction)
            dialog.setCancelClickListener(secondaryAction)
            return dialog
        }
    }
}