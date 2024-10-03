package com.example.diab

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.DefaultLabelFormatter
import android.util.Log
import android.widget.Button
import java.util.Calendar

class PastGlucoseChartActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var graph: GraphView
    private lateinit var goto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_glucose_chart)

        graph = findViewById(R.id.graph)
        goto = findViewById(R.id.gotoentry)
        firestore = Firebase.firestore

        goto.setOnClickListener {
            startActivity(Intent(this, GlucoseEntryActivity::class.java))
        }

        // Fetch data from Firestore for past days
        fetchPastBloodSugarData()
    }

    private fun fetchPastBloodSugarData() {
        firestore.collection("glucose_entries")
            .orderBy("timestamp") // Ensure entries are ordered by timestamp
            .get()
            .addOnSuccessListener { documents ->
                val dataPoints = mutableListOf<DataPoint>()
                val calendar = Calendar.getInstance()

                // Prepare a map to hold daily averages
                val dailySums = mutableMapOf<String, MutableList<Double>>()

                for (document in documents) {
                    val bloodSugar = document.getLong("bloodSugar")?.toDouble()
                    val timestamp = document.getTimestamp("timestamp")

                    // Log the fetched data for debugging
                    Log.d("PastGlucoseChartActivity", "Document: $document")

                    if (bloodSugar != null && timestamp != null) {
                        val date = timestamp.toDate()
                        calendar.time = date

                        // Format the date to "YYYY-MM-DD" to use as a key
                        val key = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"

                        // Add blood sugar values to the corresponding date key
                        dailySums.getOrPut(key) { mutableListOf() }.add(bloodSugar)
                    }
                }

                // Calculate the daily average and create data points
                dailySums.forEach { (date, values) ->
                    val average = values.average()
                    // Use the day number for the X-axis (for simplicity)
                    val dayOfMonth = Calendar.getInstance().apply {
                        time = calendar.time // Set to the last processed date
                    }.apply {
                        set(Calendar.YEAR, calendar.get(Calendar.YEAR))
                        set(Calendar.MONTH, calendar.get(Calendar.MONTH))
                        set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
                    }.get(Calendar.DAY_OF_MONTH)
                    dataPoints.add(DataPoint(dayOfMonth.toDouble(), average))
                }

                // Update the graph with fetched data
                updateGraph(dataPoints)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateGraph(dataPoints: List<DataPoint>) {
        val series = LineGraphSeries(dataPoints.toTypedArray())

        // Clear any existing series and add the new one
        graph.removeAllSeries()
        graph.addSeries(series)

        // Set the title and labels
        graph.title = "Average Blood Sugar Level Over Past Days"
        graph.gridLabelRenderer.horizontalAxisTitle = "Day of Month"
        graph.gridLabelRenderer.verticalAxisTitle = "Average Blood Sugar Level (mg/dL)"

        // Set the Y-axis bounds
        graph.viewport.setMinY(0.0) // Set minimum y-axis value to 0
        graph.viewport.setMaxY(200.0) // Set maximum y-axis value to 200

        // Enable the viewport to prevent scrolling
        graph.viewport.isScrollable = true // Allow horizontal scrolling
        graph.viewport.isScalable = true // Allow scaling

        // Set the X-axis bounds
        graph.viewport.setMinX(1.0) // Set minimum x-axis value to 1 (1st day)
        graph.viewport.setMaxX(31.0) // Set maximum x-axis value to 31 (31st day)

        // Set custom date format for X-axis labels
        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    // Format X-axis labels as day of the month
                    value.toInt().toString()
                } else {
                    // Format Y-axis labels
                    value.toString()
                }
            }
        }

        // Optional: Customize graph appearance
        series.color = Color.BLUE // Customize color to blue
        series.isDrawDataPoints = true
        series.dataPointsRadius = 10f
    }
}
