package com.example.workout.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WorkoutEntryDao {
    //Erstellen eines neuen WorkoutEntries und Zurückgeben der ID
    @Insert
    fun insert(workoutentry: WorkoutEntry): Long

    @Query("SELECT * FROM workoutentry WHERE weid = (:weid) ")
    fun getById(weid: Int?): LiveData<WorkoutEntry>

    @Query("UPDATE workoutentry SET length = (:dauer), multiple_sets = (:mehrsatz), priority = (:prio), inner_rest = (:pause) WHERE weid = (:id)")
    fun update(id: Int?, dauer: Int, mehrsatz: Boolean, prio: Int, pause: Int)

}