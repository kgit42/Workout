package com.example.workout.db

import android.location.Address
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class RoutineWorkoutStatsElement(
    @PrimaryKey(autoGenerate = true) val seid: Int = 0,
    @ColumnInfo(name = "length") var length: Int? = 0,
    @ColumnInfo(name = "numberSetsDone") var numberSetsDone: Int? = 0,
    @ColumnInfo(name = "name") var name: String? = "",
    @ColumnInfo(name = "type") var type: Int? = 0, //0 = Routine, 1 = Workout
    @ColumnInfo(name = "typeWorkout") var typeWorkout: Int? = 0, //0 = Normal, 1 = Supersatz
    @ColumnInfo(name = "timestamp") var timestamp: Long? = 0
)