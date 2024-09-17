package com.kotlin.sacalabici.utils

import android.text.Editable
import android.text.TextWatcher

class InputValidator(private val callback: () -> Unit) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        callback.invoke()
    }

    override fun afterTextChanged(s: Editable?) {}
}
