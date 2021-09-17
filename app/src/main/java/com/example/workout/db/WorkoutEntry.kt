package com.example.workout.db

import android.location.Address
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkoutEntry(
    @PrimaryKey(autoGenerate = true) val weid: Int = 0,
    @ColumnInfo(name = "length") var length: Int = 0,
    @ColumnInfo(name = "multiple_sets") var multipleSets: Boolean = true,
    @ColumnInfo(name = "priority") var priority: Int = 0,
    @ColumnInfo(name = "inner_rest") var innerRest: Int = 10,
    @Embedded val exercice: Exercice
)