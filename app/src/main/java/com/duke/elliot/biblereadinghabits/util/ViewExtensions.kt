package com.duke.elliot.biblereadinghabits.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

fun View.scaleDown(scale: Float = 0.75F, duration: Long = 200L) {
    this.animate()
        .scaleX(scale)
        .scaleY(scale)
        .alpha(0F)
        .setDuration(duration)
        .setListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animator: Animator?) {  }
            override fun onAnimationEnd(animator: Animator?) {  }
            override fun onAnimationCancel(animator: Animator?) {  }
            override fun onAnimationRepeat(animator: Animator?) {  }
        })
        .start()
}

fun View.scaleUp(scale: Float = 1F, duration: Long = 200L) {
    this.animate()
        .scaleX(scale)
        .scaleY(scale)
        .alpha(1F)
        .setDuration(duration)
        .setListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animator: Animator?) {  }
            override fun onAnimationEnd(animator: Animator?) {  }
            override fun onAnimationCancel(animator: Animator?) {  }
            override fun onAnimationRepeat(animator: Animator?) {  }
        })
        .start()
}

fun View.hideWithScaleDown(duration: Long = 200L) {
    this.animate()
        .scaleX(0.0F)
        .scaleY(0.0F)
        .alpha(0F)
        .setDuration(duration)
        .setListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animator: Animator?) {  }
            override fun onAnimationEnd(animator: Animator?) {
                this@hideWithScaleDown.visibility = View.INVISIBLE
            }
            override fun onAnimationCancel(animator: Animator?) {  }
            override fun onAnimationRepeat(animator: Animator?) {  }
        })
        .start()
}

fun View.showWithScaleUp(scale: Float = 0.75F, duration: Long = 200L) {
    this.animate()
        .scaleX(scale)
        .scaleY(scale)
        .alpha(1F)
        .setDuration(duration)
        .setListener(object: Animator.AnimatorListener{
            override fun onAnimationStart(animator: Animator?) {
                this@showWithScaleUp.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(animator: Animator?) {  }
            override fun onAnimationCancel(animator: Animator?) {  }
            override fun onAnimationRepeat(animator: Animator?) {  }
        })
        .start()
}

fun View.fadeIn(duration: Number, onAnimationEndCallback: (view: View) -> Unit) {
    this.apply {
        alpha = 0F
        visibility = View.VISIBLE

        animate()
            .alpha(1F)
            .setDuration(duration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    onAnimationEndCallback.invoke(this@fadeIn)
                }
            })
    }
}

fun View.fadeOut(duration: Number, onAnimationEndCallback: (view: View) -> Unit) {
    this.apply {
        alpha = 1F
        visibility = View.VISIBLE

        animate()
            .alpha(0F)
            .setDuration(duration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    this@fadeOut.visibility = View.GONE
                    onAnimationEndCallback.invoke(this@fadeOut)
                }
            })
    }
}