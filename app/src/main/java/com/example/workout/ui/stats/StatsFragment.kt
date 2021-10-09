package com.example.workout.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.workout.databinding.FragmentStatsBinding
import com.example.workout.R
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workout.db.Routine
import com.example.workout.db.RoutineWorkoutStatsElement
import com.example.workout.db.Workout
import com.example.workout.ui.exercices.ExercicesFragment

import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupCollapseListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList





class StatsFragment : Fragment() {

    private lateinit var statsViewModel: StatsViewModel
    private var _binding: FragmentStatsBinding? = null
    private lateinit var adapter: CustomExpandableListAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        statsViewModel =
            ViewModelProvider(this).get(StatsViewModel::class.java)

        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupListView()

        lifecycleScope.launch {
            statsViewModel.createRoutineWorkoutStatsElement(
                RoutineWorkoutStatsElement(0, 5, 3, "dfgdg", 0, 3453436)
            )
            statsViewModel.createRoutineWorkoutStatsElement(
                RoutineWorkoutStatsElement(0, 4, 5, "dg", 1, 345464)
            )
        }

        //Observer --> falls es Änderungen in DB gibt
        statsViewModel.getAllRoutineWorkoutStatsElements()
            .observe(viewLifecycleOwner) { routineWorkoutStatsElements ->

                //HashMap, die die Wochennummer einem Objekt zuordnet
                val expandableListDetail = HashMap<Int, MutableList<RoutineWorkoutStatsElement>>()

                routineWorkoutStatsElements.forEach{
                    //Calendar-Klasse nutzen, um aus timestamp Wochennummer (KW) abzuleiten:
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = it.timestamp!!

                    val mWeekNumber = calendar[Calendar.WEEK_OF_YEAR]

                    //Falls diese KW schon in der HashMap existiert, füge sie ein in die zugehörige MutableList.
                    //Sonst erstelle neue MutableList und füge ein.
                    if(expandableListDetail[mWeekNumber] != null){
                        expandableListDetail[mWeekNumber]?.add(it)
                    }else{
                        expandableListDetail[mWeekNumber] = mutableListOf(it)
                    }
                }

                val expandableListTitle = ArrayList<Int>(expandableListDetail.keys)

                //Daten an ExpandableList weitergeben
                adapter.setData(expandableListDetail, expandableListTitle) }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupListView() {

        adapter = CustomExpandableListAdapter(requireActivity(), arrayListOf(),
            hashMapOf())
        _binding?.apply {
            expandableListView.setAdapter(adapter)

            expandableListView.setOnGroupCollapseListener { groupPosition ->
                Log.v("hhh", "List collapsed")

            }

            expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                    Log.v("hhh", "onChildClick")

                false
            }


        }

    }



}



class CustomExpandableListAdapter(
    private val context: Context, private var expandableListTitle: List<Int>,
    private var expandableListDetail: HashMap<Int, MutableList<RoutineWorkoutStatsElement>>
) : BaseExpandableListAdapter() {

    //um vom ViewModel aus Daten zu ändern
    fun setData(newDataDetail: HashMap<Int, MutableList<RoutineWorkoutStatsElement>>, newDataTitle: List<Int>) {
        this.expandableListDetail = newDataDetail
        this.expandableListTitle = newDataTitle
        notifyDataSetChanged()
    }

    override fun getChild(listPosition: Int, expandedListPosition: Int): Any? {
        return expandableListDetail[expandableListTitle[listPosition]]
            ?.get(expandedListPosition)
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as RoutineWorkoutStatsElement
        if (convertView == null) {
            val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.view_item, null)
        }

        //val image: ImageView = view.findViewById(R.id.item_image)
        val text: TextView = convertView?.findViewById(R.id.item_title)!!
        val category: TextView = convertView?.findViewById(R.id.item_category)!!

        text.text = expandedListText.name

        //Calendar-Klasse nutzen, um aus timestamp Datum abzuleiten:
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = expandedListText.timestamp!!
        val mYear = calendar[Calendar.YEAR]
        val mMonth = calendar[Calendar.MONTH]
        val mDay = calendar[Calendar.DAY_OF_MONTH]
        val mHours = calendar[Calendar.HOUR]
        val mMinutes = calendar[Calendar.MINUTE]

        category.text = "$mDay.$mMonth.$mYear $mHours:$mMinutes"



        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return expandableListDetail[expandableListTitle[listPosition]]
            ?.size!!
    }

    override fun getGroup(listPosition: Int): Any {
        return expandableListTitle[listPosition]
    }

    override fun getGroupCount(): Int {
        return expandableListTitle.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as Int
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.view_item, null)
        }

        //val image: ImageView = view.findViewById(R.id.item_image)
        val text: TextView = convertView?.findViewById(R.id.item_title)!!
        val category: TextView = convertView?.findViewById(R.id.item_category)!!

        text.text = "KW ${listTitle.toString()}"
        category.text = "136 Minuten, 49 Übungen"

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
