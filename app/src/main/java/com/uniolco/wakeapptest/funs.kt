package com.uniolco.wakeapptest

import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat

fun shareResult(result: String, view: View) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "I have $result in this game. Try to beat me in Joker Game!")
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    ContextCompat.startActivity(view.context, shareIntent, null)
}
