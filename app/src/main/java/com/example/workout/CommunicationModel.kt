package com.example.workout


//Modellierung der JSON-Struktur zum Senden von Routinen über Google Cast
data class CommunicationModel (
    val routine_name: String?,
    val workouts: MutableList<WorkoutModel>



)


data class WorkoutModel(
    val workout_name: String?,
    val workout_rest: String,
    val exercices: MutableList<ExerciceModel>
)


data class ExerciceModel(
    val eid: String,
    val length: String,
    val power: Boolean?,
    val rest: String?,
    val set: String?
)


