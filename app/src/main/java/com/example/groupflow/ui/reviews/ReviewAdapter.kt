package com.example.groupflow.ui.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupflow.R
import com.example.groupflow.core.domain.Review
import java.time.format.DateTimeFormatter

class ReviewAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ratingText: TextView = view.findViewById(R.id.textRating)
        val commentText: TextView = view.findViewById(R.id.textComment)
        val dateText: TextView = view.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.ratingText.text = "★".repeat(review.rating)
        holder.commentText.text = review.comment
        holder.dateText.text = review.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    override fun getItemCount() = reviews.size
}
