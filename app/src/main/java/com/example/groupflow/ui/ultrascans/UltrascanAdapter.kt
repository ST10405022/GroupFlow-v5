package com.example.groupflow.ui.ultrascans

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.groupflow.databinding.ItemUltrascanBinding
import com.example.groupflow.models.UltrascanModel

class UltrascanAdapter(
    private val context: Context,
    private val scans: List<UltrascanModel>
) : RecyclerView.Adapter<UltrascanAdapter.UltrascanViewHolder>() {

    inner class UltrascanViewHolder(val binding: ItemUltrascanBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UltrascanViewHolder {
        val binding =
            ItemUltrascanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UltrascanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UltrascanViewHolder, position: Int) {
        val scan = scans[position]
        holder.binding.fileNameTextView.text = "Scan ${position + 1}"

        // Download button
        holder.binding.btnDownload.setOnClickListener {
            val request = DownloadManager.Request(Uri.parse(scan.fileUrl))
                .setTitle("Ultrascan File")
                .setDescription("Downloading ultrasound scan")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ultrascan_${position + 1}.pdf")

            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)

            Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
        }

        // View button
        holder.binding.btnView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse(scan.fileUrl), if (scan.fileUrl.endsWith(".pdf")) "application/pdf" else "image/*")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = scans.size
}
