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

fun View.fadeIn(duration: Number) {
    this.apply {
        alpha = 0F
        visibility = View.VISIBLE

        animate()
            .alpha(1F)
            .setDuration(duration.toLong())
            .setListener(null)
    }
}