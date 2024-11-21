package com.stopstone.musicplaylist.util

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.stopstone.musicplaylist.R

fun ImageView.loadImage(url: String) {
    Glide.with(context)
        .load(url)
        .centerCrop()
        .error(R.drawable.background_button_gray_200)
        .into(this)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT
) = Snackbar.make(this, message, duration)

fun View.showSnackBarWithNavigation(
    message: String,
    navigationView: View,
    duration: Int = Snackbar.LENGTH_SHORT
) = showSnackBar(message, duration)
    .apply {
        setAnchorView(navigationView)
    }.show()