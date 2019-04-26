package com.komsys.glive.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.ZoneId


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

        })

        val fakedataref = db.collection("fakeData")
        fakedataref.addSnapshotListener(EventListener<QuerySnapshot> { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@EventListener
            }


            val fakeData = arrayListOf<DataPoint>()
            value!!.forEach { data ->

                for (index in 0..11) {

                    val scannedHour =
                        (data["timestamp" + index] as Timestamp).toDate().toInstant().atZone(ZoneId.of("Europe/Oslo"))
                            .hour.toFloat()
                    val scannedMinute =
                        (data["timestamp" + index] as Timestamp).toDate().toInstant().atZone(ZoneId.of("Europe/Oslo"))
                            .minute.toFloat()
                    val hourfraction = scannedHour + (scannedMinute / 60)


                    val numberOfUsers = (data["number" + index] as Long).toInt()

                    fakeData.add(
                        DataPoint(
                            hourfraction, numberOfUsers
                        )
                    )
                }
            }


            currentNumberOfUsersHistory.value = fakeData
        })
    }


}

data class DataPoint(val timestamp: Float, val numberOfUsers: Int)
