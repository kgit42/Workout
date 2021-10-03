package com.example.workout.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine")
    fun getAll(): LiveData<List<Routine>>

    @Query("SELECT * FROM routine")
    fun getAllAsync(): List<Routine>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<Routine>)

    @Query("SELECT * FROM routine WHERE rid = (:rid) ")
    fun getById(rid: Int?): LiveData<Routine>

    @Query("SELECT * FROM routine WHERE rid = (:rid) ")
    fun getByIdAsync(rid: Int?): Routine

    @Update
    fun update(routine: Routine)

    //Erstellen einer neuen Routine und Zurückgeben der ID
    @Insert
    fun insert(routine: Routine): Long

    @Query("DELETE FROM routine WHERE rid = (:rid)")
    fun delete(rid: Int)

}