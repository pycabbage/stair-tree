package com.example.stairtree.background

import kotlin.math.pow

class UpAndDownJudgment(_regressionLength: Int) {
    private val regressionLength = _regressionLength
    var data = DoubleArray(_regressionLength)
    var time = DoubleArray(_regressionLength)
    var count = 0;
    fun push(value: Double) {
        val period = 0.2
        if (count < regressionLength) {
            data[count] = value
            time[count] = (count + 1) * period
            count++

        } else {
            for (i in 0 until (regressionLength - 1)) {
                data[i] = data[i + 1]
            }
            data[regressionLength - 1] = value
        }
    }

    fun possibleToJudge(): Boolean {
        return count >= regressionLength

    }

    fun sloop(): Double {
        val dataAvarage = data.average()
        val timeAverage = time.average()
        val timeSquaredAverage = time.map { it * it }.average()
        val timeDataAverage =
            data.mapIndexed { index, i -> i * time[index] }.average()
        val slope =
            (timeDataAverage - timeAverage * dataAvarage) / (timeSquaredAverage - timeAverage.pow(2))
        return slope
    }

    fun state() {

    }
}