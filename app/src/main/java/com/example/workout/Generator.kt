package com.example.workout

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.workout.db.Exercice
import com.example.workout.db.Routine
import com.example.workout.db.WorkoutEntry
import com.example.workout.ui.home.HomeViewModel
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList

class Generator(
    private var homeViewModel: HomeViewModel,
    private var rid: Int?,
    private var wid: Int?,
    private var context: Context?
) {

    //Nebenläufige Generierung der Routine
    fun generateRoutineAndStartActivity() {
        GlobalScope.launch(Dispatchers.IO) {
            lateinit var routine: Routine

            //je nachdem, ob eine ID für eine Routine oder nur für ein Workout übergeben wurde...
            if(rid == null){
                routine = Routine(0, "-", workouts = arrayListOf(homeViewModel.getWorkoutByIdAsync(wid!!)))
            }else{
                routine = homeViewModel.getRoutineByIdAsync(rid!!)
            }

            var json =
                CommunicationModel(routine.name, arrayListOf())

            //über alle Workouts der Routine iterieren
            for((index0, value0) in routine.workouts.withIndex()){
                //Log.v("hhh", index0.toString())
                var workout = value0

                json.workouts.add(WorkoutModel(workout.name, routine.restWorkouts.toString(), arrayListOf()))


                var exerciceCounter = 0

                //Liste, in der alle Übungen des Workouts in der der Priorität entsprechenden Quantität vertreten sind
                //1-fach, 5-fach, bzw. 25-fach
                var listForRandomChoice: ArrayList<Exercice> = arrayListOf()

                for ((index, value) in workout.exercices.withIndex()) {
                    when (value.priority) {
                        0 -> listForRandomChoice.add(value.exercice)
                        1 -> for (i in 1..5) {
                            listForRandomChoice.add(value.exercice)
                        }
                        2 -> for (i in 1..25) {
                            listForRandomChoice.add(value.exercice)
                        }
                        else -> {
                        }
                    }
                }

                //so oft wiederholen, wie in Workout eingestellt
                //Schleife benennen, um aus ihr ausbrechen zu können
                loop@ while (exerciceCounter < workout.numberSets!!) {
                    //zufällige Übung auswählen
                    val randomExercice =
                        listForRandomChoice.get(Random().nextInt(listForRandomChoice.size))

                    //Alle Vorkommen der eben ausgewählten Übung aus Liste löschen, damit Übung nicht doppelt vorkommt.
                    listForRandomChoice.removeAll(Collections.singleton(randomExercice))

                    //Falls die Liste nun aber leer, die eingestellte Satzzahl jedoch noch nicht erreicht ist,
                    //muss die Liste neu gefüllt werden, Übungen also doppelt drankommen.
                    if (listForRandomChoice.size == 0) {
                        for ((index, value) in workout.exercices.withIndex()) {
                            when (value.priority) {
                                0 -> listForRandomChoice.add(value.exercice)
                                1 -> for (i in 1..5) {
                                    listForRandomChoice.add(value.exercice)
                                }
                                2 -> for (i in 1..25) {
                                    listForRandomChoice.add(value.exercice)
                                }
                                else -> {
                                }
                            }
                        }
                    }

                    //passendes WorkoutEntry-Element finden
                    lateinit var entry: WorkoutEntry
                    for ((index, value) in workout.exercices.withIndex()) {
                        if (value.exercice.eid == randomExercice.eid) {
                            entry = value
                            break
                        }
                    }

                    var sets = 0
                    if (entry.multipleSets == true) {
                        //zufällige Anzahl an Sätzen. 1 bis 4.
                        val min = 1
                        val max = 4
                        var randomNum: Int = ThreadLocalRandom.current().nextInt(min, max + 1)
                        sets = randomNum
                    } else {
                        sets = 1
                    }

                    var power = false

                    //Die ausgewählten Übungen hinzufügen
                    for (j in 1..sets) {

                        //20% Chance, dass Pause zwischen Sätzen wegfällt --> Supersatz. Stattdessen 1 Satz weniger
                        //nicht bei letztem Satz und nicht, wenn nur 1 Satz
                        var newLength: Int? = entry.length
                        if (j != sets && sets != 1) {
                            if (Math.random() < 0.2) {
                                newLength = entry.length?.times(2)
                                sets--
                            }
                        }

                        //Übung kürzer, wenn power gesetzt
                        if (power) {
                            newLength = newLength?.times(0.75)?.toInt()
                        }

                        json.workouts[index0].exercices.add(
                            ExerciceModel(
                                randomExercice.eid.toString(),
                                newLength.toString(), power, entry.innerRest.toString(), j.toString()
                            )
                        )
                        exerciceCounter++

                        //äußere while-Schleife beenden, wenn eingestellte Satzanzahl erreicht
                        if (exerciceCounter == workout.numberSets) {
                            break@loop
                        }

                        //Im letzten Satz kann power auf true gesetzt werden. Chance beträgt 50%.
                        //Pause dann um 50% länger, daher wird es schon in der vorletzten Iteration gesetzt.
                        //Demnach mindestens 2 Sätze nötig.
                        power = j == sets - 1 && Math.random() > 0.5

                        //Pause nach Satz hinzufügen, aber nur, wenn nicht letzter Satz der Übung
                        if (j != sets && !power) {
                            json.workouts[index0].exercices.add(
                                ExerciceModel(
                                    "0",
                                    workout.restSets.toString(),
                                    null,
                                    null,
                                    null
                                )
                            )
                        } else if (j != sets && power) {
                            var newRest = workout.restSets?.times(1.5)?.toInt()
                            json.workouts[index0].exercices.add(
                                ExerciceModel(
                                    "0",
                                    newRest.toString(),
                                    null,
                                    null,
                                    null
                                )
                            )
                        }
                    }


                    //Pause nach Übung hinzufügen (nur, wenn nicht letzte Übung des Workouts)
                    //25% Chance, dass Pause zwischen Übungen wegfällt bzw. nur 10 Sekunden beträgt --> Supersatz.
                    var newRest2 = workout.restExercices
                    if (Math.random() < 0.25) {
                        newRest2 = 10
                    }

                    json.workouts[index0].exercices.add(
                        ExerciceModel(
                            "0",
                            newRest2.toString(),
                            null,
                            null,
                            null
                        )
                    )

                }

            }





            val gson = Gson()
            val result = gson.toJson(json)


            Log.v("hhh", result)


            val intent = Intent(context, CastActivity::class.java)
            //Generiertes Workout übergeben
            intent.putExtra("routineJson", result)
            context?.startActivity(intent)


        }
    }

}