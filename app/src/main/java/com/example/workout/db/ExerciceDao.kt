package com.example.workout.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ExerciceDao {
    @Query("SELECT * FROM exercice")
    fun getAll(): List<Exercice>

    @Query("SELECT * FROM exercice WHERE eid IN (:exerciceIds)")
    fun loadAllByIds(exerciceIds: IntArray): List<Exercice>

    @Insert
    fun insertAll(vararg users: Exercice)

    @Delete
    fun delete(user: Exercice)
}