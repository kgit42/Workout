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

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Exercice

    @Insert
    fun insertAll(vararg users: Exercice)

    @Delete
    fun delete(user: Exercice)
}