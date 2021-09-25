package com.example.workout.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.*
import com.example.workout.db.Exercice
import com.example.workout.db.Workout
import com.example.workout.db.WorkoutEntry
import kotlinx.coroutines.launch
import org.json.JSONObject
import com.google.gson.Gson
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.ArrayList


class WorkoutsFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val rv = inflater.inflate(
            R.layout.fragment_list,
            container,
            false
        ) as RecyclerView
        rv.layoutManager = LinearLayoutManager(rv.context)

        //zunächst leere ArrayList mit Workouts
        val adapter =
            MyRecyclerViewAdapter(arrayListOf(Workout()))
        rv.adapter = adapter


        //Observer --> falls es Änderungen in DB gibt
        homeViewModel.getAllWorkouts().observe(viewLifecycleOwner) { workouts -> adapter.setData(workouts) }

        return rv
    }



    inner class MyRecyclerViewAdapter(
        private var values: List<Workout>
    ) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

        //um vom ViewModel aus Daten zu ändern
        fun setData(newData: List<Workout>) {
            this.values = newData
            notifyDataSetChanged()
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null
            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(R.id.workout_title)

            val startButton: Button = view.findViewById(R.id.buttonPlay)
            val progressbar: ProgressBar = view.findViewById(R.id.progress_loader)

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.workouts_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position].name
            holder.text.text = values[position].name

            holder.view.setOnClickListener { v ->
                val context = v.context

                //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine (bzw. die ID)
                val args = Bundle()
                args.putInt("wid", values[position].wid)
                holder.view.findNavController().navigate(R.id.navigation_workout_detail, args)
            }

            //OnLongClickListener zum Löschen
            holder.view.setOnLongClickListener{ v ->
                val dialog = DeleteDialogFragment()
                val args = Bundle()
                args.putInt("wid", values[position].wid)
                dialog.arguments = args

                dialog.show(childFragmentManager, "")
                return@setOnLongClickListener true
            }

            //Start-Button OnClickListener
            holder.startButton.setOnClickListener{v ->

                //Ladesymbol einblenden
                holder.startButton.visibility = View.INVISIBLE
                holder.progressbar.visibility = View.VISIBLE

                //Workout generieren
                generateWorkout(values[position].wid)


            }

        }

        override fun getItemCount(): Int = values.size




    }


    //Nebenläufige Generierung des Workouts
    private fun generateWorkout(wid: Int){

        lifecycleScope.launch {
            var workout = homeViewModel.getWorkoutByIdAsync(wid)
            var exerciceCounter = 0
            var json = CommunicationModel("", arrayListOf(WorkoutModel(workout.name, "", arrayListOf())))

            //Liste, in der alle Übungen des Workouts in der der Priorität entsprechenden Quantität vertreten sind
            //1-fach, 5-fach, bzw. 25-fach
            var listForRandomChoice: ArrayList<Exercice> = arrayListOf()

            for ((index, value) in workout.exercices.withIndex()) {
                when (value.priority) {
                    0 -> listForRandomChoice.add(value.exercice)
                    1 -> for(i in 1..5) {listForRandomChoice.add(value.exercice)}
                    2 -> for(i in 1..25) {listForRandomChoice.add(value.exercice)}
                    else -> {

                    }
                }
            }

            //so oft wiederholen, wie in Workout eingestellt
            loop@ while(exerciceCounter < workout.numberExercices!!){
                //zufällige Übung auswählen
                val randomExercice = listForRandomChoice.get(Random().nextInt(listForRandomChoice.size))

                //Alle Vorkommen der eben ausgewählten Übung aus Liste löschen, damit Übung nicht doppelt vorkommt.
                listForRandomChoice.removeAll(Collections.singleton(randomExercice))

                //passendes WorkoutEntry-Element finden
                lateinit var entry: WorkoutEntry
                for((index, value) in workout.exercices.withIndex()) {
                    if(value.exercice.eid == randomExercice.eid){
                        entry = value
                        break
                    }
                }

                if(entry.multipleSets == true){
                    //zufällige Anzahl an Sätzen. 1 bis 4.
                    val min = 1
                    val max = 4
                    var randomNum: Int = ThreadLocalRandom.current().nextInt(min, max + 1)

                    var power = false

                    //Die ausgewählten Übungen hinzufügen
                    for(j in 1..randomNum){

                        //10% Chance, dass Pause zwischen Sätzen wegfällt --> Supersatz. Stattdessen 1 Satz weniger
                        //nicht bei letztem Satz
                        var newLength: Int? = entry.length
                        if(j != randomNum){
                            if (Math.random() < 0.1){
                                newLength = entry.length?.times(2)
                                randomNum--
                            }
                        }

                        //Übung kürzer, wenn power gesetzt
                        if(power){
                            newLength = newLength?.times(0.75)?.toInt()
                        }

                        json.workouts[0].exercices.add(ExerciceModel(
                            randomExercice.eid.toString(),
                            newLength.toString(), power, entry.innerRest.toString()
                        ))
                        exerciceCounter++

                        //äußere while-Schleife beenden, wenn eingestellte Übungsanzahl erreicht
                        if(exerciceCounter == workout.numberExercices){
                            break@loop
                        }

                        //Im letzten Satz kann power auf true gesetzt werden. Chance beträgt 50%.
                        //Pause dann um 50% länger, daher wird es schon in der vorletzten Iteration gesetzt
                        power = j == randomNum - 1 && Math.random() > 0.5

                        //Pause hinzufügen, aber nur, wenn nicht letzter Satz der Übung
                        if(j != randomNum && !power){
                            json.workouts[0].exercices.add(ExerciceModel("0", workout.restSets.toString(), null, null))
                        }else if(j != randomNum && power){
                            var newRest = workout.restSets?.times(1.5)
                            json.workouts[0].exercices.add(ExerciceModel("0", newRest.toString(), null, null))
                        }
                    }

                }

                //Pause nach Übung hinzufügen
                //10% Chance, dass Pause zwischen Übungen wegfällt bzw. nur 10 Sekunden beträgt --> Supersatz.
                var newRest2 = workout.restExercices
                if(Math.random() < 0.1){
                   newRest2 = 10
                }

                json.workouts[0].exercices.add(ExerciceModel("0", newRest2.toString(), null, null))

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
