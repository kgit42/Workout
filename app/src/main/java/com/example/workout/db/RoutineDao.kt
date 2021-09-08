package com.example.workout.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine")
    fun getAll(): LiveData<List<Routine>>

}