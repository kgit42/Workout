package com.example.workout

import com.example.workout.db.WorkoutEntry

//Interface, um in Helper Class unterschiedliche Adapter verwenden zu können
interface WorkoutDetailFragmentAdapterInterface {
    fun removeElement(value: WorkoutEntry)
}