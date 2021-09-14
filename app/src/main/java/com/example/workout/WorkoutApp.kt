package com.example.workout

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.workout.db.AppDatabase
import com.example.workout.db.Exercice
import com.example.workout.db.Routine
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.*
import android.content.SharedPreferences





//Base class for maintaining global application state. You can provide your own implementation by creating
// a subclass and specifying the fully-qualified name of this subclass as the
// "android:name" attribute in your AndroidManifest.xml's <application> tag.
// The Application class, or your subclass of the Application class, is instantiated before any
// other class when the process for your application/package is created.

class WorkoutApp : Application() {

    override fun onCreate() {
        super.onCreate()

        coroutine()
    }

    //Coroutine! Sonst Error: Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
    private fun coroutine() {
        ProcessLifecycleOwner.get().lifecycleScope.launch {
            prepopulateDatabase()
        }
    }


    //Pre-populate database with raw data.
    suspend fun prepopulateDatabase() {

        //Shared Preferences, um nur beim ersten Start Initialisierung der DB auszuführen
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)

                try {
                    applicationContext.resources.openRawResource(R.raw.exercices).use { inputStream ->
                        JsonReader(inputStream.reader()).use { jsonReader ->
                            val type = object : TypeToken<List<Exercice>>() {}.type
                            val exerciceList: List<Exercice> = Gson().fromJson(jsonReader, type)

                            //Calls the specified suspending block with a given coroutine context, suspends until it completes, and returns the result.
                            withContext(Dispatchers.IO) {
                                Log.v("hello1",
                                    AppDatabase.getInstance(applicationContext).exerciceDao().getAll().value.toString()
                                )
                                //(--> nur beim ersten Start)
                                if(firstStart){
                                    AppDatabase.getInstance(applicationContext).exerciceDao().insertAll(exerciceList)

                                    val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
                                    val editor = prefs.edit()
                                    editor.putBoolean("firstStart", false)
                                    editor.apply()
                                }

                            }

                        }
                    }
                    Log.v("hello", "success")
                } catch (e: Exception) {
                    Log.v("hello", "failure$e")
                }

    }


}

