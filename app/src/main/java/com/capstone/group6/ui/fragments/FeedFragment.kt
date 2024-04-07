package com.capstone.group6.ui.fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.group6.Constant.Companion.VIEW_TYPE_DETAILS
import com.capstone.group6.Constant.Companion.VIEW_TYPE_GRID
import com.capstone.group6.Constant.Companion.VIEW_TYPE_LIST
import com.capstone.group6.Constant.Companion.startActivity
import com.capstone.group6.MealApp
import com.capstone.group6.R
import com.capstone.group6.databinding.FilterLayoutBinding
import com.capstone.group6.databinding.FragmentFeedBinding
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import com.capstone.group6.ui.MainActivity
import com.capstone.group6.ui.MealPlannerActivity
import com.capstone.group6.ui.adapters.FeedsAdapter
import com.capstone.group6.ui.interfaces.BookmarkClickEvent
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class FeedFragment : Fragment(), BookmarkClickEvent {

    private val TAG: String? = "FeedFragment"
    private val mealsViewModel: MealsViewModel by activityViewModels()
    private lateinit var feedsAdapter: FeedsAdapter
    private lateinit var binding: FragmentFeedBinding
    lateinit var toolbar: Toolbar
    private var mealMutableList: ArrayList<Meal> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFeedBinding.inflate(inflater)
        setUpRecyclerView()
        readFirebaseData()
        setUpSearch()
        return binding.root
    }

    private fun setToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu);
                val menuItem = menu.findItem(R.id.item_search)
                val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView
                searchView.setQuery(getString(R.string.search_feeds_here), true)

                searchView.setOnQueryTextListener(object :
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        filterList(query)
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        filterList(newText)
                        return false
                    }

                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val id: Int = menuItem.itemId

                if (id == R.id.theme) {
                   themeDialog()
                }
                if (id == R.id.filter) {
                    filterSet()
                }
                if (id == R.id.add) {
                    activity?.startActivity(MealPlannerActivity::class.java)
                }
                if (id == R.id.viewTypeGrid) {
                    updateGridSpanCount(2)
                    MealApp.prefs1?.span =1
                    feedsAdapter.updateViewType(VIEW_TYPE_GRID)


                }
                if (id == R.id.viewTypeDetails) {
                    updateGridSpanCount(1)
                    MealApp.prefs1?.span =2
                    feedsAdapter.updateViewType(VIEW_TYPE_DETAILS)

                }

                if (id == R.id.viewTypeTile) {
                    updateGridSpanCount(1)
                    MealApp.prefs1?.span =0
                    feedsAdapter.updateViewType(VIEW_TYPE_LIST)


                }

                return true
            }


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun setUpSearch() {
        toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).title = getString(R.string.app_name)
        toolbar.setTitleTextColor(Color.WHITE)
        setToolbarMenu()


    }

    private fun filterList(query: String?) {
        val queryTo = query?.lowercase() ?: ""
        var filterList = arrayListOf<Meal>()
        mealMutableList.filter { item ->
            item.title?.lowercase()?.contains(queryTo) == true ||
                    item.cuisineType.name?.lowercase()?.contains(queryTo) == true ||
                    item.dietarytags.name?.lowercase()?.contains(queryTo) == true
        }.forEach { filteredItem ->
            filterList.add(filteredItem)
        }

        if (filterList.isEmpty()) {

        } else {
            feedsAdapter.setFilterList(filterList)
        }

    }


    private fun setUpRecyclerView() {
        feedsAdapter = FeedsAdapter(mealMutableList, activity!!, MealApp.prefs1?.span!!, this)
        binding.rvFeeds.apply {
            adapter = feedsAdapter
            layoutManager = GridLayoutManager(activity, 1)
        }
    }

    private fun updateGridSpanCount(spanCount: Int) {
        val layoutManager = binding.rvFeeds.layoutManager
        if (layoutManager is GridLayoutManager) {
            layoutManager.spanCount = spanCount
            feedsAdapter.notifyDataSetChanged()
        }
    }

    private fun readFirebaseData() {
        mealsViewModel.readFireStoreData()
        lifecycleScope.launch {
            mealsViewModel.fetchMeals(false).collect { meals ->
                Log.d(TAG, "readFirebaseData: ${meals}")
                mealMutableList = meals as ArrayList<Meal>
                feedsAdapter.setFilterList(mealMutableList)
            }

        }
    }

    private fun filterSet() {
        val builder = context?.let { AlertDialog.Builder(it, R.style.CustomAlertDialog) }

        val filterLayoutBinding = FilterLayoutBinding.inflate(layoutInflater);

        val dialog: AlertDialog = builder!!.create()

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        dialog.setContentView(filterLayoutBinding.root);

        val spinner = filterLayoutBinding.spinnerMealType
        val adapter = context?.let {
            ArrayAdapter.createFromResource(
                it,
                R.array.meal_types, android.R.layout.simple_spinner_item
            )
        }
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setSelection(0)
        spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                    (parent.getChildAt(0) as TextView)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        spinner.adapter = adapter
        dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        filterLayoutBinding.buttonApplyFilter.setOnClickListener {
            val selectedMealType = filterLayoutBinding.spinnerMealType.selectedItem as String
            filterLayoutBinding.spinnerMealType.background.setColorFilter(
                getResources().getColor(R.color.black),
                PorterDuff.Mode.SRC_ATOP
            );
            applyFilter(filterLayoutBinding)
            dialog.dismiss()
        }
        filterLayoutBinding.buttonReset.setOnClickListener {
            readFirebaseData()
            dialog.dismiss()
        }
        filterLayoutBinding.close.setOnClickListener { dialog.dismiss() }

    }

    private fun applyFilter(filterLayoutBinding: FilterLayoutBinding) {
        val mealType = filterLayoutBinding.spinnerMealType.selectedItem.toString()

        val ingredients = filterLayoutBinding.editTextIngredients.text.toString().lowercase()
        val vegetarian = filterLayoutBinding.checkboxVegetarian.isChecked
        val vegan = filterLayoutBinding.checkboxVegan.isChecked
        val dairy = filterLayoutBinding.checkboxDairyfree.isChecked
        val gluten = filterLayoutBinding.checkboxGluten.isChecked
        lifecycleScope.launch {
            mealsViewModel.filterMeals(
                mealType,
                ingredients,
                vegetarian,
                vegan,
                dairy,
                gluten,
                true
            ).collect()
            {
                Log.d(TAG, "applyFilter: $it")
                mealMutableList = it as ArrayList<Meal>
                feedsAdapter.setFilterList(mealMutableList)
            }
        }

    }

    override fun onBookMarkSaved(position: Int, feed: Meal) {
        (activity as MainActivity).update(feed);
    }

    override fun feedShare(feed: Meal) {

    }

    private fun themeDialog() {
        val bottomSheetDialog = BottomSheetDialog(context!!)
        bottomSheetDialog.setContentView(R.layout.list_theme)

        var dark = bottomSheetDialog.findViewById<RadioButton>(R.id.dark)!!
        var light = bottomSheetDialog.findViewById<RadioButton>(R.id.light)!!

        if (MealApp.prefs1!!.theme.equals("dark")) {
            dark.isChecked = true
        } else {
            light.isChecked = true
        }

        dark.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {
                bottomSheetDialog.dismiss()
                MealApp.prefs1!!.theme = "dark"
                changeTheme()
            }
        }

        light.setOnCheckedChangeListener { p0, p1 ->
            if (p1) {
                bottomSheetDialog.dismiss()
                MealApp.prefs1!!.theme = "light"
                changeTheme()
            }
        }


        if (!activity!!.isFinishing)
            bottomSheetDialog.show()
    }

    fun changeTheme() {
        // Switch to the opposite theme mode
        val newNightMode =
            if (MealApp.prefs1!!.theme.equals("dark")) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        if (currentNightMode != newNightMode) {

            // Set the new theme mode
            AppCompatDelegate.setDefaultNightMode(newNightMode)

        }
    }


}