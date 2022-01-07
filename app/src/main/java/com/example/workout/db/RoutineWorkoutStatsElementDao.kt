package com.example.workout.db

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RoutineWorkoutStatsElementDao {
    @Query("SELECT * FROM routineworkoutstatselement ORDER BY timestamp")
    fun getAll(): LiveData<List<RoutineWorkoutStatsElement>>

    @Insert
    fun insert(routineWorkoutStatsElement: RoutineWorkoutStatsElement)

    @Query("DELETE FROM routineworkoutstatselement WHERE seid = (:seid)")
    fun delete(seid: Int)
}