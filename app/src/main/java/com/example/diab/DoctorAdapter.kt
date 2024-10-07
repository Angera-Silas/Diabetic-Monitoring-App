package com.example.diab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DoctorAdapter(
    private val doctorList: List<Doctor>,
    private val listener: OnDoctorClickListener // Listener for doctor clicks
) : RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    // Interface to handle doctor click events
    interface OnDoctorClickListener {
        fun onDoctorClick(doctor: Doctor)
    }

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewDoctorName)
        val emailTextView: TextView = itemView.findViewById(R.id.textViewDoctorEmail)

        // Bind doctor data to the UI and set up the click listener
        fun bind(doctor: Doctor) {
            nameTextView.text = doctor.name
            emailTextView.text = doctor.email

            // Handle item click
            itemView.setOnClickListener {
                listener.onDoctorClick(doctor) // Call the listener when doctor is clicked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_doctor, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.bind(doctor)
    }

    override fun getItemCount(): Int {
        return doctorList.size
    }
}
