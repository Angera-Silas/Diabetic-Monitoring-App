package com.example.diab // Replace with your actual package name

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogbookAdapter(private val logEntries: List<LogEntry>) : RecyclerView.Adapter<LogbookAdapter.LogEntryViewHolder>() {

    // ViewHolder class to hold individual log entry views
    class LogEntryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBloodSugar: TextView = itemView.findViewById(R.id.tvBloodSugar)
        val tvMedication: TextView = itemView.findViewById(R.id.tvMedication) // General medication TextView
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
        holder.tvMedication.text = "Medication: ${entry.medication}" // Only one medication to display
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = logEntries.size
}
