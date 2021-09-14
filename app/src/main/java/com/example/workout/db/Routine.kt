package com.example.workout.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Routine(
    @PrimaryKey(autoGenerate = true) val rid: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "rest_workouts") val restWorkouts: String?,

    //@Embedded val exercicesWithRest: List<Map<WorkoutEntry, Int>>?

)