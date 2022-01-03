package com.example.workout.ui.exercices

import android.app.Application
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.HelperClass
import com.example.workout.R
import com.example.workout.databinding.FragmentExercicesBinding
import com.example.workout.db.AppDatabase
import com.example.workout.db.Exercice
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ExercicesFragment : Fragment() {

    private lateinit var exercicesViewModel: ExercicesViewModel
    private lateinit var _binding: FragmentExercicesBinding
    private lateinit var adapter: MyRecyclerViewAdapter

    private lateinit var toolbar: Toolbar

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

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
        onOptionsItemSelected()

        //Observer --> falls es Änderungen in DB gibt
        exercicesViewModel.getAllExercices()
            .observe(viewLifecycleOwner) { exercices -> adapter.setData(exercices) }

        return root
    }

    /*
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
     */


    private fun setupRecyclerView() {
        //zunächst leere ArrayList erzeugen
        adapter = MyRecyclerViewAdapter(arrayListOf())
        _binding.apply {
            listExercices.adapter = adapter
            listExercices.layoutManager = LinearLayoutManager(listExercices.context)
        }
    }


    //Import-Button
    private fun onOptionsItemSelected() {
        toolbar = binding.toolbarExercices
        toolbar.setOnMenuItemClickListener {

            runBlocking {
                launch(Dispatchers.IO) {
                    //zunächst alle Exercices aus DB löschen:
                    withContext(Dispatchers.IO) {
                        AppDatabase.getInstance(requireContext()).exerciceDao().deleteAll()
                    }

                    //dann neu importieren:
                    try {
                        context?.resources?.openRawResource(R.raw.exercices).use { inputStream ->
                            JsonReader(inputStream?.reader()).use { jsonReader ->
                                val type = object : TypeToken<List<Exercice>>() {}.type
                                val exerciceList: List<Exercice> = Gson().fromJson(jsonReader, type)

                                withContext(Dispatchers.IO) {
                                    AppDatabase.getInstance(requireContext()).exerciceDao()
                                        .insertAll(exerciceList)

                                }

                            }
                        }
                        Log.v("hhh", "success")
                    } catch (e: Exception) {
                        Log.v("hhh", "failure$e")
                    }
                }
            }



            return@setOnMenuItemClickListener true


        }

    }


    inner class MyRecyclerViewAdapter(
        private var values: List<Exercice>
    ) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

        //um vom ViewModel aus Daten zu ändern
        fun setData(newData: List<Exercice>) {
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
                R.layout.view_item, parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position].name
            holder.text.text = values[position].name
            holder.category.text = values[position].category


            //Bild suchen
            val res: Resources = resources
            val mDrawableName1 = values[position].animation
            if(!mDrawableName1.equals("")){
                //Dateiendung entfernen
                val mDrawableName = mDrawableName1?.substring(0, mDrawableName1.lastIndexOf('.'))
                val resID: Int = res.getIdentifier(mDrawableName, "drawable", context?.getPackageName())
                val drawable: Drawable? = ContextCompat.getDrawable(context!!, resID)
                //Bild setzen
                holder.image.setImageDrawable(drawable)
            }else{
                holder.image.setImageResource(R.drawable.ic_baseline_image_24)
            }


            holder.view.setOnClickListener { v ->

            }

        }


        override fun getItemCount(): Int = values.size
    }
}




