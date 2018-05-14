package com.custom_view.mycustomview

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getScreenScaleHeight(750, 1334))
        loading_bg.layoutParams = layoutParams
        (taotao_icon.layoutParams as FrameLayout.LayoutParams).topMargin = getScreenHeight() * 350 / 1334

        progress.setProgress(50F)
    }


    /**
     * 按照宽高比计算控件高度
     *
     * @param scaleWidth  宽度比值
     * @param scaleHeight 高度比值
     */
    fun getScreenScaleHeight(scaleWidth: Int, scaleHeight: Int): Int {
        return getScreenWidth() * scaleHeight / scaleWidth
    }

    /**
     * 获得屏幕宽度
     *
     */
    fun getScreenWidth(): Int {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    /**
     * 获得屏幕高度
     *
     */
    fun getScreenHeight(): Int {
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }
}
