#!/usr/bin/env sh

sed -i 's/AppCompatDelegate\.setDefaultNightMode/\/\/AppCompatDelegate.setDefaultNightMode/' app/src/main/java/com/example/stairtree/MainActivity.kt
sed -i 's/lineChart.invalidate/lineChart.legend.textColor = Color.GRAY\n        lineChart.description.textColor = Color.GRAY\n        lineChart.xAxis.textColor = Color.GRAY\n        lineChart.axisRight.textColor = Color.GRAY\n        lineChart.invalidate/' app/src/main/java/com/example/stairtree/ui/detail/DetailFragment.kt
