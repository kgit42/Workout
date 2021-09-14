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
import androidx.lifecycle.LiveData
import com.example.workout.db.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeViewModel(app: Application) : AndroidViewModel(app) {


    /*
    // Create a LiveData with a String
    val currentName: MutableLiveData<String> by lazy {
        //db = AppDatabase.getInstance()
        MutableLiveData<String>()
    }
     */


    //Referenz zur Datenbank
    val db = AppDatabase.getInstance(app.applicationContext)




    fun getAllRoutines(): LiveData<List<Routine>> {
        return db.routineDao().getAll()
    }

    fun getAllWorkouts(): LiveData<List<Workout>> {
        return db.workoutDao().getAll()
    }

    fun getAllExercices(): LiveData<List<Exercice>> {
        return db.exerciceDao().getAll()
    }

    fun getById(wid: Int?): LiveData<Workout> {
        return db.workoutDao().getById(wid)
    }

    suspend fun createWorkout(workout: Workout): Long {
        return withContext(Dispatchers.IO) { db.workoutDao().insert(workout) }
    }

    suspend fun createWorkoutEntry(workoutentry: WorkoutEntry): Long {
        return withContext(Dispatchers.IO) { db.workoutEntryDao().insert(workoutentry) }
    }






}


