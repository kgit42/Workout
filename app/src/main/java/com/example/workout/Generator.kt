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

            //je nachdem, ob eine ID für eine Routine oder nur für ein Workout übergeben wurde, eine
            // neue Routine anlegen oder die Routine übernehmen
            if (rid == null) {
                routine = Routine(
                    0,
                    "-",
                    workouts = arrayListOf(homeViewModel.getWorkoutByIdAsync(wid!!))
                )
            } else {
                routine = homeViewModel.getRoutineByIdAsync(rid!!)
            }

            var json =
                CommunicationModel(routine.name, arrayListOf())

            //über alle Workouts der Routine iterieren
            for ((index0, value0) in routine.workouts.withIndex()) {
                //Log.v("hhh", index0.toString())
                var workout = value0

                json.workouts.add(
                    WorkoutModel(
                        workout.name,
                        routine.restWorkouts.toString(),
                        arrayListOf()
                    )
                )

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

                //Falls es ein Supersatz-Workout ist, noch 2 weitere Listen nötig: eine mit nur Einsatzübungen und eine mit nur Mehrsatzübungen
                var listForRandomChoiceSuper1Set: ArrayList<Exercice> = arrayListOf()
                var listForRandomChoiceSuperMSet: ArrayList<Exercice> = arrayListOf()
                if (workout.type == 1) {
                    for ((index, value) in workout.exercicesSuper.withIndex()) {
                        when (value.priority) {
                            0 ->
                                if (value.multipleSets == false) {
                                    listForRandomChoiceSuper1Set.add(value.exercice)
                                } else {
                                    listForRandomChoiceSuperMSet.add(value.exercice)
                                }
                            1 -> if (value.multipleSets == false) {
                                for (i in 1..5) {
                                    listForRandomChoiceSuper1Set.add(value.exercice)
                                }
                            } else {
                                for (i in 1..5) {
                                    listForRandomChoiceSuperMSet.add(value.exercice)
                                }
                            }
                            2 -> if (value.multipleSets == false) {
                                for (i in 1..25) {
                                    listForRandomChoiceSuper1Set.add(value.exercice)
                                }
                            } else {
                                for (i in 1..25) {
                                    listForRandomChoiceSuperMSet.add(value.exercice)
                                }
                            }
                        }
                    }
                }

                //endlos wiederholen, Abbruch erfolgt in der Schleife
                //Schleife benennen, um aus ihr ausbrechen zu können
                loop@ while (true) {
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


                    //zugehöriges WorkoutEntry-Element finden
                    lateinit var entry: WorkoutEntry
                    for ((index, value) in workout.exercices.withIndex()) {
                        if (value.exercice.eid == randomExercice.eid) {
                            entry = value
                            break
                        }
                    }


                    //zufällige Übung 2 auswählen, falls Supersatz-Workout.
                    lateinit var randomExercice2: Exercice
                    lateinit var entry2: WorkoutEntry

                    //je nach Art der ausgewählten Übung 1 Übung 2 auswählen
                    if (workout.type == 1) {
                        if (entry.multipleSets == true) {
                            randomExercice2 = listForRandomChoiceSuperMSet[Random().nextInt(
                                listForRandomChoiceSuperMSet.size
                            )]
                        } else {
                            randomExercice2 = listForRandomChoiceSuper1Set[Random().nextInt(
                                listForRandomChoiceSuper1Set.size
                            )]
                        }


                        //Alle Vorkommen der eben ausgewählten Übung aus Liste löschen, damit Übung nicht doppelt vorkommt.
                        listForRandomChoiceSuper1Set.removeAll(Collections.singleton(randomExercice2))
                        listForRandomChoiceSuperMSet.removeAll(Collections.singleton(randomExercice2))

                        //Falls eine der Listen nun aber leer ist, die eingestellte Satzzahl jedoch noch nicht erreicht ist,
                        //muss die Liste neu gefüllt werden, Übungen also doppelt drankommen.
                        if (listForRandomChoiceSuper1Set.size == 0) {
                            for ((index, value) in workout.exercicesSuper.withIndex()) {
                                when (value.priority) {
                                    0 ->
                                        if (value.multipleSets == false) {
                                            listForRandomChoiceSuper1Set.add(value.exercice)
                                        }
                                    1 -> if (value.multipleSets == false) {
                                        for (i in 1..5) {
                                            listForRandomChoiceSuper1Set.add(value.exercice)
                                        }
                                    }
                                    2 -> if (value.multipleSets == false) {
                                        for (i in 1..25) {
                                            listForRandomChoiceSuper1Set.add(value.exercice)
                                        }
                                    }
                                }
                            }
                        }

                        if (listForRandomChoiceSuperMSet.size == 0) {
                            for ((index, value) in workout.exercicesSuper.withIndex()) {
                                when (value.priority) {
                                    0 ->
                                        if (value.multipleSets == true) {
                                            listForRandomChoiceSuperMSet.add(value.exercice)
                                        }
                                    1 -> if (value.multipleSets == true) {
                                        for (i in 1..5) {
                                            listForRandomChoiceSuperMSet.add(value.exercice)
                                        }
                                    }
                                    2 -> if (value.multipleSets == true) {
                                        for (i in 1..25) {
                                            listForRandomChoiceSuperMSet.add(value.exercice)
                                        }
                                    }
                                }
                            }
                        }

                        //zugehöriges WorkoutEntry finden
                        for ((index, value) in workout.exercicesSuper.withIndex()) {
                            if (value.exercice.eid == randomExercice2.eid) {
                                entry2 = value
                                break
                            }
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


                    //Die ausgewählte Übung <sets> Mal hinzufügen
                    for (j in 1..sets) {

                        //20% Chance, dass Pause zwischen Sätzen wegfällt --> Supersatz. Stattdessen 1 Satz weniger.
                        //Nicht bei letztem Satz und nicht, wenn nur 1 Satz.
                        var newLength: Int? = entry.length

                        var newLength2: Int? = 0
                        if (workout.type == 1) {
                            newLength2 = entry2.length
                        }
                        if (j != sets && sets != 1) {
                            if (Math.random() < 0.2) {
                                newLength = entry.length?.times(2)

                                if (workout.type == 1) {
                                    newLength2 = entry2.length?.times(2)
                                }

                                sets--
                            }
                        }

                        //Satz kürzer, wenn power gesetzt
                        if (power) {
                            newLength = newLength?.times(0.75)?.toInt()

                            if (workout.type == 1) {
                                newLength2 = newLength2?.times(0.75)?.toInt()
                            }

                        }

                        var supersetInd: String?
                        if (workout.type == 1) {
                            supersetInd = "Übung A"
                        } else {
                            supersetInd = null
                        }

                        //Exercice-Model zu JSON-Struktur hinzufügen
                        json.workouts[index0].exercices.add(
                            ExerciceModel(
                                randomExercice.eid.toString(),
                                newLength.toString(),
                                power,
                                entry.innerRest.toString(),
                                j.toString(),
                                supersetInd
                            )
                        )

                        //bei Supersatz-Workout weitere Exercice-Models hinzufügen
                        if (workout.type == 1) {
                            //10s Pause bei Supersatz
                            json.workouts[index0].exercices.add(
                                ExerciceModel(
                                    "0", "10", null, null, null, null
                                )
                            )

                            json.workouts[index0].exercices.add(
                                ExerciceModel(
                                    randomExercice2.eid.toString(),
                                    newLength2.toString(),
                                    power,
                                    entry2.innerRest.toString(),
                                    j.toString(),
                                    "Übung B"
                                )
                            )
                        }

                        exerciceCounter++

                        //äußere while-Schleife beenden, wenn eingestellte Satzanzahl erreicht
                        if (exerciceCounter == workout.numberSets) {
                            break@loop
                        }

                        //Im letzten Satz kann power auf true gesetzt werden. Chance beträgt 50%.
                        //Pause dann um 50% länger, daher wird es schon in der vorletzten Iteration gesetzt.
                        //Demnach mindestens 2 Sätze nötig.
                        power = j == sets - 1 && Math.random() > 0.5

                        //Pause nach Satz hinzufügen, aber nur, wenn nicht letzter Satz der Übung / der Übungskombination
                        if (j != sets && !power) {
                            json.workouts[index0].exercices.add(
                                ExerciceModel(
                                    "0",
                                    workout.restSets.toString(),
                                    null,
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
                                    null,
                                    null
                                )
                            )
                        }

                    }


                    //Pause nach Übung / Übungskombination hinzufügen (nur, wenn nicht letzte Übung / letzte Übungskombination des Workouts)
                    //25% Chance, dass Pause zwischen Übungen (nicht bei Supersatz-Workouts) wegfällt bzw. nur 10 Sekunden beträgt --> Supersatz.
                    var newRest2 = workout.restExercices
                    if (workout.type == 0 && Math.random() < 0.25) {
                        newRest2 = 10
                    }

                    json.workouts[index0].exercices.add(
                        ExerciceModel(
                            "0",
                            newRest2.toString(),
                            null,
                            null,
                            null, null
                        )
                    )

                }

            }


            val gson = Gson()
            val result = gson.toJson(json)


            Log.v("hhh", result)


            val intent = Intent(context, CastActivity::class.java)
            //Generierte Routine übergeben
            intent.putExtra("routineJson", result)

            //falls übergebene Routine zu groß ist, stürzt App ab ohne entsprechende Fehlermeldung/Exception
            context?.startActivity(intent)


        }
    }

}