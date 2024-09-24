package com.example.diab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class LogbookAdapter(private val logEntries: List<LogEntry>) : RecyclerView.Adapter<LogbookAdapter.LogEntryViewHolder>() {

    // ViewHolder class to hold individual log entry views
    class LogEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBloodSugar: TextView = itemView.findViewById(R.id.tvBloodSugar)
        val tvMorningMedication: TextView = itemView.findViewById(R.id.tvMorningMedication)
        val tvAfternoonMedication: TextView = itemView.findViewById(R.id.tvAfternoonMedication)
        val tvEveningMedication: TextView = itemView.findViewById(R.id.tvEveningMedication)
        // If you want to add more user-specific info, do it here
    }

    // Inflates the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogEntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_log_entry, parent, false)
        return LogEntryViewHolder(view)
    }

    // Binds the data to each view in the RecyclerView
    override fun onBindViewHolder(holder: LogEntryViewHolder, position: Int) {
        val entry = logEntries[position]
        holder.tvBloodSugar.text = "Blood Sugar: ${entry.bloodSugar} mg/dL"
        holder.tvMorningMedication.text = "Morning Medication: ${entry.morningMedication} units"
        holder.tvAfternoonMedication.text = "Afternoon Medication: ${entry.afternoonMedication} units"
        holder.tvEveningMedication.text = "Evening Medication: ${entry.eveningMedication} units"

        // Optionally bind user-specific information if included in LogEntry
        // holder.tvUserId.text = "User ID: ${entry.userId}" // Uncomment if you want to show user ID
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = logEntries.size
}
