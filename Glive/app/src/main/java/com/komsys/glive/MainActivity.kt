package com.komsys.glive

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.FirebaseApp
import com.komsys.glive.ui.main.MainViewModel
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        FirebaseApp.initializeApp(this)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.currentNumberOfUsersHistory.observe(this, Observer {
            val entries = ArrayList<Entry>()


            for (dataObject in it) {
                entries.add(Entry(dataObject.timestamp, dataObject.numberOfUsers.toFloat()))
            }


            val dataSet = LineDataSet(entries, "Label") // add entries to dataset
            dataSet.setDrawValues(false)

            dataSet.setDrawFilled(true)


            chart.xAxis.axisMinimum = 0.0f
            chart.xAxis.mAxisMaximum = 24.0f
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM





            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.setDrawAxisLine(false)
            chart.xAxis.setDrawLabels(true)
            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.setDrawLabels(true)
            chart.axisLeft.setDrawAxisLine(false)
            chart.axisRight.setDrawLabels(false)
            chart.axisRight.setDrawAxisLine(false)
            chart.axisRight.setDrawGridLines(false)


            val lineData = LineData(dataSet)
            chart.data = lineData



            chart.setDrawGridBackground(false)
            chart.legend.isEnabled = false   // Hide the legend

            chart.description.isEnabled = false

            chart.invalidate() // refresh
        })



        viewModel.currentNumberOfGymMembers.observe(this, Observer {
            currentNumberOfUsers.text = it.toString()
        })
    }

}
