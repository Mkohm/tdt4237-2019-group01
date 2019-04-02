package com.komsys.glive.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore


class MainViewModel : ViewModel() {
    companion object {
        val TAG = "TAG"
    }

    val currentNumberOfGymMembers: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(   )
    }

    init {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()


        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        Log.d(TAG, document.id + " => " + document.data)
                    }
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }

    }


}
