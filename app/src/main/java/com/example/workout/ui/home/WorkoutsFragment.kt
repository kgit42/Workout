package com.example.workout.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.*
import com.example.workout.db.Workout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.util.*


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


        //RecyclerView: zunächst leere ArrayList mit Workouts
        val adapter =
            MyRecyclerViewAdapter(arrayListOf(Workout()))
        rv.adapter = adapter


        //Observer --> falls es Änderungen in DB gibt
        homeViewModel.getAllWorkouts()
            .observe(viewLifecycleOwner) { workouts -> adapter.setData(workouts) }

        return rv
    }


    override fun onResume() {
        super.onResume()

        //Ladesymbole ausblenden und Start-Buttons wieder einblenden
        hideLoadingAndShowStartButtons()


    }

    private fun hideLoadingAndShowStartButtons(){
        val recyclerview: RecyclerView? = view?.findViewById(R.id.recyclerview)
        //über den LayoutManager an Items der RecyclerView kommen
        for (j in 0..(recyclerview?.layoutManager?.itemCount!!)){
            recyclerview.layoutManager!!.findViewByPosition(j)?.findViewById<ProgressBar>(R.id.progress_loader)?.visibility = View.INVISIBLE
            recyclerview.layoutManager!!.findViewByPosition(j)?.findViewById<Button>(R.id.buttonPlay)?.visibility = View.VISIBLE
        }
    }


    override fun onPause() {
        super.onPause()

        //Log.v("hhh", "onPause")
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

            val image: ImageView = view.findViewById(R.id.item_image)
            val text: TextView = view.findViewById(R.id.item_title)
            val category: TextView = view.findViewById(R.id.item_category)

            val startButton: Button = view.findViewById(R.id.buttonPlay)
            val progressbar: ProgressBar = view.findViewById(R.id.progress_loader)

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.workouts_routines_view_item, parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position].name
            holder.text.text = values[position].name

            val numberSets = values[position].numberSets
            if(numberSets!! > 1){
                holder.category.text = "$numberSets Sätze"
            }else{
                holder.category.text = "$numberSets Satz"
            }



            holder.image.setImageResource(R.drawable.ic_baseline_fitness_center_24)

            holder.view.setOnClickListener { v ->
                val context = v.context

                //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine (bzw. die ID)
                val args = Bundle()
                args.putInt("wid", values[position].wid)

                if(values[position].type == 0){
                    holder.view.findNavController().navigate(R.id.navigation_workout_detail, args)
                }else{
                    holder.view.findNavController().navigate(R.id.navigation_workout_detail_superset, args)
                }

            }

            //OnLongClickListener zum Löschen
            holder.view.setOnLongClickListener { v ->
                val dialog = DeleteDialogFragment()
                val args = Bundle()
                args.putInt("wid", values[position].wid)
                dialog.arguments = args

                dialog.show(childFragmentManager, "")
                return@setOnLongClickListener true
            }

            //Start-Button OnClickListener
            holder.startButton.setOnClickListener { v ->

                //Ladesymbol einblenden
                holder.startButton.visibility = View.INVISIBLE
                holder.progressbar.visibility = View.VISIBLE

                //Workout generieren.
                generateWorkoutAndStartActivity(values[position].wid)


            }

        }

        override fun getItemCount(): Int = values.size


    }



    private fun generateWorkoutAndStartActivity(wid: Int) {
        //Wenn keine Übungen im Workout, nicht starten
        lifecycleScope.launch{
            var workout = homeViewModel.getWorkoutByIdAsync(wid)
            if(workout.exercices.size == 0){
                hideLoadingAndShowStartButtons()
                Snackbar.make(requireView(), "Fehler: Workout ist leer.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }else{
                val generator = Generator(homeViewModel, null, wid, context)
                generator.generateRoutineAndStartActivity()
            }
        }

    }
}
