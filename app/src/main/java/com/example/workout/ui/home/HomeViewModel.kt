package com.example.workout.ui.home

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.db.AppDatabase
import com.example.workout.db.ExerciceDao
import com.example.workout.db.Routine
import com.example.workout.db.RoutineDao
import androidx.lifecycle.LiveData




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

    //Referenz zur Datenbank
    val db = AppDatabase.getInstance(app.applicationContext)





    fun getAllRoutines(): LiveData<List<Routine>> {
        return db.routineDao().getAll()
    }





}


