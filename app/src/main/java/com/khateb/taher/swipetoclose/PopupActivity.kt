package com.khateb.taher.swipetoclose

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Point
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_popup.*

/**
 * Created by Rami on 1/27/2018.
 */

class PopupActivity : Activity(), View.OnTouchListener {

    private var previousFingerPosition = 0
    private var baseLayoutPosition = 0
    private var defaultViewHeight: Int = 0

    private var isClosing = false
    private var isScrollingUp = false
    private var isScrollingDown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popup)
        base_popup_layout.setOnTouchListener(this)
    }


    override fun onTouch(view: View, event: MotionEvent): Boolean {
        val Y = event.rawY.toInt()
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                defaultViewHeight = base_popup_layout.height
                previousFingerPosition = Y
                baseLayoutPosition = base_popup_layout.y.toInt()
            }

            MotionEvent.ACTION_UP -> {
                if (isScrollingUp) {
                    base_popup_layout.y = 0f
                    isScrollingUp = false
                }

                if (isScrollingDown) {
                    base_popup_layout.y = 0f
                    base_popup_layout.layoutParams.height = defaultViewHeight
                    base_popup_layout.requestLayout()
                    isScrollingDown = false
                }
            }
            MotionEvent.ACTION_MOVE -> if (!isClosing) {
                val currentYPosition = base_popup_layout.y.toInt()
                if (previousFingerPosition > Y) {
                    if (!isScrollingUp) {
                        isScrollingUp = true
                    }

                    if (base_popup_layout.height < defaultViewHeight) {
                        base_popup_layout.layoutParams.height = base_popup_layout.height - (Y - previousFingerPosition)
                        base_popup_layout.requestLayout()
                    } else {
                        if (baseLayoutPosition - currentYPosition > defaultViewHeight / 4) {
                            closeUpAndDismissDialog(currentYPosition)
                            return true
                        }
                    }
                    base_popup_layout.y = base_popup_layout.y + (Y - previousFingerPosition)

                } else {
                    if (!isScrollingDown) {
                        isScrollingDown = true
                    }

                    if (Math.abs(baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
                        closeDownAndDismissDialog(currentYPosition)
                        return true
                    }

                    base_popup_layout.y = base_popup_layout.y + (Y - previousFingerPosition)
                    base_popup_layout.layoutParams.height = base_popup_layout.height - (Y - previousFingerPosition)
                    base_popup_layout.requestLayout()
                }
                previousFingerPosition = Y
            }
        }
        return true
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun closeUpAndDismissDialog(currentPosition: Int) {
        isClosing = true
        val positionAnimator = ObjectAnimator.ofFloat(base_popup_layout,
                "translationY",
                currentPosition.toFloat(),
                -base_popup_layout.height.toFloat())
        positionAnimator.duration = 300
        positionAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                finish()
            }
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        positionAnimator.start()
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun closeDownAndDismissDialog(currentPosition: Int) {
        isClosing = true
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val screenHeight = size.y
        val positionAnimator = ObjectAnimator.ofFloat(base_popup_layout,
                "translationY",
                currentPosition.toFloat(),
                screenHeight + base_popup_layout.height.toFloat())
        positionAnimator.duration = 300
        positionAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                finish()
            }
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        positionAnimator.start()
    }
}