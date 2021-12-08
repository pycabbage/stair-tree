package com.example.stairtree.ui.detail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stairtree.databinding.FragmentDetailBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

class DetailFragment : Fragment() {

    private val model by viewModels<DetailViewModel>()
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val lineChart = binding.chart
        val x = listOf<Float>(1f, 2f, 3f, 5f, 8f, 13f, 21f, 34f)//X軸データ
        val y = x.map{it*it}//Y軸データ（X軸の2乗）
        var entryList = mutableListOf<Entry>()//1本目の線
        for(i in x.indices){
            entryList.add(
                Entry(x[i], y[i])
            )
        }
        val lineDataSets = mutableListOf<ILineDataSet>()
        //②DataSetにデータ格納
        val lineDataSet = LineDataSet(entryList, "square")
        //③DataSetにフォーマット指定(3章で詳説)
        lineDataSet.color = Color.BLUE

        //リストに格納
        lineDataSets.add(lineDataSet)

        val lineData = LineData(lineDataSets)
        //⑤LineChartにLineData格納

        lineChart.data = lineData
        //⑥Chartのフォーマット指定(3章で詳説)
        //X軸の設定
        lineChart.xAxis.apply {
            isEnabled = true
            textColor = Color.BLACK
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