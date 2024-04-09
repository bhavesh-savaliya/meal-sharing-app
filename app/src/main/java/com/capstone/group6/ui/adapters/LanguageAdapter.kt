/**Created By Nirali Pandya
 * Date :2023-07-26
 * Time :12:21 p.m.
 * Project Name :Calculator
 *
 */
package com.ai.emailassistant.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.capstone.group6.MealApp
import com.capstone.group6.R
import com.capstone.group6.databinding.ListItemBinding
import com.capstone.group6.feature_meal.domain.model.Language
import com.capstone.group6.ui.interfaces.AdapterOnClick
import com.google.android.material.bottomsheet.BottomSheetDialog

class LanguageAdapter(
    val adapterOnClick: AdapterOnClick,
    private val LanguageList: ArrayList<Language>,
    val activity: AppCompatActivity,
) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    var languagelist = LanguageList
    var languageActivity = activity
    var selectedItemPosition: Int = -1



    class LanguageViewHolder(
        private val binding: ListItemBinding,
        private val adapter: LanguageAdapter
    ) : RecyclerView.ViewHolder(binding.root) {
        var container: RelativeLayout? = null
        fun bind(
            item: Language,
            languageActivity: AppCompatActivity,
            adapterOnClick: AdapterOnClick,

        ) {
            container = binding.container

            binding.apply {
                // Assuming you have a 'setStatus' function in the binding class to set the status
                // Assuming you have an ImageView and a TextView in the list_item.xml layout with IDs 'flagImage' and 'languageName'
                flagImage.setImageResource(item.flag)
                name.text = item.language
                Log.d("TAG", "onBindViewHolder:pos "+    MealApp.prefs1!!.position+">>"+adapterPosition)


                binding.name.setOnClickListener {
                    binding.container!!.setBackgroundResource(R.drawable.bg_round)
                    adapter.selectedItemPosition = adapterPosition
                    adapterOnClick.onClickLanguage(
                        item.language,
                        adapterPosition
                    )
                    adapter.notifyDataSetChanged()



                    // container.setBackgroundResource(R.drawable.btn_bg_select)
                }


            }
            setSelected()
        }

        fun setSelected() {
            // selectedItemPosition = pos
            //adapter.notifyDataSetChanged()
        }
    }

    // ...

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageViewHolder(binding, this)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val currentLanguage = languagelist[position]
        holder.bind(currentLanguage, languageActivity, adapterOnClick)


        if (selectedItemPosition == position)
            holder.container!!.setBackgroundResource(R.drawable.bg_round)
        else
            holder.container!!.setBackgroundResource(0)


        if (MealApp.prefs1!!.position!! == position)
            holder.container!!.setBackgroundResource(R.drawable.bg_round)

    }

    override fun getItemCount(): Int {
        // You should return the actual number of items in the list
        return languagelist.size
    }


}










