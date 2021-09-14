package com.example.workout.db

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface WorkoutEntryDao {
    //Erstellen eines neuen WorkoutEntries und Zurückgeben der ID
    @Insert
    fun insert(workoutentry: WorkoutEntry): Long
}