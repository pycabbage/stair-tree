package com.example.stairtree.ui.detail

import android.graphics.Color
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

class DetailFragment : Fragment() {

    private val model by viewModels<DetailViewModel>()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private lateinit var db: AppDatabase
    private lateinit var dailyDatabase: DailyDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val toggle = binding.toggleButton
        val lineChart = binding.chart
        val dataE = false  // true: elevator  false: stair
//        val toggle = binding.toggleButton
        toggle.setOnCheckedChangeListener { _, isChecked ->
            coroutineScope.launch {
                db = AppDatabase.create(requireContext())
                dailyDatabase = db.daily()
                val dbData: MutableMap<String, Double> = mutableMapOf()
                val dbAll = dailyDatabase.selectAll()
                dbAll.forEach {
                    val data = if (isChecked) it.elevator else it.stair
                    if (dbData.containsKey(it.date)) {
                        dbData[it.date] = data + dbData[it.date]!!
                    } else {
                        dbData[it.date] = data
                    }
                }
                var n = 0f
                val x = dbData.keys.map {
                    n += 1f
                    n
                }
                val y = dbData.entries.map { it.value.toFloat() }
//            Log.i("x", x.toString())
//            Log.i("y", y.toString())
                val entryList: MutableList<Entry> = mutableListOf()  // 1本目の線
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
                        if (count == dbData.keys.size) count = 0
                        return (dbData.keys.map { it }[count++])
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

                //⑦lineChart更新
                lineChart.invalidate()
            }
        }
        toggle.toggle()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}