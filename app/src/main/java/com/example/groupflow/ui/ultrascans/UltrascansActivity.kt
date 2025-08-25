package com.example.groupflow.ui.ultrascans

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groupflow.databinding.ActivityUltrascansBinding
import com.example.groupflow.models.UltrascanModel
import com.google.firebase.database.*

class UltrascansActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUltrascansBinding
    private val scans = mutableListOf<UltrascanModel>()
    private lateinit var adapter: UltrascanAdapter
    private var currentFileUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUltrascansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔙 Toolbar back
        binding.topAppBarUltrascans.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // 📋 RecyclerView setup
        binding.recyclerUltrascans.layoutManager = LinearLayoutManager(this)
        adapter = UltrascanAdapter(this, scans)
        binding.recyclerUltrascans.adapter = adapter

        // 🔥 Fetch scans from Firebase
        fetchScansFromFirebase()

        // 👁️ View button
        binding.btnView.setOnClickListener {
            currentFileUrl?.let {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(it), if (it.endsWith(".pdf")) "application/pdf" else "image/*")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show()
                }
            } ?: Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }

        // ⬇️ Download button
        binding.btnDownload.setOnClickListener {
            currentFileUrl?.let {
                val request = DownloadManager.Request(Uri.parse(it))
                    .setTitle("Ultrascan File")
                    .setDescription("Downloading ultrasound scan")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "ultrascan_file")

                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)

                Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Realtime updates from Firebase
    private fun fetchScansFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("ultrascans")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scans.clear()
                for (scanSnap in snapshot.children) {
                    val scan = scanSnap.getValue(UltrascanModel::class.java)
                    scan?.let { scans.add(it) }
                }

                // Keep track of last uploaded file (latest)
                currentFileUrl = scans.lastOrNull()?.fileUrl

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UltrascansActivity, "Failed to load scans", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
