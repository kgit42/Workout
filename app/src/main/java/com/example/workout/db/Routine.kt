package com.example.workout.db

import androidx.room.*

@Entity()
data class Routine(
    @PrimaryKey(autoGenerate = true) val rid: Int = 0,
    @ColumnInfo(name = "name") val name: String? = "",
    @ColumnInfo(name = "rest_workouts") val restWorkouts: Int? = 0,
    val workouts: ArrayList<Workout> = arrayListOf()

    //@Embedded val exercicesWithRest: List<Map<WorkoutEntry, Int>>?

)