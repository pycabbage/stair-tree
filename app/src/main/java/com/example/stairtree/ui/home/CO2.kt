package com.example.stairtree.ui.home

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.stairtree.R
import kotlin.math.cos
import kotlin.math.sin


class CO2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var number = 2
    private var speed = 0.025
    private var count = 0.0
    private val co2Image = BitmapFactory.decodeResource(resources, R.drawable.co2)
    private val rect = Rect(0, 0, 0, 0)
    private val paint = Paint()
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val s = (sin(count) * 100).toInt()
        val c = (cos(count) * 100).toInt()
        val sc = (sin(count) * cos(count) * 100).toInt()

        if (number == 0 || number == 1 || number == 2) {
            rect.set(
                width * 2 / 4 - 50 + c,
                height * 2 / 6 + sc,
                width * 2 / 4 + 50 + c,
                height * 2 / 6 + 100 + sc
            )
            canvas?.drawBitmap(co2Image, null, rect, paint)
        }
        
        if (number == 1 || number == 2) {
            rect.set(
                width * 1 / 4 - 50 + s,
                height * 4 / 7 + c,
                width * 1 / 4 + 50 + s,
                height * 4 / 7 + 100 + c
            )
            canvas?.drawBitmap(co2Image, null, rect, paint)
        }

        if (number == 2) {
            rect.set(
                width * 3 / 4 - 50 + sc,
                height * 3 / 6 + s,
                width * 3 / 4 + 50 + sc,
                height * 3 / 6 + 100 + s
            )
            canvas?.drawBitmap(co2Image, null, rect, paint)
        }

        count += speed
        invalidate()
    }
}