package com.chatanoga.cab.rider.activities.main.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.chatanoga.cab.common.models.ServiceCategory
import com.chatanoga.cab.rider.activities.main.fragments.ServiceCarousalFragment
import java.util.*

class ServiceCategoryViewPagerAdapter(manager: FragmentManager?, private var list: List<ServiceCategory>, private val distance: Int, private val duration: Int, private val currency: String) : FragmentStatePagerAdapter(manager!!) {
    override fun getItem(position: Int): Fragment {
        return ServiceCarousalFragment.newInstance(list[position].services!!, distance, duration, currency)
    }

    fun setItems(list: ArrayList<ServiceCategory>) {
        this.list = list
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return list[position].title
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

}