package ru.mikhailskiy.intensiv.ui.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView


    fun showLoadingIndicator(show: Boolean, loadingIndicator: ImageView) {

        if (show) {
            loadingIndicator!!.visibility = View.VISIBLE
            loadingIndicator!!.animate().setInterpolator(AccelerateDecelerateInterpolator())
                .rotationBy(360f).setDuration(500).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        loadingIndicator!!.animate()
                            .setInterpolator(AccelerateDecelerateInterpolator())
                            .rotationBy(360f).setDuration(500).setListener(this)
                    }
                })
        } else {
            loadingIndicator!!.animate().cancel()
            loadingIndicator!!.visibility = View.GONE
        }
    }
