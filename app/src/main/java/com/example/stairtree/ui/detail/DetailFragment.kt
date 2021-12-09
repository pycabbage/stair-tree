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

        coroutineScope.launch {
            db = AppDatabase.create(requireContext())
            dailyDatabase = db.daily()
            for (i in dailyDatabase.selectStairAndEle()) {
                Log.i("aaa", i.toString())
            }
        }

        val lineChart = binding.chart
        val x = listOf<Float>(1f, 2f, 3f, 5f, 8f, 13f, 21f, 34f)//X軸データ
        val y = x.map { it * it }//Y軸データ（X軸の2乗）
        var entryList = mutableListOf<Entry>()//1本目の線
        for (i in x.indices) {
            entryList.add(
                Entry(x[i], y[i])
            )
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
            isEnabled = true
            textColor = Color.GRAY
        }

        lineChart.axisLeft.apply {
            textColor = Color.GRAY
        }

        lineChart.axisRight.apply {
            textColor = Color.GRAY
        }

        //⑦linechart更新
        lineChart.invalidate()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}