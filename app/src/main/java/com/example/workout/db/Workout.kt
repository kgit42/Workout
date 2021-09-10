package com.example.workout.db

import android.location.Address
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Workout(
    @PrimaryKey val wid: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "type") val type: Int?,      //0 = Normal; 1 = Supersets
    @ColumnInfo(name = "number_exercices") val numberExercices: Int?,
    @ColumnInfo(name = "rest_exercices") val restExercices: Int?,
    @ColumnInfo(name = "rest_sets") val restSets: Int?,
    //@Embedded val exercices: List<WorkoutEntry>?,
    //@Embedded val exercicesSuper: List<WorkoutEntry>?

)