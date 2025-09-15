package com.example.groupflow.ui.reviews

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupflow.R
import com.example.groupflow.core.domain.Review
import com.google.firebase.database.FirebaseDatabase
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ReviewAdapter(private val reviews: List<Review>,
                    private val coroutineScope: CoroutineScope) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ratingText: TextView = view.findViewById(R.id.textRating)
        val commentText: TextView = view.findViewById(R.id.textComment)
        val dateText: TextView = view.findViewById(R.id.textDate)

        val patientNameText: TextView = view.findViewById(R.id.textPatientName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        // Clear previous name to avoid showing incorrect data while loading
        holder.patientNameText.text = "Loading..."

        // Launch a coroutine to fetch the patient's name
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Fetch the name from Firebase using the patientId from the review
                val userSnapshot = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(review.patientId!!)
                    .child("name")
                    .get()
                    .await()

                val patientName = userSnapshot.getValue(String::class.java) ?: "Unknown Patient"

                // Switch back to the main thread to update the UI
                withContext(Dispatchers.Main) {
                    holder.patientNameText.text = patientName
                }
            } catch (e: Exception) {
                // Handle potential errors, e.g.
                withContext(Dispatchers.Main) {
                    holder.patientNameText.text = "Patient not found"
                }
                Log.e("ReviewAdapter", "Error fetching patient name: ${e.message}", e)
            }
        }

        holder.ratingText.text = "★".repeat(review.rating)
        holder.commentText.text = review.comment
        holder.dateText.text = review.createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    override fun getItemCount() = reviews.size
}
