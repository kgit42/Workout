package com.example.workout.ui.home

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.example.workout.HelperClassRoutine
import com.example.workout.R
import com.example.workout.databinding.FragmentRoutineDetailAddBinding
import com.example.workout.db.Workout


class RoutineDetailAddFragment : Fragment() {

    private lateinit var menuItem: MenuItem
    //private val args: WorkoutDetailAddFragmentArgs by navArgs()
    private lateinit var binding: FragmentRoutineDetailAddBinding
    private lateinit var adapter: MyRecyclerViewAdapter
    /*private val workout: Workout by lazy {
        args.workout
    }*/
    private lateinit var toolbar: Toolbar
    private var pausedTime: Long = 0

    private lateinit var homeViewModel: HomeViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentRoutineDetailAddBinding.inflate(inflater, container, false)
        /*binding.apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }*/

        setupRecyclerView()

        //Observer --> falls es Änderungen in DB gibt
        homeViewModel.getAllWorkouts().observe(viewLifecycleOwner) { workouts ->
            adapter.setData(workouts) }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbarWithNavigation()

    }

    private fun setupToolbarWithNavigation() {
        toolbar = binding.toolbarDetail
        toolbar.navigationContentDescription = "Navigate Up"
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }



    private fun setupRecyclerView() {

        //zunächst leere ArrayList erzeugen
        adapter = MyRecyclerViewAdapter(
            arrayListOf()
        )
        binding.apply {
            listExercices.adapter = adapter
            listExercices.layoutManager = LinearLayoutManager(listExercices.context)
        }

    }



    //"inner" Schlüsselwort, um von innen auf Variablen der äußeren Klasse zugreifen zu können
    inner class MyRecyclerViewAdapter(
        //Konstruktor:
        private var values: List<Workout>,
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


            override fun toString(): String {
                return super.toString() + " '" + text.text
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.add_workout_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position].name
            holder.text.text = values[position].name

            val numberExercices = values[position].exercices.size
            if(numberExercices > 1){
                holder.category.text = "$numberExercices Übungen"
            }else{
                holder.category.text = "$numberExercices Übung"
            }

            if(values[position].type == 0){
                holder.image.setImageResource(R.drawable.ic_baseline_fitness_center_24)
            }else{
                holder.image.setImageResource(R.drawable.ic_baseline_fitness_center_24_superset)
            }


            holder.view.setOnClickListener { v ->

                HelperClassRoutine.allWorkouts.add(values[position])

                //zurück navigieren
                holder.view.findNavController().navigateUp()

            }

        }


        override fun getItemCount(): Int = values.size
    }

}




