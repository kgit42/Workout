package com.example.workout.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.example.workout.db.AppDatabase
import com.example.workout.db.ExerciceDao
import com.example.workout.db.Routine
import com.example.workout.db.RoutineDao

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    // Create a LiveData with a String
    val currentName: MutableLiveData<String> by lazy {
        //db = AppDatabase.getInstance()
        MutableLiveData<String>()
    }

    val db = AppDatabase.getInstance(app.applicationContext)


    val exercicesWithBreak: LiveData<List<Routine>> = db.routineDao().getAll()

}
