package com.example.workout.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(Exercice::class, Routine::class, Workout::class, WorkoutEntry::class), version = 16)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun exerciceDao(): ExerciceDao
    abstract fun routineDao(): RoutineDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun workoutEntryDao(): WorkoutEntryDao

    companion object {

        //Volatile sorgt dafür, dass Variable INSTANCE nicht gecached wird und immer up-to-date ist (von allen Threads)
        //https://developer.android.com/codelabs/kotlin-android-training-room-database#5
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getInstance(context: Context): AppDatabase {
            //wechselseitiger Ausschluss: Nur einer kann gleichzeitig Block betreten
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "app_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}