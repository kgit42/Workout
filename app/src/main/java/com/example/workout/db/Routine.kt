package com.example.workout.db

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Routine(
    @PrimaryKey val rid: Int,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "rest") val rest: String?,

    //@Embedded val exercicesWithBreak: List<Map<WorkoutEntry, Int>>?

)