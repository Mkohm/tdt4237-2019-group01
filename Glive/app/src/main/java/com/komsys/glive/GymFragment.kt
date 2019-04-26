package com.komsys.glive

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.komsys.glive.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_gym.*

class GymFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gym, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.currentNumberOfUsersHistory.observe(this, Observer {
            val entries = ArrayList<Entry>()


            for (dataObject in it) {
                entries.add(Entry(dataObject.timestamp, dataObject.numberOfUsers.toFloat()))
            }


            val dataSet = LineDataSet(entries, "Label") // add entries to dataset
            dataSet.setDrawValues(false)

            dataSet.setDrawFilled(true)


            chart.xAxis.axisMinimum = 8.0f
            chart.xAxis.mAxisMaximum = 20.0f
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM


            // todo: probably store in the database in a separate table: Current number of users with the timestamp


            chart.xAxis.setDrawGridLines(false)
            chart.xAxis.setDrawAxisLine(false)
            chart.xAxis.setDrawLabels(true)
            chart.axisLeft.setDrawGridLines(false)
            chart.axisLeft.setDrawLabels(true)
            chart.axisLeft.setDrawAxisLine(false)
            chart.axisRight.setDrawLabels(false)
            chart.axisRight.setDrawAxisLine(false)
            chart.axisRight.setDrawGridLines(false)
            chart.axisLeft.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
            chart.xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
            chart.setViewPortOffsets(0f, 0f, 0f, 0f)
            chart.xAxis.valueFormatter = MyFormatter()

            val lineData = LineData(dataSet)
            chart.data = lineData



            chart.setDrawGridBackground(false)
            chart.legend.isEnabled = false   // Hide the legend

            chart.description.isEnabled = false

            chart.invalidate() // refresh
        })



        viewModel.currentNumberOfGymMembers.observe(this, Observer {
            currentNumberOfUsers.text = it.toString() + " people at the gym"
        })


        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Token", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                Log.d("Token", token)
            })


    }


    companion object {
        fun newInstance(): GymFragment = GymFragment()
    }
}

