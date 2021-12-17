package com.example.stairtree.ui.home

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.stairtree.R
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random


class CO2 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var number = 5
    var size = 60
    private var speed = 0.025
    private var count = 0.0
    private val co2Image = BitmapFactory.decodeResource(resources, R.drawable.co2)
    private val paint = Paint().apply {
        color = Color.WHITE
    }
    private val co2 = mutableListOf<CO2Movement>()

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        while (true) {
            if (co2.size < number) {
                co2.add(0, CO2Movement())
            } else if (co2.size > number) {
                co2.removeAt(0)
            } else {
                break
            }
        }

        co2.forEach {
            val rect = it.setRect(width, height, count, size)
            canvas?.drawBitmap(co2Image, null, rect, paint)
        }

        val rect = Rect(0, height - 1, width, height)
        canvas?.drawRect(rect, paint)

        count += speed
        invalidate()
    }
}

class CO2Movement {
    private val randomSCX = Random.nextInt() % 5
    private val randomSCY = Random.nextInt() % 5
    private val randomX = abs(Random.nextInt())
    private val randomY = abs(Random.nextInt())
    private val rect = Rect(0, 0, 0, 0)

    private fun sc(count: Double, sc: Int): Int {
        return when (sc) {
            0 -> (sin(count) * 100).toInt()
            1 -> (cos(count) * 100).toInt()
            2 -> (sin(count) + cos(count) * 100).toInt()
            3 -> (sin(count) * sin(count) * 100).toInt()
            4 -> (cos(count) * cos(count) * 100).toInt()
            else -> (sin(count) * cos(count) * 100).toInt()
        }
    }

    fun setRect(width: Int, height: Int, count: Double, CO2SIZE: Int): Rect {
        rect.set(
            randomX % width + sc(count, randomSCX) - CO2SIZE / 2,
            randomY % height + sc(count, randomSCY) - CO2SIZE / 2,
            randomX % width + sc(count, randomSCX) + CO2SIZE / 2,
            randomY % height + sc(count, randomSCY) + CO2SIZE / 2,
        )
        return rect
    }
}