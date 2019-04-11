package com.komsys.glive.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.ZoneId
import java.time.ZoneOffset


class MainViewModel : ViewModel() {
    companion object {
        val TAG = "TAG"
    }

    val currentNumberOfGymMembers: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }


    val currentNumberOfUsersHistory: MutableLiveData<List<DataPoint>> by lazy {
        MutableLiveData<List<DataPoint>>()
    }

    init {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()


        val docRef = db.collection("users")
        docRef.addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }

            val current = value!!.filter { it["isIntheGym"] == true }.size


            currentNumberOfGymMembers.value = current


            var dataPoints = ArrayList<DataPoint>()
            val timestamps = value.forEach {
                val currentTimestamp = it["scanTime"] as Timestamp
                val numberOfUsers = getActiveUsersAtTimestamp(currentTimestamp, value)

                val scannedHour = currentTimestamp.toDate().toInstant().atZone(ZoneId.of("Europe/Oslo")).hour.toFloat()
                val scannedMinute = currentTimestamp.toDate().toInstant().atZone(ZoneId.of("Europe/Oslo")).minute.toFloat()

                val hourfraction = scannedHour + (scannedMinute/60)

                dataPoints.add(DataPoint(hourfraction, numberOfUsers))

            }

            val sortedDataPoints = dataPoints.sortedBy { it.timestamp }

            currentNumberOfUsersHistory.value = sortedDataPoints

        })

    }

    fun getActiveUsersAtTimestamp(currentTimestamp: Timestamp, it: QuerySnapshot?): Int {

        var numberOfUsers = 1
        it?.forEach { user ->
            val isIntheGym = user["isIntheGym"] as Boolean
            val timestamp = user["scanTime"] as Timestamp
            if (isIntheGym && timestamp.compareTo(currentTimestamp) == -1) {
                numberOfUsers++
            }
        }

        return numberOfUsers
    }


}

data class DataPoint(val timestamp: Float, val numberOfUsers: Int)
