package com.paiwaddev.testwsapp.view

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.paiwaddev.testwsapp.R

class AlertMessage(private val context: Context, private val body: String, private val positText: String, private val nagatText: String) {

    private lateinit var listener: ItemListener

    init {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(body)
            //.setCancelable(false)
            .setPositiveButton(positText) { dialog, _ ->
                dialog.dismiss()
                this.listener.onItemClicked(POSITIVE)
            }
            .setNegativeButton(nagatText) { dialog, _ ->
                dialog.cancel()
                this.listener.onItemClicked(NAGATIVE)
            }
        val alert = dialogBuilder.create()
        alert.setTitle("แจ้งเตือน")
        alert.show()
    }

    interface ItemListener {
        fun onItemClicked(IsAction: Int)
    }

    fun setListener(itemListener: ItemListener) {
        this.listener = itemListener;
    }

    companion object{
        const val POSITIVE = 1
        const val NAGATIVE = 0
    }
}