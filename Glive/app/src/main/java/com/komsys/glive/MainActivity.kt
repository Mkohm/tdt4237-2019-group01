package com.komsys.glive

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.komsys.glive.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_gym.*
import kotlinx.android.synthetic.main.main_activity.*


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        FirebaseApp.initializeApp(this)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.gym -> {
                    val gymFragment = GymFragment.newInstance()
                    openFragment(gymFragment)
                    true
                }
                R.id.settings -> {
                    val fragment = SettingsFragment.newInstance()
                    openFragment(fragment)
                    true
                }
                else -> {
                    Log.d("TAG", "yo")
                    true
                }
            }
        }

        bottom_navigation.setSelectedItemId(R.id.gym)

    }


}

class MyFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {

        return when (value) {
            8f -> ""
            10f -> "10:00"
            12f -> "12:00"
            14f -> "14:00"
            16f -> "16:00"
            18f -> "18:00"
            20f -> ""
            else -> super.getAxisLabel(value, axis)
        }

    }
}
