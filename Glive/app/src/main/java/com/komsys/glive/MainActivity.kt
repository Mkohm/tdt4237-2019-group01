package com.komsys.glive

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.Description
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


        val dataObjects = listOf(Pair(1f, 1f), Pair(2f, 3f), Pair(2f, 3f), Pair(4f, 3f))

        val entries = ArrayList<Entry>()

        for (dataObject in dataObjects) {

            entries.add(Entry(dataObject.first, dataObject.second));

        }

        val dataSet = LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.CYAN)


        dataSet.setHighlightEnabled(true); // allow highlighting for DataSet

        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.RED
        dataSet.setDrawHighlightIndicators(true)

        dataSet.getEntriesForXValue(2f)
        //chart.highlightValue(2f, 1)
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.xAxis.setDrawAxisLine(false)
        chart.axisLeft.setDrawAxisLine(false)
        chart.axisRight.setDrawAxisLine(false)

        val lineData = LineData(dataSet)
        chart.data = lineData


        val description = Description()
        description.text = "Number of people at gym"


        chart.setDrawGridBackground(false)
        chart.description = description


        chart.invalidate() // refresh
    }

}
