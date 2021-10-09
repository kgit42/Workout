package com.example.workout.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.workout.db.AppDatabase
import com.example.workout.db.Exercice
import com.example.workout.db.Routine
import com.example.workout.db.RoutineWorkoutStatsElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatsViewModel(app: Application) : AndroidViewModel(app) {

    //Referenz zur Datenbank
    val db = AppDatabase.getInstance(app.applicationContext)

    fun getAllRoutineWorkoutStatsElements(): LiveData<List<RoutineWorkoutStatsElement>> {
        return db.routineWorkoutStatsElementDao().getAll()
    }

    suspend fun createRoutineWorkoutStatsElement(routineWorkoutStatsElement: RoutineWorkoutStatsElement) {
        withContext(Dispatchers.IO) { db.routineWorkoutStatsElementDao().insert(routineWorkoutStatsElement) }
    }
}
