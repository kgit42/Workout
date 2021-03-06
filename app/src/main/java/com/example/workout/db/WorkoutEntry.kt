package com.example.workout.db

import android.location.Address
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity

//Relation WorkoutEntry wird in erster Linie für die fortlaufende ID genutzt. Die
// relevanten Daten sind ausschließlich in Workout-Relation gespeichert
data class WorkoutEntry(
    @PrimaryKey(autoGenerate = true) val weid: Int = 0,
    @ColumnInfo(name = "length") var length: Int? = 45,
    @ColumnInfo(name = "multiple_sets") var multipleSets: Boolean? = true,
    @ColumnInfo(name = "priority") var priority: Int? = 1,
    @ColumnInfo(name = "inner_rest") var innerRest: Int? = 10,
    @ColumnInfo(name = "custom_rest") var customRest: Int? = -1,
    @Embedded val exercice: Exercice
)