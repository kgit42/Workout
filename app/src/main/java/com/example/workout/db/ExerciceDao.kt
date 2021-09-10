package com.example.workout.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExerciceDao {
    @Query("SELECT * FROM exercice")
    fun getAll(): LiveData<List<Exercice>>

    @Query("SELECT * FROM exercice WHERE eid IN (:exerciceIds)")
    fun loadAllByIds(exerciceIds: IntArray): List<Exercice>

    @Insert
    fun insertAll(vararg users: Exercice)

    @Delete
    fun delete(user: Exercice)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(list: List<Exercice>)
}