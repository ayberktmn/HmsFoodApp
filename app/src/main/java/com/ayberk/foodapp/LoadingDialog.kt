package com.ayberk.foodapp

import android.app.AlertDialog
import androidx.fragment.app.Fragment


class LoadingDialog(val myfragment:Fragment) {
    private lateinit var isdialog:AlertDialog

    fun startLoading(){

        val inflater = myfragment.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item,null)

        val bulider = AlertDialog.Builder(myfragment.requireContext())
        bulider.setView(dialogView)
        bulider.setCancelable(false)
        isdialog = bulider.create()
        isdialog.show()
    }
    fun isDismiss(){
        isdialog.dismiss()
    }
}