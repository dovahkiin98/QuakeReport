package net.inferno.quakereport.extension

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

//region Base
private fun View.showSnackbar(
    @BaseTransientBottomBar.Duration duration: Int,
    message: CharSequence,
    actionMessage: CharSequence = "",
    action: (() -> Unit)? = null,
) = Snackbar.make(this, message, duration).apply {
    if (action != null) setAction(actionMessage) { action() }
    show()
}

private fun View.showSnackbar(
    @BaseTransientBottomBar.Duration duration: Int,
    @StringRes messageRes: Int,
    @StringRes actionMessageRes: Int = 0,
    action: (() -> Unit)? = null,
) = showSnackbar(
    duration,
    resources.getText(messageRes),
    resources.getText(actionMessageRes),
    action,
)
//endregion

//region Short Snackbar
fun View.showShortSnackbar(
    @StringRes messageRes: Int,
    @StringRes actionMessageRes: Int = 0,
    action: (() -> Unit)? = null,
) = showSnackbar(Snackbar.LENGTH_SHORT, messageRes, actionMessageRes, action)

fun View.showShortSnackbar(
    message: CharSequence,
    actionMessage: CharSequence = "",
    action: (() -> Unit)? = null,
) = showSnackbar(Snackbar.LENGTH_SHORT, message, actionMessage, action)
//endregion

//region Long Snackbar
fun View.showLongSnackbar(
    @StringRes messageRes: Int,
    @StringRes actionMessageRes: Int = 0,
    action: (() -> Unit)? = null,
) = showSnackbar(Snackbar.LENGTH_LONG, messageRes, actionMessageRes, action)

fun View.showLongSnackbar(
    message: CharSequence,
    actionMessage: CharSequence = "",
    action: (() -> Unit)? = null,
) = showSnackbar(Snackbar.LENGTH_LONG, message, actionMessage, action)
//endregion

//region Indefinite Snackbar
fun View.showIndefiniteSnackbar(
    @StringRes messageRes: Int,
    @StringRes actionMessageRes: Int = 0,
    action: (() -> Unit)? = null,
) = showSnackbar(Snackbar.LENGTH_INDEFINITE, messageRes, actionMessageRes, action)

fun View.showIndefiniteSnackbar(
    message: CharSequence,
    actionMessage: CharSequence = "",
    action: (() -> Unit)? = null,
) = showSnackbar(Snackbar.LENGTH_INDEFINITE, message, actionMessage, action)
//endregion