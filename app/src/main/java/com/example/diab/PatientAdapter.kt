package com.example.diab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientAdapter(
    private val patientList: List<Patient>,
    private val listener: OnPatientClickListener // Listener for click events
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewPatientName)
        val emailTextView: TextView = itemView.findViewById(R.id.textViewPatientEmail)

        init {
            itemView.setOnClickListener(this) // Set click listener for the itemView
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onPatientClick(patientList[position]) // Trigger click callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patientList[position]
        holder.nameTextView.text = patient.name ?: "No Name"
        holder.emailTextView.text = patient.email ?: "No Email"
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    interface OnPatientClickListener {
        fun onPatientClick(patient: Patient) // Interface for handling patient click events
    }
}
