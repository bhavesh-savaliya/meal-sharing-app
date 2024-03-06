/**Created By Nirali Pandya
 * Date :2023-07-26
 * Time :12:21 p.m.
 * Project Name :Calculator
 *
 */
package com.capstone.group6.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.group6.MealApp
import com.capstone.group6.R
import com.capstone.group6.databinding.LayoutIngredientsBinding
import com.capstone.group6.ui.MealPlannerActivity
import com.capstone.group6.ui.interfaces.AdapterOnClick

class IngredientsAdapter(
    private val adapterOnClick: AdapterOnClick,
    private val ingredientsList: Array<String>,
    private val iconList: Array<Int>,
    private val activity: MealPlannerActivity
) :
    RecyclerView.Adapter<IngredientsAdapter.LanguageViewHolder>() {

    var ingredients = ingredientsList
    private val selected: ArrayList<String> = arrayListOf()


    companion object {
        var selectedItemPosition: Int = 0
        public var selectedIngredients: ArrayList<String> = arrayListOf()
    }

    class LanguageViewHolder(
        private val binding: LayoutIngredientsBinding, private val adapter: IngredientsAdapter
    ) : RecyclerView.ViewHolder(binding.root) {

        var cardLayout: LinearLayout? = null
        var emotion: TextView? = null
        fun bind(
            item: String,
            icon: Int,
            selected:ArrayList<String>,
            adapterOnClickListner: AdapterOnClick,

            ) {
            cardLayout = binding.cardLayout
            emotion = binding.emotion
            binding.apply {
                emotion.text = item
                iconGroup.setImageResource(icon)
            }
            binding.cardLayout.setOnClickListener {
                selectedItemPosition = adapterPosition
                selected.add(item)
                val isSelected = selectedIngredients.contains(item)
                if (isSelected) {
                    selectedIngredients = selectedIngredients.filter { it != item } as ArrayList<String>

                } else {
                    selectedIngredients += item.lowercase()
                }

                Log.d("bind:", "bind: $adapterPosition")
                adapter.notifyDataSetChanged()

            }
            MealApp.prefs1!!.saveStringArray("selectedIngredients",selectedIngredients)
            MealApp.prefs1!!.positionTone = adapterPosition



        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding =
            LayoutIngredientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding, this)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val currentIngrdient = ingredientsList[position]
        holder.bind(currentIngrdient, iconList[position],selected ,adapterOnClick)
        if (selected.contains(currentIngrdient)) {
            holder.cardLayout!!.setBackgroundResource(R.drawable.bg_round)
            holder.emotion?.setTextColor(activity.getColor(R.color.white))
        } else {
            holder.cardLayout!!.setBackgroundResource(R.drawable.bg_round_corner)
            holder.emotion?.setTextColor(activity.getColor(R.color.grey))
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        adapterOnClick.onClick(selectedIngredients, 0)
    }
}










