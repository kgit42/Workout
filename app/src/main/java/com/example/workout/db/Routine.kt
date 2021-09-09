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
    // @Embedded /*@ColumnInfo(name = "exercices_with_break")*/ val exercicesWithBreak: WorkoutEntry /*ArrayList<Map<WorkoutEntry, Int>>?*/
)