package com.example.workout.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workout.databinding.FragmentStatsBinding
import com.example.workout.R
import android.content.Context
import android.util.Log
import android.widget.*
import com.example.workout.db.RoutineWorkoutStatsElement

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
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
                RoutineWorkoutStatsElement(0, 5, 3, "dfgdg", 0, 1633860131000)
            )
            statsViewModel.createRoutineWorkoutStatsElement(
                RoutineWorkoutStatsElement(0, 4, 5, "dg", 1, 1633168926000)
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

    //View des untergeordneten Elementes
    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as RoutineWorkoutStatsElement
        if (convertView == null) {
            val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.stats_view_item, null)
        }

        val image: ImageView = convertView?.findViewById(R.id.item_image)!!
        val text: TextView = convertView?.findViewById(R.id.item_title)!!
        val category: TextView = convertView?.findViewById(R.id.item_category)!!
        val time: TextView = convertView?.findViewById(R.id.item_time)!!

        text.text = expandedListText.name

        var string1: String
        var string2: String

        if(expandedListText.length!! > 1 || expandedListText.length == 0){
            string1 = "Minuten"
        }else{
            string1 = "Minute"
        }

        if(expandedListText.numberSetsDone!! > 1 || expandedListText.numberSetsDone == 0){
            string2 = "Sätze"
        }else{
            string2 = "Satz"
        }

        category.text = "${expandedListText.length} $string1, ${expandedListText.numberSetsDone} $string2"

        if(expandedListText.type == 1){
            image.setImageResource(R.drawable.ic_baseline_fitness_center_24)
        }else{
            image.setImageResource(R.drawable.ic_baseline_view_carousel_24)
        }


        //Calendar-Klasse nutzen, um aus timestamp Datum abzuleiten:
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = expandedListText.timestamp!!

        /*
        val mYear = calendar[Calendar.YEAR]
        val mMonth = calendar[Calendar.MONTH]
        val mDay = calendar[Calendar.DAY_OF_MONTH]
        val mHours = calendar[Calendar.HOUR]
        val mMinutes = calendar[Calendar.MINUTE]
         */

        //Datum & Zeit formatieren
        val format = SimpleDateFormat("dd.MM.yyyy hh:mm")
        time.text = format.format(calendar.time)



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

    //View des übergeordneteten Elementes
    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View?, parent: ViewGroup
    ): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as Int
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.stats_view_item_group, null)
        }

        //val image: ImageView = view.findViewById(R.id.item_image)
        val text: TextView = convertView?.findViewById(R.id.item_title)!!
        val category: TextView = convertView?.findViewById(R.id.item_category)!!

        //Anzahl der Sätze und Minuten insgesamt berechnen
        var counterMinutes = 0
        var counterSets = 0

        expandableListDetail[listTitle]?.forEach{
            counterMinutes += it.length!!
            counterSets += it.numberSetsDone!!
        }

        var string1: String
        var string2: String

        if(counterMinutes > 1 || counterMinutes == 0){
            string1 = "Minuten"
        }else{
            string1 = "Minute"
        }

        if(counterSets > 1 || counterSets == 0){
            string2 = "Sätze"
        }else{
            string2 = "Satz"
        }

        text.text = "KW ${listTitle.toString()}"
        category.text = "$counterMinutes $string1, $counterSets $string2"

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
