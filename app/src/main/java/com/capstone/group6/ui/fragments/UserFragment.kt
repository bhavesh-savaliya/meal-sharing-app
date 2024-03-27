package com.capstone.group6.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.group6.Constant.Companion.VIEW_TYPE_DETAILS
import com.capstone.group6.databinding.FragmentUserBinding
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import com.capstone.group6.ui.MainActivity
import com.capstone.group6.ui.adapters.FeedsAdapter
import com.capstone.group6.ui.interfaces.BookmarkClickEvent
import kotlinx.coroutines.launch


class UserFragment : Fragment(), BookmarkClickEvent {
    private lateinit var binding: FragmentUserBinding
    private val mealsViewModel: MealsViewModel by activityViewModels()
    private var mealMutableList: ArrayList<Meal> = arrayListOf()
    private lateinit var feedsAdapter: FeedsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        binding = FragmentUserBinding.inflate(inflater)
        setUpRecyclerView()
        readData()
        return binding.root

    }

    private fun setUpRecyclerView() {
        feedsAdapter = FeedsAdapter(mealMutableList, activity!!, VIEW_TYPE_DETAILS,this, true )
        binding.rvFeeds.apply {
            adapter = feedsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun readData() {
        lifecycleScope.launch {
            mealsViewModel.fetchMeals(true).collect { meals ->
                mealMutableList = meals as ArrayList<Meal>
                if (mealMutableList.isEmpty()) {
                    binding.noText.visibility = View.VISIBLE
                } else {
                    binding.noText.visibility = View.GONE
                }
                feedsAdapter.setFilterList(mealMutableList)
            }

        }
    }

    override fun onBookMarkSaved(position: Int, feed: Meal) {
    }

    override fun feedShare(feed: Meal) {
        lifecycleScope.launch {
            feed.apply {
                this.isLocal = false
            }
            mealsViewModel.updateData(feed)

            (activity as MainActivity).feedShare(feed)
        }
    }


}