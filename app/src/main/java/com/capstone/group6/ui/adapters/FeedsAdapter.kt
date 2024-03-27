/**Created By Nirali Pandya
 * Date :2024-01-20
 * Time :5:03 p.m.
 * Project Name :MVVMNewsApp
 *
 */
package com.capstone.group6.ui.adapters

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.group6.Constant.Companion.VIEW_TYPE_DETAILS
import com.capstone.group6.Constant.Companion.VIEW_TYPE_GRID
import com.capstone.group6.Constant.Companion.VIEW_TYPE_LIST
import com.capstone.group6.Constant.Companion.startActivity
import com.capstone.group6.R
import com.capstone.group6.databinding.ItemFeedBinding
import com.capstone.group6.databinding.ItemFeedGridBinding
import com.capstone.group6.databinding.ItemFeedTileBinding
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.ui.FeedDetailsActivity
import com.capstone.group6.ui.interfaces.BookmarkClickEvent
import java.util.Random


class FeedsAdapter(
    private var feedList: ArrayList<Meal>,
    var activity: FragmentActivity,
    private val currentView: Int,
    private val bookmarkClickEvent: BookmarkClickEvent,
    val fromUser: Boolean = false

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var currentViewType: Int = currentView

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
        val bookmark = binding.ivBookmark
        val share = binding.ivShare
    }

    inner class ListViewHolder(binding: ItemFeedTileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle: TextView = binding.tvTitle
        val ivImage = binding.ivFeedImage
        val userName = binding.tvUserName
        val bookmark = binding.ivBookmark
        val share = binding.ivShare

    }

    inner class GridViewHolder(binding: ItemFeedGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val tvTitle: TextView = binding.tvTitle
        val ivImage = binding.ivFeedImage
        val userName = binding.tvUserName
        val bookmark = binding.ivBookmark
        val share = binding.ivShare


    }

    private val TAG = "FeedsAdapter"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_LIST -> ListViewHolder(
                ItemFeedTileBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_GRID -> GridViewHolder(
                ItemFeedGridBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            VIEW_TYPE_DETAILS -> ArticleViewHolder(
                ItemFeedBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return feedList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val feed = feedList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_LIST -> bindListView(holder as ListViewHolder, feed, position)
            VIEW_TYPE_GRID -> bindGridView(holder as GridViewHolder, feed, position)
            VIEW_TYPE_DETAILS -> bindDetailsView(holder as ArticleViewHolder, feed, position)
        }

    }

    private fun bindGridView(holder: FeedsAdapter.GridViewHolder, feed: Meal, position: Int) {
        holder.itemView.apply {
            val uri: Uri? = if (feed.image.isNullOrEmpty()) null else Uri.parse(feed.image)

            Glide.with(this).load(uri).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(holder.ivImage)

            if (fromUser) {
                holder.share.visibility = View.VISIBLE
            } else {
                holder.share.visibility = View.GONE
            }
            holder.tvTitle.text = feed.title
            val fullText = "Dietary Tag: ${feed.dietarytags.name}"
            val spannable = SpannableString(fullText)
            val startIndex = fullText.indexOf(feed.dietarytags.name)
            val endIndex = startIndex + feed.dietarytags.name.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.userName.text = feed.userData?.name ?: ""
            isGridLike(feed, holder)
            // feed.userData?.name?.let { setUserDetails(holder.userImage, holder.prefix, it) }

            setOnClickListener {
                activity.startActivity(FeedDetailsActivity::class.java, position)

            }
            holder.bookmark.setOnClickListener {
                isGridLike(feed, holder)
                feed.let {
                    feed.isLike = !feed.isLike

                }
                bookmarkClickEvent.onBookMarkSaved(position, feed)

            }

            holder.share.setOnClickListener {

            }
        }
    }

    private fun bindDetailsView(holder: ArticleViewHolder, feed: Meal, position: Int) {
        holder.itemView.apply {
            val uri: Uri? = if (feed.image.isNullOrEmpty()) null else Uri.parse(feed.image)

            Glide.with(this).load(uri).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(holder.ivImage)

            if (fromUser) {
                holder.share.visibility = View.VISIBLE
            } else {
                holder.share.visibility = View.GONE
            }
            holder.tvTitle.text = feed.title
            holder.tvDescription.text = feed.description
            holder.cuisineType.text = "Cuisine Type: ${feed.cuisineType.name}"
            holder.mealType.text = "Meal Type: ${feed.type}"
            val fullText = "Dietary Tag: ${feed.dietarytags.name}"
            val spannable = SpannableString(fullText)
            val startIndex = fullText.indexOf(feed.dietarytags.name)
            val endIndex = startIndex + feed.dietarytags.name.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.mealType.text = "Meal Type: ${feed.type}"
            holder.dietaryTag.text = spannable
            holder.totalLikes.text = "" + feed.likes
            holder.userName.text = feed.userData?.name ?: ""
            isLike(feed, holder)
            feed.userData?.name?.let { setUserDetails(holder.userImage, holder.prefix, it) }

            setOnClickListener {
                activity.startActivity(FeedDetailsActivity::class.java, position)

            }
            holder.bookmark.setOnClickListener {
                isLike(feed, holder)
                feed.let {
                    feed.isLike = !feed.isLike
                }
                bookmarkClickEvent.onBookMarkSaved(position, feed)

            }

            holder.share.setOnClickListener {


            }
        }
    }

    private fun bindListView(holder: ListViewHolder, feed: Meal, position: Int) {
        holder.itemView.apply {
            val uri: Uri? = if (feed.image.isNullOrEmpty()) null else Uri.parse(feed.image)

            Glide.with(this).load(uri).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(holder.ivImage)

            if (fromUser) {
                holder.share.visibility = View.VISIBLE
            } else {
                holder.share.visibility = View.GONE
            }
            holder.tvTitle.text = feed.title
            val fullText = "Dietary Tag: ${feed.dietarytags.name}"
            val spannable = SpannableString(fullText)
            val startIndex = fullText.indexOf(feed.dietarytags.name)
            val endIndex = startIndex + feed.dietarytags.name.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.userName.text = feed.userData?.name ?: ""
            isListLike(feed, holder)
            //feed.userData?.name?.let { setUserDetails(holder.userImage, holder.prefix, it) }

            setOnClickListener {
                activity.startActivity(FeedDetailsActivity::class.java, position)

            }
            holder.bookmark.setOnClickListener {
                isListLike(feed, holder)
                feed.let {
                    feed.isLike = !feed.isLike
                }
                bookmarkClickEvent.onBookMarkSaved(position, feed)

            }

            holder.share.setOnClickListener {

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

    fun isLike(feed: Meal, holder: ArticleViewHolder) {
        if (feed.isLike) {
            holder.bookmark.setImageResource(R.drawable.ic_boomark_select)
        } else {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_unselect)
        }
    }

    fun isListLike(feed: Meal, holder: ListViewHolder) {
        if (feed.isLike) {
            holder.bookmark.setImageResource(R.drawable.ic_boomark_select)
        } else {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_unselect)
        }
    }

    private fun isGridLike(feed: Meal, holder: GridViewHolder) {
        if (feed.isLike) {
            holder.bookmark.setImageResource(R.drawable.ic_boomark_select)
        } else {
            holder.bookmark.setImageResource(R.drawable.ic_bookmark_unselect)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentViewType
    }

    fun updateViewType(newViewType: Int) {
        currentViewType = newViewType
        notifyDataSetChanged()
    }

}