package com.example.workout.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout")
    fun getAll(): LiveData<List<Workout>>

    @Query("SELECT * FROM workout WHERE wid = (:wid) ")
    fun getById(wid: Int): LiveData<Workout>


}