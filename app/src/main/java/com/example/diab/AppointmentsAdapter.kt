package com.example.diab


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppointmentsAdapter(private val appointments: List<Appointment>) : RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false) // Change to your item layout name
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    override fun getItemCount() = appointments.size

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewSpeciality: TextView = itemView.findViewById(R.id.textViewSpeciality) // Change to your TextView ID
        private val textViewReason: TextView = itemView.findViewById(R.id.textViewReason) // Change to your TextView ID
        private val textViewHistory: TextView = itemView.findViewById(R.id.textViewHistory) // Change to your TextView ID
        private val textViewDateTime: TextView = itemView.findViewById(R.id.textViewDateTime) // Change to your TextView ID

        fun bind(appointment: Appointment) {
            textViewSpeciality.text = appointment.speciality
            textViewReason.text = appointment.reason
            textViewHistory.text = appointment.history
            textViewDateTime.text = appointment.dateTime
        }
    }
}
