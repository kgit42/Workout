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

    /*
    fun getAllExercicesWithoutExisting(): LiveData<List<Exercice>> {
        return db.exerciceDao().getAll()
    }
     */

    fun getWorkoutById(wid: Int?): LiveData<Workout> {
        return db.workoutDao().getById(wid)
    }

    fun getWorkoutEntryById(weid: Int?): LiveData<WorkoutEntry> {
        return db.workoutEntryDao().getById(weid)
    }

    suspend fun createWorkout(workout: Workout): Long {
        return withContext(Dispatchers.IO) { db.workoutDao().insert(workout) }
    }

    //erstellt ein neues WorkoutEntry, ID des übergebenen Objektes wird dabei verworfen
    suspend fun createWorkoutEntry(workoutentry: WorkoutEntry): Long {
        return withContext(Dispatchers.IO) { db.workoutEntryDao().insert(workoutentry) }
    }

    /*
    //updatet das WorkoutEntry anhand der ID
    suspend fun updateWorkoutEntry(id: Int?, dauer: Int, mehrsatz: Boolean, prio: Int, pause: Int){
        return withContext(Dispatchers.IO) {db.workoutEntryDao().update(id, dauer, mehrsatz, prio, pause)}
    }
     */

    //updatet das Workout anhand der ID (https://developer.android.com/training/data-storage/room/accessing-data)
    suspend fun updateWorkout(workout: Workout){
        return withContext(Dispatchers.IO) {db.workoutDao().update(workout)}
    }

    suspend fun deleteWorkout(id: Int) {
        return withContext(Dispatchers.IO) { db.workoutDao().delete(id) }
    }





}


