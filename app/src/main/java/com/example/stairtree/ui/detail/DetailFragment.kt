package com.example.stairtree.ui.detail

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stairtree.databinding.FragmentDetailBinding
import com.example.stairtree.db.AppDatabase
import com.example.stairtree.db.daily.DailyDao
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//class MyXAxisValueFormatter: IndexAxisValueFormatter {
//    override fun getXValue(dateInMillisecons: String, index: Int, viewPortHandler: ViewPortHandler): String? {
//        try {
//            val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
//            return sdf.format(Date(dateInMillisecons.toLong()))
//        } catch (e: Exception) {
//            return dateInMillisecons
//        }
//    }
//}

class DetailFragment : Fragment() {

    private val model by viewModels<DetailViewModel>()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private lateinit var db: AppDatabase
    private lateinit var dailyDatabase: DailyDao

    private fun strDateToFloat(date: String): Float {
        var df: Float = 0f
        df += date.split("-")[0].toFloat() * 10000
        df += date.split("-")[1].toFloat() * 100
        df += date.split("-")[2].toFloat()
        return df
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val dataE = false  // true: elevator  false: stair
        coroutineScope.launch {
            val df = SimpleDateFormat("yyyy-MM-dd")
            db = AppDatabase.create(requireContext())
            dailyDatabase = db.daily()
            var dbdata: MutableMap<String, Double> = mutableMapOf()
            val dbbbbbb = dailyDatabase.selectAll()
            dbbbbbb.forEach {
                val _data = if (dataE) it.elevator else it.stair
                if (dbdata.containsKey(it.date)) {
                    dbdata[it.date] = _data + dbdata[it.date]!!
                } else {
                    dbdata[it.date] = _data
                }
            }
            Log.i("aaaaa", dbdata.toString())
            val lineChart = binding.chart
            var n = 0f
            val x = dbdata.keys.map {
//                df.format(it).toString()
                n = n + 1f
                n
            }
            val y = dbdata.entries.map { it.value.toFloat() }
//            Log.i("x", x.toString())
//            Log.i("y", y.toString())
            var entryList = mutableListOf<Entry>()  // 1本目の線
            for (i in x.indices) {
                entryList.add(
                    Entry(x[i], y[i])
                )
            }

            val xAxisFormatter = object : ValueFormatter() {
                var count = 0
                override fun getFormattedValue(value: Float): String {
                    // value には 0, 1, 2... という index が入ってくるので
                    // index からタイムスタンプを取得する
                    Log.i("value", value.toString())
                    if(count == dbdata.keys.size) count = 0
                    return (dbdata.keys.map{it}[count++])
                }
            }

            val lineDataSets = mutableListOf<ILineDataSet>()
            //②DataSetにデータ格納
            val lineDataSet = LineDataSet(entryList, "square")
            //③DataSetにフォーマット指定(3章で詳説)
            lineDataSet.color = Color.CYAN
            lineChart.legend.textColor = Color.CYAN

            //リストに格納
            lineDataSets.add(lineDataSet)
            val lineData = LineData(lineDataSets)

            //⑤LineChartにLineData格納
            lineChart.data = lineData

            //⑥Chartのフォーマット指定(3章で詳説)
            //X軸の設定
            lineChart.xAxis.apply {
//                isEnabled = true
                textColor = Color.GRAY
                valueFormatter = xAxisFormatter
            }

            lineChart.axisLeft.apply {
                textColor = Color.GRAY
            }

            lineChart.axisRight.apply {
                textColor = Color.GRAY
            }

            //⑦linechart更新
            lineChart.invalidate()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}