package net.inferno.quakereport.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

inline fun <reified T> Context.startActivity(vararg extras: Pair<String, Any>) {
    this.startActivity(Intent(this, T::class.java).apply {
        putExtras(bundleOf(*extras))
    })
}

inline fun <reified T> Context.startActivity(bundle: Bundle) {
    this.startActivity(Intent(this, T::class.java).apply {
        putExtras(bundle)
    })
}

inline fun <reified T> Activity.startActivityForResult(requestCode: Int, vararg args: Pair<String, Any>) {
    val intent = Intent(this, T::class.java)
    intent.putExtras(bundleOf(*args))
    startActivityForResult(intent, requestCode)
}

inline fun <reified T> Fragment.startActivityForResult(requestCode: Int, vararg args: Pair<String, Any>) {
    val intent = Intent(requireContext(), T::class.java)
    intent.putExtras(bundleOf(*args))
    startActivityForResult(intent, requestCode)
}

fun Intent.chooser(title: String) = Intent.createChooser(this, title)!!

fun View.hideKeyboard() {
    context.getSystemService<InputMethodManager>()!!.hideSoftInputFromWindow(windowToken, 0)
}

fun Fragment.hideKeyboard() {
    requireContext().getSystemService<InputMethodManager>()!!.hideSoftInputFromWindow(
        requireActivity().window.decorView.windowToken,
        0,
    )
}

fun ViewGroup.inflateView(@LayoutRes resource: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(resource, this, attachToRoot)!!

fun Context.hasPermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.requestPermissionsCompat(requestCode: Int, vararg permissions: String) {
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}

fun Location.isEmpty() = longitude == 0.0 && latitude == 0.0

fun Resources.toDp(number: Number) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    number.toFloat(),
    displayMetrics
)

fun Resources.getColorCompat(
    @ColorRes colorRes: Int,
    theme: Resources.Theme,
) = ResourcesCompat.getColor(this, colorRes, theme)

fun Context.toDp(number: Number) = resources.toDp(number)

inline fun consume(block: () -> Unit): Boolean {
    block()
    return true
}

var TextInputLayout.text: String
    get() = (editText?.text ?: "").toString()
    set(value) {
        editText?.setText(value)
    }
val TextInputLayout.editableText get() = editText?.text ?: ""

val Throwable.isInternetException
    get() = this::class in arrayOf(
        UnknownHostException::class,
        SocketTimeoutException::class,
        TimeoutCancellationException::class
    )

val Throwable.isCancellationException
    get() = this is CancellationException
            && this !is TimeoutCancellationException