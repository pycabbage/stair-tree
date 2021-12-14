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
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class DetailFragment : Fragment() {

    private val model by viewModels<DetailViewModel>()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private lateinit var db: AppDatabase
    private lateinit var dailyDatabase: DailyDao
    private val listDays = 7

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        val lineChart = binding.chart
        coroutineScope.launch {
            val date = Date()
            val calendar = Calendar.getInstance()
            var month = mutableListOf<String>()
            calendar.time = date
            for (i in 0..listDays) {
                month.add(
                    calendar.get(Calendar.YEAR)
                        .toString() + "-" + (calendar.get(Calendar.MONTH) + 1).toString() + "-" + calendar.get(
                        Calendar.DATE
                    ).toString()
                )
                calendar.add(Calendar.DATE, -1)
            }
            month.reverse()

            db = AppDatabase.create(requireContext())
            dailyDatabase = db.daily()
            val stairDBData: MutableMap<String, Double> = mutableMapOf()
            val elevatorDBData: MutableMap<String, Double> = mutableMapOf()

            dailyDatabase.selectAll().forEach {
                if (stairDBData.containsKey(it.date)) {
                    stairDBData[it.date] = it.stair / 1000F + stairDBData[it.date]!!
                    elevatorDBData[it.date] = it.elevator / 1000F + elevatorDBData[it.date]!!
                } else {
                    stairDBData[it.date] = it.stair / 1000F
                    elevatorDBData[it.date] = it.elevator / 1000F
                }
            }

            val x: List<Float> = (0..listDays).toList().map { it.toFloat() }
            val stairY = (0..listDays).toList().map {
                if (stairDBData.containsKey(month[it])) {
                    stairDBData[month[it]]!!.toFloat()
                } else {
                    0F
                }
            }
            val elevatorY = (0..listDays).toList().map {
                if (elevatorDBData.containsKey(month[it])) {
                    elevatorDBData[month[it]]!!.toFloat()
                } else {
                    0F
                }
            }

            val stairEntryList: MutableList<Entry> = mutableListOf()  // 階段の線
            val elevatorEntryList: MutableList<Entry> = mutableListOf()  // エレベータの線
            for (i in x.indices) {
                stairEntryList.add(Entry(x[i], stairY[i]))
            }
            for (i in x.indices) {
                elevatorEntryList.add(Entry(x[i], elevatorY[i]))
            }

            val xAxisFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return month[value.toInt()].substring(5)
                }
            }

            val lineDataSets = mutableListOf<ILineDataSet>()

            //②DataSetにデータ格納
            val stairLineDataSet = LineDataSet(stairEntryList, "階段使用量")
            val elevatorLineDataSet = LineDataSet(elevatorEntryList, "エレベーター使用量")

            //③DataSetにフォーマット指定(3章で詳説)
            stairLineDataSet.color = Color.CYAN
            elevatorLineDataSet.color = Color.MAGENTA
            lineChart.legend.textColor = Color.GRAY

            //リストに格納
            lineDataSets.add(stairLineDataSet)
            lineDataSets.add(elevatorLineDataSet)
            val lineData = LineData(lineDataSets)

            //⑤LineChartにLineData格納
            lineChart.data = lineData

            //⑥Chartのフォーマット指定(3章で詳説)
            //X軸の設定
            lineChart.xAxis.apply {
                isEnabled = true
                textColor = Color.GRAY
                valueFormatter = xAxisFormatter
            }

            lineChart.axisLeft.apply { textColor = Color.GRAY }
            lineChart.axisRight.apply { textColor = Color.GRAY }

            //⑦lineChart更新
            lineChart.invalidate()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}