package com.example.workout.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExerciceDao {
    @Query("SELECT * FROM exercice ORDER BY name")
    fun getAll(): LiveData<List<Exercice>>

    @Query("SELECT * FROM exercice WHERE eid IN (:exerciceIds)")
    fun loadAllByIds(exerciceIds: IntArray): List<Exercice>

    @Insert
    fun insertAll(vararg exercice: Exercice)

    @Delete
    fun delete(exercice: Exercice)

    @Query("DELETE FROM exercice")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<Exercice>)
}