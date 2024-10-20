package com.example.diab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserDetailAdapter(private val userDetailsList: List<UserDetail>) :
    RecyclerView.Adapter<UserDetailAdapter.UserDetailViewHolder>() {

    class UserDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBloodSugar: TextView = itemView.findViewById(R.id.tvBloodSugar)
        val tvExercise: TextView = itemView.findViewById(R.id.tvExercise)
        val tvMeal: TextView = itemView.findViewById(R.id.tvMeal)
        val tvMedication: TextView = itemView.findViewById(R.id.tvMedication)
        val tvTimeOfDay: TextView = itemView.findViewById(R.id.tvTimeOfDay)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_detail, parent, false)
        return UserDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserDetailViewHolder, position: Int) {
        val userDetail = userDetailsList[position]
        holder.tvBloodSugar.text = "Blood Sugar: ${userDetail.bloodSugar}"
        holder.tvExercise.text = "Exercise: ${userDetail.exercise}"
        holder.tvMeal.text = "Meal: ${userDetail.meal}"
        holder.tvMedication.text = "Medication: ${userDetail.medication}"
        holder.tvTimeOfDay.text = "Time of Day: ${userDetail.timeOfDay}"
        holder.tvTimestamp.text = "Timestamp: ${userDetail.timestamp}"
    }

    override fun getItemCount() = userDetailsList.size
}
