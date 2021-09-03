package com.example.workout.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import java.util.ArrayList

class WorkoutsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rv = inflater.inflate(
            R.layout.fragment_list,
            container,
            false
        ) as RecyclerView
        rv.layoutManager = LinearLayoutManager(rv.context)
        rv.adapter = SimpleStringRecyclerViewAdapter(arrayListOf("Hallo", "Hallo2"))

        return rv
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
                R.layout.workouts_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position]
            holder.text.text = values[position]

            holder.view.setOnLongClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/

                //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine
                val args = Bundle()
                args.putParcelable("workout", null)
                holder.view.findNavController().navigate(R.id.navigation_workout_detail, args)
                return@setOnLongClickListener true
            }

        }

        override fun getItemCount(): Int = values.size
    }
}
