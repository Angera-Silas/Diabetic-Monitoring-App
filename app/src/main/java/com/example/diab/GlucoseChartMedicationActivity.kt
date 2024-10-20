package com.example.diab

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class GlucoseChartMedicationActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var graph: GraphView
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var dataTextView: TextView // Declare TextView variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucose_chart_medication)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Check if the user is logged in
        if (currentUser != null) {
            userId = currentUser.uid
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish() // Close activity if no user is logged in
        }

        graph = findViewById(R.id.graph)
        dataTextView = findViewById(R.id.dataTextView) // Initialize TextView
        firestore = Firebase.firestore

        // Fetch data based on medication input
        fetchBloodSugarDataByMedication()
    }

    private fun fetchBloodSugarDataByMedication() {
        firestore.collection("glucose_entries")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { documents ->
                val dataPoints = mutableListOf<DataPoint>()
                val fetchedData = StringBuilder()
                var totalBloodSugar = 0.0
                var count = 0

                for (document in documents) {
                    val bloodSugar = document.getDouble("bloodSugar")
                    val exercise = document.getString("exercise") ?: ""
                    val meal = document.getString("meal") ?: ""
                    val medication = document.getString("medication") ?: ""
                    val timeOfDay = document.getString("timeOfDay") ?: ""
                    val timestamp = document.getTimestamp("timestamp")

                    // Check if bloodSugar and timestamp are not null and medication is not empty
                    if (bloodSugar != null && timestamp != null && medication.isNotEmpty()) {
                        val date = timestamp.toDate()
                        dataPoints.add(DataPoint(date.time.toDouble(), bloodSugar))

                        // Append data to StringBuilder
                        fetchedData.append("Date: ${date}, Blood Sugar: $bloodSugar mg/dL\n")
                        fetchedData.append("Meal: $meal\n")
                        fetchedData.append("Exercise: $exercise\n")
                        fetchedData.append("Medication: $medication\n")
                        fetchedData.append("Time of Day: $timeOfDay\n\n")

                        // Calculate total blood sugar for average
                        totalBloodSugar += bloodSugar
                        count++
                    } else {
                        Log.d("GlucoseChartMedication", "Skipping document due to empty medication or null values: ${document.id}")
                    }
                }

                // Log the fetched data for debugging
                Log.d("GlucoseChartMedication", "Fetched data: $fetchedData")

                // Update the TextView with fetched data
                dataTextView.text = fetchedData.toString()

                // Show a toast message summarizing the fetched data
                if (count > 0) {
                    val averageBloodSugar = totalBloodSugar / count
                    Toast.makeText(
                        this,
                        "Fetched $count entries. Average Blood Sugar: ${averageBloodSugar} mg/dL",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(this, "No data available for the selected criteria.", Toast.LENGTH_SHORT).show()
                }

                updateGraph(dataPoints) // Update the graph with data points
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateGraph(dataPoints: List<DataPoint>) {
        val series = LineGraphSeries(dataPoints.toTypedArray())

        graph.removeAllSeries()
        graph.addSeries(series)

        // Customize graph
        graph.title = "Blood Sugar Level (with Medication)"
        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(200.0)
        graph.viewport.isScrollable = true
        graph.viewport.isScalable = true

        // Log the series data points
        for (dataPoint in dataPoints) {
            Log.d("GlucoseChartMedication", "Adding Data Point: x=${dataPoint.x}, y=${dataPoint.y}")
        }
    }
}
