package com.example.diab

import android.graphics.Color
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
import com.jjoe64.graphview.DefaultLabelFormatter
import java.text.SimpleDateFormat
import java.util.*

class GlucoseChartFoodActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var graph: GraphView
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var dataTextView: TextView // Declare TextView variable

    // Define thresholds
    private val dangerouslyLowThreshold = 70.0
    private val normalThreshold = 130.0
    private val riskyThreshold = 180.0
    private val dangerouslyHighThreshold = 200.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucose_chart_food)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Check if the user is logged in
        if (currentUser != null) {
            userId = intent.getStringExtra("patientId") ?: currentUser.uid // Use the patient ID if available, otherwise use current user ID
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish() // Close activity if no user is logged in
        }

        graph = findViewById(R.id.graph)
        dataTextView = findViewById(R.id.dataTextView) // Initialize TextView
        firestore = Firebase.firestore

        // Listen for real-time updates
        fetchBloodSugarDataByFoodRealTime()
    }

    private fun fetchBloodSugarDataByFoodRealTime() {
        firestore.collection("glucose_entries")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("GlucoseChartFood", "Listen failed.", e)
                    Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val dataPoints = mutableListOf<DataPoint>()
                    val fetchedData = StringBuilder()
                    var totalBloodSugar = 0.0
                    var count = 0

                    for (document in snapshots) {
                        val bloodSugar = document.getDouble("bloodSugar")
                        val exercise = document.getString("exercise") ?: ""
                        val meal = document.getString("meal") ?: ""
                        val medication = document.getString("medication") ?: ""
                        val timeOfDay = document.getString("timeOfDay") ?: ""
                        val timestamp = document.getTimestamp("timestamp")

                        // Check if bloodSugar and timestamp are not null and meal is not empty
                        if (bloodSugar != null && timestamp != null && meal.isNotEmpty()) {
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
                        } else if (meal.isEmpty()) {
                            Log.d("GlucoseChartFood", "Skipping document due to empty meal: ${document.id}")
                        } else {
                            Log.d("GlucoseChartFood", "Null values found in document: ${document.id}")
                        }
                    }

                    // Log the fetched data for debugging
                    Log.d("GlucoseChartFood", "Fetched data: $fetchedData")

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

                    updateGraph(dataPoints) // Update the graph with the new data
                }
            }
    }

    private fun updateGraph(dataPoints: List<DataPoint>) {
        val series = LineGraphSeries(dataPoints.toTypedArray())

        graph.removeAllSeries()
        graph.addSeries(series)

        graph.title = "Blood Sugar Level (with Food Intake)"
        graph.gridLabelRenderer.horizontalAxisTitle = "Date/Time"
        graph.gridLabelRenderer.verticalAxisTitle = "Blood Sugar Level (mg/dL)"

        graph.viewport.setMinY(0.0)
        graph.viewport.setMaxY(200.0)

        graph.viewport.isScrollable = true
        graph.viewport.isScalable = true

        if (dataPoints.isNotEmpty()) {
            val xMin = dataPoints.minOf { it.x }
            val xMax = dataPoints.maxOf { it.x }
            graph.viewport.setMinX(xMin)
            graph.viewport.setMaxX(xMax)

            graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
                private val dayFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                private val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                override fun formatLabel(value: Double, isValueX: Boolean): String {
                    return if (isValueX) {
                        val date = Date(value.toLong())
                        dayFormat.format(date)
                    } else {
                        value.toString()
                    }
                }
            }
        }

        // Add horizontal lines for thresholds
        addHorizontalLines()

        series.color = resources.getColor(R.color.pastel_blue)
        series.isDrawDataPoints = true
        series.dataPointsRadius = 10f
    }

    private fun addHorizontalLines() {
        // Dangerously Low (70 mg/dL)
        val dangerouslyLowThreshold = LineGraphSeries<DataPoint>(
            arrayOf(DataPoint(0.0, 70.0), DataPoint(1.0, 70.0))
        )
        dangerouslyLowThreshold.color = Color.RED
        dangerouslyLowThreshold.thickness = 8
        graph.addSeries(dangerouslyLowThreshold)

        // Normal (130 mg/dL)
        val normalThreshold = LineGraphSeries<DataPoint>(
            arrayOf(DataPoint(0.0, 130.0), DataPoint(1.0, 130.0))
        )
        normalThreshold.color = Color.GREEN
        normalThreshold.thickness = 8
        graph.addSeries(normalThreshold)

        // Risky (180 mg/dL)
        val riskyThreshold = LineGraphSeries<DataPoint>(
            arrayOf(DataPoint(0.0, 180.0), DataPoint(1.0, 180.0))
        )
        riskyThreshold.color = Color.YELLOW
        riskyThreshold.thickness = 8
        graph.addSeries(riskyThreshold)

        // Dangerously High (200 mg/dL)
        val dangerouslyHighThreshold = LineGraphSeries<DataPoint>(
            arrayOf(DataPoint(0.0, 200.0), DataPoint(1.0, 200.0))
        )
        dangerouslyHighThreshold.color = Color.RED
        dangerouslyHighThreshold.thickness = 8
        graph.addSeries(dangerouslyHighThreshold)
    }
}
