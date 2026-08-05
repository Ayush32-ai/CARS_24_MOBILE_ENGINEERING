package com.example.cars24sdui.actions

import android.content.Context
import android.widget.Toast

class ActionHandler(private val context: Context) {
    fun dispatch(action: String) {
        Toast.makeText(context, when (action) {
            "Call now" -> "An expert will call you shortly"
            else -> "$action selected"
        }, Toast.LENGTH_SHORT).show()
    }
}
