package com.example.groupflow.ui.patients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groupflow.databinding.ItemPatientBinding
import com.example.groupflow.models.UserModel
import android.util.Log

class PatientAdapter(
    private val patients: List<UserModel>,
    private val onPatientClick: (UserModel) -> Unit
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    inner class PatientViewHolder(val binding: ItemPatientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding =
            ItemPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.binding.textPatientName.text = patient.name
        holder.binding.textPatientEmail.text = patient.email

        // Log which patient is being bound
        Log.d("PatientAdapter", "Binding patient: ${patient.name}, ${patient.email}")

        holder.binding.root.setOnClickListener {
            Log.d("PatientAdapter", "Patient clicked: ${patient.name}")
            onPatientClick(patient)
        }
    }

    override fun getItemCount() = patients.size
}
