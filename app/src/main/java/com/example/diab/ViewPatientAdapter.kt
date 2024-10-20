package com.example.diab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewPatientAdapter(private val patientList: List<Patient>) : RecyclerView.Adapter<ViewPatientAdapter.PatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        // Inflate the item_patient layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patientList[position]
        holder.bind(patient)
    }

    override fun getItemCount(): Int = patientList.size

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textViewPatientName)
        private val textViewEmail: TextView = itemView.findViewById(R.id.textViewPatientEmail)

        fun bind(patient: Patient) {
            textViewEmail.text = patient.email
        }
    }
}
