/**Created By Nirali Pandya
 * Date :2024-01-20
 * Time :5:03 p.m.
 * Project Name :MVVMNewsApp
 *
 */
package com.capstone.group6.ui.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.group6.Constant.Companion.startActivity
import com.capstone.group6.R
import com.capstone.group6.databinding.ItemFeedBinding
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.ui.FeedDetailsActivity
import java.util.Random



class FeedsAdapter(private var feedList: ArrayList<Meal>, public var activity: Activity) :
    RecyclerView.Adapter<FeedsAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle: TextView = binding.tvTitle
        val tvDescription = binding.tvDescription
        val ivImage = binding.ivFeedImage
        val totalLikes = binding.totalLikes
        val cuisineType = binding.tvCuisineType
        val dietaryTag = binding.tvDietaryType
        val userName = binding.tvUserName
        val userImage = binding.ivUserImage
        val prefix = binding.tvPrefix
        val mealType = binding.tvMealType
    }

    private val TAG = "FeedsAdapter"

    private val differCallback = object : DiffUtil.ItemCallback<Meal>() {

        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            ItemFeedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val feed = feedList[position]
        holder.itemView.apply {
            val uri: Uri? = if (feed.image.isNullOrEmpty()) null else Uri.parse(feed.image)

            Glide.with(this).load(uri).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(holder.ivImage)
            holder.tvTitle.text = feed.title
            holder.tvDescription.text = feed.description
            holder.cuisineType.text = "Cuisine Type: ${feed.cuisineType.name}"
            holder.mealType.text ="Meal Type: ${feed.Type}"
            val fullText = "Dietary Tag: ${feed.dietarytags.name}"
            val spannable = SpannableString(fullText)
            val startIndex = fullText.indexOf(feed.dietarytags.name)
            val endIndex = startIndex + feed.dietarytags.name.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.mealType.text = "Meal Type: ${feed.Type}"
            holder.dietaryTag.text = spannable
            holder.totalLikes.text = "" + feed.likes
            holder.userName.text = feed.user?.name ?: ""

            feed.user?.name?.let { setUserDetails(holder.userImage, holder.prefix, it) }

            setOnClickListener {
                activity.startActivity(FeedDetailsActivity::class.java,position)

            }
        }
    }

    fun setFilterList(newList: ArrayList<Meal>) {
        feedList = newList
        notifyDataSetChanged()

    }


    fun setUserDetails(ivUserImage: ImageView, textView: TextView, userName: String) {
        val androidColors: IntArray = activity.resources.getIntArray(R.array.androidcolors)
        val randomColor = androidColors[Random().nextInt(androidColors.size)]

        ivUserImage.setBackgroundResource(R.drawable.circle)


        val backgroundDrawable = ivUserImage.background as GradientDrawable

        backgroundDrawable.setColor(randomColor)

        val prefix = userName.substring(0, 1).uppercase()
        textView.text = prefix
        Log.d(TAG, "setUserDetails: $prefix$randomColor")
    }


}