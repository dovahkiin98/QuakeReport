package net.inferno.quakereport.extension

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

//region Base
fun Context.showToast(message: CharSequence, duration: Int) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(@StringRes messageRes: Int, duration: Int) {
   showToast(getText(messageRes), duration)
}
//endregion

//region Short Toast
fun Context.showShortToast(@StringRes message: Int) = showShortToast(getString(message))

fun Context.showShortToast(message: String) {
    showToast(message, Toast.LENGTH_SHORT)
}

fun Fragment.showShortToast(@StringRes message: Int) = showShortToast(getString(message))

fun Fragment.showShortToast(message: String) {
    requireContext().showToast(message, Toast.LENGTH_LONG)
}
//endregion

//region Long Toast
fun Context.showLongToast(@StringRes message: Int) = showLongToast(getString(message))

fun Context.showLongToast(message: String) {
    showToast(message, Toast.LENGTH_LONG)
}

fun Fragment.showLongToast(@StringRes message: Int) = showLongToast(getString(message))

fun Fragment.showLongToast(message: String) {
    requireContext().showToast(message, Toast.LENGTH_LONG)
}
//endregion