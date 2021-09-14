package com.example.workout.db

import android.location.Address
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

//Entität mit Default-Werten
@Entity
data class Workout(
    @PrimaryKey(autoGenerate = true) val wid: Int = 0,
    @ColumnInfo(name = "name") val name: String? = "",
    @ColumnInfo(name = "type") val type: Int? = 0,      //0 = Normal; 1 = Supersets
    @ColumnInfo(name = "number_exercices") val numberExercices: Int? = 0,
    @ColumnInfo(name = "rest_exercices") val restExercices: Int? = 0,
    @ColumnInfo(name = "rest_sets") val restSets: Int? = 0,
    val exercices: ArrayList<WorkoutEntry> = arrayListOf(),
    val exercicesSuper: ArrayList<WorkoutEntry> = arrayListOf()

)