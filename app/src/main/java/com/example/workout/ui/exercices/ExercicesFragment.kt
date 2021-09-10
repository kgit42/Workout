package com.example.workout.ui.exercices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.databinding.FragmentExercicesBinding
import com.example.workout.db.Exercice
import com.example.workout.db.Routine
import com.example.workout.ui.home.RoutinesFragment

class ExercicesFragment : Fragment() {

    private lateinit var exercicesViewModel: ExercicesViewModel
    private lateinit var _binding: FragmentExercicesBinding
    private lateinit var adapter: SimpleStringRecyclerViewAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        exercicesViewModel =
            ViewModelProvider(this).get(ExercicesViewModel::class.java)

        _binding = FragmentExercicesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*val textView: TextView = binding.textNotifications
        statsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/

        setupRecyclerView()

        //Observer --> falls es Änderungen in DB gibt
        exercicesViewModel.getAllExercices().observe(viewLifecycleOwner) { exercices -> adapter.setData(exercices) }

        return root
    }

    /*
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
     */


    private fun setupRecyclerView() {
        adapter = SimpleStringRecyclerViewAdapter(arrayListOf(Exercice(0, "", "", "", "", false)))
        _binding.apply {
            listExercices.adapter = adapter
            listExercices.layoutManager = LinearLayoutManager(listExercices.context)
        }
    }

}

class SimpleStringRecyclerViewAdapter(
    private var values: List<Exercice>
) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {

    //um vom ViewModel aus Daten zu ändern
    fun setData(newData: List<Exercice>) {
        this.values = newData
        notifyDataSetChanged()
    }

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
        holder.boundString = values[position].name
        holder.text.text = values[position].name

        holder.view.setOnClickListener { v ->
            val context = v.context
            /*val intent = Intent(context, CheeseDetailActivity::class.java)
            intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
            context.startActivity(intent)*/
        }

    }



    override fun getItemCount(): Int = values.size
}




