package com.example.workout.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

const val ROUTINES_PAGE_INDEX = 0
const val WORKOUTS_PAGE_INDEX = 1

class ViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount() = tabFragmentsCreator.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreator[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

    /**
     * Mapping of the ViewPager page indexes to their respective Fragments
     */
    private val tabFragmentsCreator: Map<Int, () -> Fragment> = mapOf(
        ROUTINES_PAGE_INDEX to { RoutinesFragment() },
        WORKOUTS_PAGE_INDEX to { WorkoutsFragment() }
    )
}