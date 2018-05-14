package com.custom_view.mycustomview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.support.v4.content.res.TypedArrayUtils
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by WuKai on 2018/5/14.
 *
 * email:wukai@caiqr.com
 */
class CustomProgress : View {
    /**
     * 当前进度的长度
     */
    private var progressWidth: Float = 0.toFloat()

    /**
     * 进度动画
     */
    private var progressAnimator: ValueAnimator? = null

    /**
     * 动画执行时间
     */
    private val duration = 1000
    /**
     * 动画延时启动时间
     */
    private val startDelay = 500

    private val lineRectF = RectF()
    private val bgRectF = RectF()
    private val progressRectF = RectF()

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    //圆角的大小
    private var roundRectRadius: Int = 0
    //进度条背景的画笔
    private lateinit var progressBackgroundPaint: Paint
    //进度条背景颜色
    private var progressBackgroundColor: Int = 0Xffffff

    //进度条的高度
    private var progressHeight: Int = 0

    //进度条的画笔
    private lateinit var progressPaint: Paint

    //边框线的画笔
    private lateinit var linePaint: Paint
    //最外层边框线的高度
    private var startLineHeight: Int = 0

    //最外层边框线背景颜色
    private var startLineColor: Int = 0Xffffff

    //中间层边框线的高度
    private var centerLineHeight: Int = 0

    //中间层边框线背景颜色
    private var centerLineColor: Int = 0Xffffff

    //内层边框线的高度
    private var endLineHeight: Int = 0

    //内层边框线背景颜色
    private var endLineColor: Int = 0Xffffff

    //该view的高度
    private var mViewHeight: Int = 0

    //进度条或者进度条背景画笔线的高度
    private var progressPaintWidth = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(context, attrs, defStyleAttr)
        initData()
        initPaint()
    }

    /**
     * 自定义属性
     */
    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomProgress, defStyleAttr, 0)
        progressHeight = typedArray.getDimensionPixelSize(R.styleable.CustomProgress_progress_height, 0)
        startLineHeight = typedArray.getDimensionPixelSize(R.styleable.CustomProgress_start_line_height, dp2px(1))
        centerLineHeight = typedArray.getDimensionPixelSize(R.styleable.CustomProgress_center_line_height, dp2px(1))
        endLineHeight = typedArray.getDimensionPixelSize(R.styleable.CustomProgress_end_line_height, dp2px(1))
        roundRectRadius = typedArray.getDimensionPixelSize(R.styleable.CustomProgress_round_radius, dp2px(20))
        startLineColor = typedArray.getColor(R.styleable.CustomProgress_start_line_color, Color.parseColor("#FF877B"))
        centerLineColor = typedArray.getColor(R.styleable.CustomProgress_center_line_color, Color.parseColor("#FFC9C3"))
        endLineColor = typedArray.getColor(R.styleable.CustomProgress_end_line_color, Color.parseColor("#D35D41"))
        progressBackgroundColor = typedArray.getColor(R.styleable.CustomProgress_progress_background_color, Color.parseColor("#F66847"))
        typedArray.recycle()

    }

    private fun initData() {
        progressPaintWidth = dp2px(1)
        mViewHeight = progressHeight + progressPaintWidth * 5 + startLineHeight * 4 + centerLineHeight * 2 + endLineHeight * 2
    }

    /**
     * 初始化相关画笔
     */
    private fun initPaint() {
        progressBackgroundPaint = getPaint(progressPaintWidth, Paint.Style.FILL, progressBackgroundColor)
        progressPaint = getPaint(progressPaintWidth, Paint.Style.FILL, progressBackgroundColor)
        linePaint = getPaint(startLineHeight, Paint.Style.STROKE, startLineColor)
    }

    /**
     * 创建画笔
     */
    private fun getPaint(strokeWidth: Int, paintStyle: Paint.Style, color: Int): Paint {
        val paint = Paint()
        paint.strokeWidth = strokeWidth.toFloat()
        paint.style = paintStyle
        paint.color = color
        paint.isAntiAlias = true
        return paint
    }

    // 将dip或dp值转换为px值
    fun dp2px(dipValue: Int): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(measureWidth(widthMode, width), measureHeight(heightMode, height))
    }

    /**
     * 测量宽度
     *
     * @param mode
     * @param width
     * @return
     */
    private fun measureWidth(mode: Int, width: Int): Int {
        when (mode) {
            View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST -> {
            }
            View.MeasureSpec.EXACTLY -> mWidth = width
        }
        return mWidth
    }

    /**
     * 测量高度
     *
     * @param mode
     * @param height
     * @return
     */
    private fun measureHeight(mode: Int, height: Int): Int {
        when (mode) {
            View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST -> mHeight = mViewHeight
            View.MeasureSpec.EXACTLY -> mHeight = height
        }
        return mHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawLine(canvas)

        //背景
        drawBgProgress(canvas)
        //进度条
        drawProgress(canvas)
    }

    private fun drawLine(canvas: Canvas) {
        progressBackgroundPaint.color = startLineColor
        bgRectF.left = 0f
        bgRectF.top = 0f
        bgRectF.right = this.measuredWidth.toFloat()
        bgRectF.bottom = bgRectF.top + progressHeight.toFloat() + startLineHeight.toFloat() + centerLineHeight.toFloat() + endLineHeight.toFloat()
        canvas.drawRoundRect(bgRectF, roundRectRadius.toFloat(), roundRectRadius.toFloat(), progressBackgroundPaint)

        linePaint.color = centerLineColor
        linePaint.strokeWidth = centerLineHeight.toFloat()
        lineRectF.left = (startLineHeight * 2).toFloat()
        lineRectF.top = (startLineHeight * 2).toFloat()
        lineRectF.right = (this.measuredWidth - startLineHeight * 2).toFloat()
        lineRectF.bottom = bgRectF.top + progressHeight.toFloat() + centerLineHeight.toFloat() + endLineHeight.toFloat() - startLineHeight
        canvas.drawRoundRect(lineRectF, roundRectRadius.toFloat(), roundRectRadius.toFloat(), linePaint)

        linePaint.color = endLineColor
        linePaint.strokeWidth = endLineHeight.toFloat()
        lineRectF.left = (startLineHeight * 2 + centerLineHeight).toFloat()
        lineRectF.top = (startLineHeight * 2 + centerLineHeight).toFloat()
        lineRectF.right = (this.measuredWidth - startLineHeight * 2 - centerLineHeight).toFloat()
        lineRectF.bottom = bgRectF.top + progressHeight.toFloat() + endLineHeight.toFloat() - startLineHeight
        canvas.drawRoundRect(lineRectF, roundRectRadius.toFloat(), roundRectRadius.toFloat(), linePaint)
    }

    private fun drawBgProgress(canvas: Canvas) {
        progressBackgroundPaint.color = progressBackgroundColor
        bgRectF.left = startLineHeight * 1.5f + centerLineHeight.toFloat() + endLineHeight.toFloat()
        bgRectF.top = startLineHeight * 1.5f + centerLineHeight.toFloat() + endLineHeight.toFloat()
        bgRectF.right = this.measuredWidth.toFloat() - startLineHeight * 1.5f - centerLineHeight.toFloat() - endLineHeight.toFloat()
        bgRectF.bottom = (progressHeight - startLineHeight / 2).toFloat()
        canvas.drawRoundRect(bgRectF, roundRectRadius.toFloat(), roundRectRadius.toFloat(), progressBackgroundPaint)
    }

    private fun drawProgress(canvas: Canvas) {
        progressRectF.left = startLineHeight * 1.5f + centerLineHeight.toFloat() + endLineHeight.toFloat()
        progressRectF.top = startLineHeight * 1.5f + centerLineHeight.toFloat() + endLineHeight.toFloat()
        progressRectF.right = progressWidth - startLineHeight * 1.5f - centerLineHeight.toFloat() - endLineHeight.toFloat()
        progressRectF.bottom = (progressHeight - startLineHeight / 2).toFloat()
        val colors = intArrayOf(Color.parseColor("#FFF29F"), Color.parseColor("#FEC41C"), Color.parseColor("#EB7C09"))
        val shader = LinearGradient(progressRectF.left, progressRectF.top, progressRectF.left, progressRectF.bottom, colors, null, Shader.TileMode.REPEAT)
        progressPaint.shader = shader

        canvas.drawRoundRect(progressRectF, roundRectRadius.toFloat(), roundRectRadius.toFloat(), progressPaint)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.taotao_load_frame01)
        progressRectF.left = startLineHeight * 3.5f + centerLineHeight.toFloat() + endLineHeight.toFloat()
        progressRectF.top = startLineHeight * 1.5f + centerLineHeight.toFloat() + endLineHeight.toFloat()
        progressRectF.right = this.measuredWidth.toFloat() - startLineHeight * 3.5f - centerLineHeight.toFloat() - endLineHeight.toFloat()
        progressRectF.bottom = (progressHeight / 2).toFloat()
        canvas.drawBitmap(bitmap, null, progressRectF, progressPaint)
    }

    /**
     * 进度
     */
    private var mProgress: Float = 0f
    //当前进度
    private var currentProgress: Float = 0f

    fun setProgress(progress: Float): CustomProgress {
        currentProgress = mProgress
        mProgress = progress
        initAnimation()
        return this
    }

    /**
     * 进度移动动画  通过插值的方式改变移动的距离
     */
    private fun initAnimation() {
        progressAnimator = ValueAnimator.ofFloat(currentProgress, mProgress)
        progressAnimator?.run {
            duration = duration
            startDelay = startDelay
            interpolator = LinearInterpolator()
            addUpdateListener({ valueAnimator ->
                val value = valueAnimator.animatedValue as Float
                progressWidth = value * mWidth / 100
                invalidate()
            })
            if (!isStarted) {
                progressAnimator?.start()
            }
        }

    }

}