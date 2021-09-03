package com.example.workout.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutDetailBinding
import com.example.workout.ui.exercices.SimpleStringRecyclerViewAdapter

class WorkoutDetailFragment : Fragment() {

    private lateinit var menuItem: MenuItem
    //private val args: WorkoutDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentWorkoutDetailBinding
    private lateinit var adapter: SimpleStringRecyclerViewAdapter
    /*private val workout: Workout by lazy {
        args.workout
    }*/
    private lateinit var toolbar: Toolbar
    private var pausedTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        /*binding.apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }*/

        setupRecyclerView()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbarWithNavigation()

        /*
        onOptionsItemSelected()
        detailViewModel.start(workout.id)

        detailViewModel.workout.observe(viewLifecycleOwner) { workout ->
            updateMenuItemIcon(workout.isSaved)
        }

        detailViewModel.workoutTimeMillis.observe(viewLifecycleOwner) { workoutTimeMillis ->
            binding.workoutProgress.setDuration(workoutTimeMillis)
        }

        detailViewModel.savedPausedTime.observe(viewLifecycleOwner) { savedPausedTime ->
            detailViewModel.manageTimer(savedPausedTime)
        }

        detailViewModel.runningTime.observe(viewLifecycleOwner) {
            binding.workoutProgress.updateProgressBar(it)
        }

        detailViewModel.pausedWorkoutTimeMillis.observe(viewLifecycleOwner) {
            pausedTime = it
        }

         */
    }

    private fun setupToolbarWithNavigation() {
        toolbar = binding.toolbarDetail
        toolbar.navigationContentDescription = "Navigate Up"
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

/*
    private fun onOptionsItemSelected() {
        toolbar.setOnMenuItemClickListener {
            detailViewModel.setFavourite(workout)
            true
        }
    }*/

    private fun setupRecyclerView() {
        adapter = SimpleStringRecyclerViewAdapter(arrayListOf("Hallo", "Hallo2", "Hallo3", "Hallo4", "Hallo5", "Hallo6"))
        binding.apply {
            addExerciceList.adapter = adapter
            addExerciceList.layoutManager = LinearLayoutManager(addExerciceList.context)
        }
    }




    class SimpleStringRecyclerViewAdapter(
        private val values: List<String>
    ) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null
            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(R.id.workout_title)

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.routines_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position]
            holder.text.text = values[position]

            holder.view.setOnClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/

                //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine
                val args = Bundle()
                args.putParcelable("workout", null)
                holder.view.findNavController().navigate(R.id.navigation_workout_detail_exercice, args)
            }

        }

        override fun getItemCount(): Int = values.size
    }

}

