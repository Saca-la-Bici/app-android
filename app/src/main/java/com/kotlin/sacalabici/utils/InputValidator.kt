package com.kotlin.sacalabici.utils

// Importaciones para TextWatcher y Editable
import android.text.Editable
import android.text.TextWatcher

// Esta clase toma una función que se invoca cada vez que el texto cambia
class InputValidator(private val callback: () -> Unit) : TextWatcher {

    // Este método se llama antes de que el texto cambie
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    // Se llama mientras el texto está cambiando
    // En este caso, se notifica que el texto ha cambiado
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        callback.invoke()  // Se llama al callback
    }

    // Después de que el texto cambie
    override fun afterTextChanged(s: Editable?) {}
}
