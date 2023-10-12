package com.ayberk.foodapp

import android.app.AlertDialog
import androidx.fragment.app.Fragment


class LoadingDialog(val myfragment: Fragment) {
    private lateinit var isdialog: AlertDialog

    fun startLoading() {
        val inflater = myfragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item, null)

        // Stili burada özelleştirin.
        val builder = AlertDialog.Builder(myfragment.requireContext(), R.style.TransparentAlertDialog)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isdialog = builder.create()
        isdialog.show()
    }

    fun isDismiss() {
        isdialog.dismiss()
    }
}
