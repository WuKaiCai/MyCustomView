package com.custom_view.mycustomview

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

/**
 * Created by WuKai on 2018/5/14.
 *
 * email:wukai@caiqr.com
 */
class LoadingDots : LinearLayout {
    private var jumpHeight: Int = 0
    private var autoPlay: Boolean = false
    private var period: Int = 0
    private val mAnimatorSet = AnimatorSet()

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        Handler(Looper.getMainLooper())
        //自定义属性
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaitingDots)
            period = typedArray.getInt(R.styleable.WaitingDots_period, 1000)
            jumpHeight = typedArray.getDimensionPixelSize(R.styleable.WaitingDots_jumpHeight, 0)
            autoPlay = typedArray.getBoolean(R.styleable.WaitingDots_autoPlay, true)
            typedArray.recycle()
        }
        orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.BOTTOM
        layoutParams.rightMargin = -dp2px(3)
        val dotOne = ImageView(context)
        dotOne.setBackgroundResource(R.drawable.taotao_loadingd_pic)
        dotOne.layoutParams = layoutParams
        val layoutParams1 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams1.gravity = Gravity.BOTTOM
        layoutParams1.rightMargin = -dp2px(3)
        val dotTwo = ImageView(context)
        dotTwo.setBackgroundResource(R.drawable.taotao_loadingd_pic)
        dotTwo.layoutParams = layoutParams1
        val layoutParams2 = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams2.gravity = Gravity.BOTTOM
        val dotThree = ImageView(context)
        dotThree.setBackgroundResource(R.drawable.taotao_loadingd_pic)
        dotThree.layoutParams = layoutParams2
        addView(dotOne)
        addView(dotTwo)
        addView(dotThree)
        // 一下两个是把updateListener加到点1上，通过它来进行刷新动作
        val dotOneJumpAnimator = createDotJumpAnimator(dotOne, 0)
        dotOneJumpAnimator.addUpdateListener { invalidate() }
        // 这里通过animationSet来控制三个点的组合动作
        mAnimatorSet.playTogether(dotOneJumpAnimator, createDotJumpAnimator(dotTwo,
                (period / 6).toLong()), createDotJumpAnimator(dotThree, (period * 2 / 6).toLong()))

        if (autoPlay) {
            start()
        }
    }

    fun start() {
        //一旦开始就INFINITE
        setAllAnimationsRepeatCount(ValueAnimator.INFINITE)
        mAnimatorSet.start()
    }

    /**
     * 动画的实现核心
     *
     * @param imageView 传入点
     * @delay 动画运行延迟，通过这个参数让三个点进行有时差的运动
     */
    private fun createDotJumpAnimator(imageView: ImageView, delay: Long): ObjectAnimator {
        val jumpAnimator = ObjectAnimator.ofFloat(imageView, "translationY", 0f, -jumpHeight.toFloat())
        jumpAnimator.setEvaluator(TypeEvaluator<Number> { fraction, from, to -> Math.max(0.0, Math.sin(fraction.toDouble() * Math.PI * 2.0)) * (to.toFloat() - from.toFloat()) })
        jumpAnimator.duration = period.toLong()
        jumpAnimator.startDelay = delay
        jumpAnimator.repeatCount = ValueAnimator.INFINITE
        jumpAnimator.repeatMode = ValueAnimator.RESTART
        return jumpAnimator
    }

    private fun setAllAnimationsRepeatCount(repeatCount: Int) {
        mAnimatorSet.childAnimations
                .filterIsInstance<ObjectAnimator>()
                .forEach { it.repeatCount = repeatCount }
    }

    // 将dip或dp值转换为px值
    fun dp2px(dipValue: Int): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
}