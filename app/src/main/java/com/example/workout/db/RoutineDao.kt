package com.example.workout.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine")
    fun getAll(): LiveData<List<Routine>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<Routine>)

}