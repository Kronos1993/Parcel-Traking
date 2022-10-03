package com.kronos.core.util

import android.content.Context
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog

class LoadingDialog {
    companion object {
        var progressDialog: SweetAlertDialog? = null
        fun getProgressDialog(context: Context, text: Int, progressColor: Int): SweetAlertDialog? {
            if (progressDialog == null) {
                progressDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                progressDialog!!.progressHelper.barColor =
                    ContextCompat.getColor(context, progressColor)
                progressDialog!!.setTitle(text)
                progressDialog!!.setCancelable(false)
            }
            return progressDialog
        }
    }
}