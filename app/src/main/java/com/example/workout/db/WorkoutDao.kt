package com.example.workout.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout")
    fun getAll(): LiveData<List<Workout>>

    @Query("SELECT * FROM workout WHERE wid = (:wid) ")
    fun getById(wid: Int?): LiveData<Workout>

    //Erstellen eines neuen Workouts und Zurückgeben der ID
    @Insert
    fun insert(workout: Workout): Long

    @Update
    fun update(workout: Workout)

    @Query("DELETE FROM workout WHERE wid = (:wid)")
    fun delete(wid: Int)


}