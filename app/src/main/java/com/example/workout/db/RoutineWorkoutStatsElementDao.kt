package com.example.workout.db

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RoutineWorkoutStatsElementDao {
    @Query("SELECT * FROM routineworkoutstatselement")
    fun getAll(): LiveData<List<RoutineWorkoutStatsElement>>

    @Insert
    fun insert(routineWorkoutStatsElement: RoutineWorkoutStatsElement)
}