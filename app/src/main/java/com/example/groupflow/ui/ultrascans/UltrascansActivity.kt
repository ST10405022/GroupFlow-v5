package com.example.groupflow.ui.ultrascans

import android.os.Bundle
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

    // Holds the currently selected scan's file URL
    private var currentFileUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUltrascansBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button in toolbar
        binding.topAppBarUltrascans.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // RecyclerView setup with new adapter signature
        adapter = UltrascanAdapter(this, scans) { selectedUrl ->
            currentFileUrl = selectedUrl
            Toast.makeText(this, "Selected scan updated", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerUltrascans.layoutManager = LinearLayoutManager(this)
        binding.recyclerUltrascans.adapter = adapter

        // Fetch scans from Firebase
        fetchScansFromFirebase()
    }

    /**
     * Reads the ultrascans node from Firebase Realtime Database.
     * Whenever data changes, it updates the local list and refreshes the RecyclerView.
     */
    private fun fetchScansFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("ultrascans")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                scans.clear()
                for (scanSnap in snapshot.children) {
                    val scan = scanSnap.getValue(UltrascanModel::class.java)
                    scan?.let { scans.add(it) }
                }

                // Update the last selected file (latest entry)
                currentFileUrl = scans.lastOrNull()?.fileUrl

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@UltrascansActivity,
                    "Failed to load scans: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
