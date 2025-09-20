package com.example.groupflow.ui.notifications

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groupflow.databinding.ItemNotificationBinding
import com.example.groupflow.models.NotificationModel
import com.example.groupflow.ui.appointments.AppointmentsActivity
import com.example.groupflow.ui.hubs.EmployeeHubActivity
import com.example.groupflow.ui.ultrascans.UltrascansActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class NotificationsAdapter(
    private val notifications: MutableList<NotificationModel>,
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {
    inner class NotificationViewHolder(
        val binding: ItemNotificationBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationModel) {
            // Set message
            binding.notificationMessage.text = notification.message

            // Timestamp formatting
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            binding.notificationTimestamp.text = sdf.format(Date(notification.timestamp))

            // Read/unread styling
            if (notification.read) {
                binding.notificationMessage.setTypeface(null, Typeface.NORMAL)
                binding.root.setBackgroundColor(binding.root.context.getColor(android.R.color.white))
            } else {
                binding.notificationMessage.setTypeface(null, Typeface.BOLD)
                binding.root.setBackgroundColor(binding.root.context.getColor(android.R.color.darker_gray))
            }

            // On click: mark as read
            binding.root.setOnClickListener {
                // Mark notification as read
                if (!notification.read) {
                    notification.read = true
                    notifyItemChanged(absoluteAdapterPosition)

                    // Update notification in Firebase
                    val db = FirebaseDatabase.getInstance().getReference("notifications")
                    db.child(notification.id).child("read").setValue(true)
                }

                // Navigate to correct activity
                val userRef = FirebaseDatabase.getInstance().getReference("users")
                val userRole = userRef.child(notification.recipientId).child("role")

                // Navigate based on type
                val context = binding.root.context
                when (notification.type) {
                    // For Ultrascans notification, navigate to UltrascansActivity
                    "ULTRASCAN" -> {
                        if (userRole.toString() == "EMPLOYEE") {
                            val intent = Intent(context, EmployeeHubActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            val intent =
                                Intent(context, UltrascansActivity::class.java).apply {
                                    putExtra("scanId", notification.relatedId)
                                }
                            context.startActivity(intent)
                        }
                    }
                    // For Appointments notification, navigate to AppointmentsActivity
                    "APPOINTMENT" -> {
                        if (userRole.toString() == "EMPLOYEE") {
                            val intent = Intent(context, EmployeeHubActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            val intent =
                                Intent(context, AppointmentsActivity::class.java).apply {
                                    putExtra("appointmentId", notification.relatedId)
                                }
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NotificationViewHolder {
        val binding =
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return NotificationViewHolder(binding)
    }

    override fun getItemCount(): Int = notifications.size

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int,
    ) {
        holder.bind(notifications[position])
    }
}
