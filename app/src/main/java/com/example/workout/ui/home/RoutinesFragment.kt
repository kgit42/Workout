package com.example.workout.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.db.Routine
import com.example.workout.ui.exercices.ExercicesViewModel
import java.util.ArrayList

class RoutinesFragment : Fragment() {

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

        //zunächst leere ArrayList mit Routinen
        val adapter = SimpleStringRecyclerViewAdapter(arrayListOf(Routine(0, "", "")))
        rv.adapter = adapter


        //Observer --> falls es Änderungen in DB gibt
        homeViewModel.getAllRoutines().observe(viewLifecycleOwner) { routines -> adapter.setData(routines) }

        return rv
    }


    class SimpleStringRecyclerViewAdapter(
        private var values: List<Routine>
    ) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {

        //um vom ViewModel aus Daten zu ändern
        fun setData(newData: List<Routine>) {
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
            //Elvis-Operator für Null-Check:
            holder.boundString = values[position].name
            holder.text.text = values[position].name

            holder.view.setOnClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/

                //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine
                val args = Bundle()
                args.putParcelable("workout", null)
                holder.view.findNavController().navigate(R.id.navigation_routine_detail, args)
            }

            holder.view.setOnLongClickListener { v ->
                return@setOnLongClickListener true
            }

        }

        override fun getItemCount(): Int {
                return values.size

        }

    }
}

