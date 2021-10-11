package com.example.workout

import com.example.workout.db.Exercice

//für RecyclerView muss zusätzlich gespeichert werden, ob das Element gewählt ist.
data class ExerciceWrapper(
    var exercice: Exercice,
    var selected: Boolean
)