package com.example.workout.ui.home

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.HelperClass
import com.example.workout.HelperClassRoutine
import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutDetailAddBinding
import com.example.workout.db.Exercice
import com.example.workout.db.Workout

class RoutineDetailAddFragment : Fragment() {

    private lateinit var menuItem: MenuItem
    //private val args: WorkoutDetailAddFragmentArgs by navArgs()
    private lateinit var binding: FragmentWorkoutDetailAddBinding
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

        binding = FragmentWorkoutDetailAddBinding.inflate(inflater, container, false)
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
        //Warum Instanziierung der Klasse WorkoutDetailAddFragment? --> siehe https://discuss.kotlinlang.org/t/kotlin-constructor-of-inner-class-nested-can-be-called-only-with-receiver-of-containing-class/7700
        //zunächst leere ArrayList erzeugen
        adapter = RoutineDetailAddFragment().MyRecyclerViewAdapter(
            arrayListOf()
        )
        binding.apply {
            listExercices.adapter = adapter
            listExercices.layoutManager = LinearLayoutManager(listExercices.context)
        }

    }




    //"inner" Schlüsselwort, um von innen auf Variablen der äußeren Klasse zugreifen zu können
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


            override fun toString(): String {
                return super.toString() + " '" + text.text
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position].name
            holder.text.text = values[position].name

            holder.view.setOnClickListener { v ->

                HelperClassRoutine.workoutsToAdd.add(values[position])

                //zurück navigieren
                findNavController().navigateUp()

            }

        }

        override fun getItemCount(): Int = values.size
    }

}

