package com.example.workout.ui.exercices

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.db.AppDatabase
import com.example.workout.db.Exercice
import com.example.workout.db.Routine

class ExercicesViewModel(app: Application) : AndroidViewModel(app) {

    //Referenz zur Datenbank
    val db = AppDatabase.getInstance(app.applicationContext)

    fun getAllExercices(): LiveData<List<Exercice>> {
        return db.exerciceDao().getAllAlphabetical()
    }


}