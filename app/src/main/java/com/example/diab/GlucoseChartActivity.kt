package com.example.diab

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.DefaultLabelFormatter
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import java.text.SimpleDateFormat
import java.util.*

class GlucoseChartActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var graph: GraphView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    // Define thresholds
    private val dangerouslyLowThreshold = 70.0
    private val normalThreshold = 130.0
    private val riskyThreshold = 180.0
    private val dangerouslyHighThreshold = 200.0

    private lateinit var dangerouslyLowIndicator: TextView
    private lateinit var normalIndicator: TextView
    private lateinit var riskyIndicator: TextView
    private lateinit var dangerouslyHighIndicator: TextView
    private lateinit var gotoentry: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucose_chart)

        graph = findViewById(R.id.graph)
        firestore = Firebase.firestore

        // Initialize indicators
        dangerouslyLowIndicator = findViewById(R.id.indicator_dangerously_low)
        normalIndicator = findViewById(R.id.indicator_normal)
        riskyIndicator = findViewById(R.id.indicator_risky)
        dangerouslyHighIndicator = findViewById(R.id.indicator_dangerously_high)
        gotoentry = findViewById(R.id.gotoentry)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        // Set up the toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up drawer toggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle navigation item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Handle Home action
                    startActivity(Intent(this,GlucoseEntryActivity::class.java))

                    Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_medication -> {
                    // Handle Medication Report action
                    startActivity(Intent(this,PastGlucoseChartActivity::class.java))

                    Toast.makeText(this, "Medication Report selected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_appointment -> {
                    // Handle Appointment action
                    Toast.makeText(this, "Appointment selected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_chat -> {
                    // Handle Chat action
                    startActivity(Intent(this,DoctorListActivity::class.java))
                    Toast.makeText(this, "Chat selected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_logout -> {
                    // Handle Logout action
                    Toast.makeText(this, "Logout selected", Toast.LENGTH_SHORT).show()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Navigate to glucose entry activity
        gotoentry.setOnClickListener {
            startActivity(Intent(this, GlucoseEntryActivity::class.java))
        }

        // Fetch data from Firestore
        fetchBloodSugarData()
    }

    private fun fetchBloodSugarData() {
        firestore.collection("glucose_entries")
            .orderBy("timestamp") // Ensure entries are ordered by timestamp
            .get()
            .addOnSuccessListener { documents ->
                val dataPoints = mutableListOf<DataPoint>()
                var latestBloodSugar: Double? = null // To keep track of the latest blood sugar value

                for (document in documents) {
                    val bloodSugar = document.getDouble("bloodSugar")
                    val timestamp = document.getTimestamp("timestamp")

                    Log.d("GlucoseChartActivity", "Document: $document")

                    if (bloodSugar != null && timestamp != null) {
                        val date = timestamp.toDate()
                        dataPoints.add(DataPoint(date.time.toDouble(), bloodSugar))

                        // Update latest blood sugar value
                        latestBloodSugar = bloodSugar
                        Toast.makeText(this, "Fetched: Blood Sugar: $bloodSugar mg/dL at $date", Toast.LENGTH_SHORT).show()
                    }
                }

                // Update the graph with fetched data
                updateGraph(dataPoints)

                // Update indicators based on the latest blood sugar value
                if (latestBloodSugar != null) {
                    updateIndicators(latestBloodSugar)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateIndicators(bloodSugar: Double) {
        // Reset indicators color
        dangerouslyLowIndicator.setBackgroundColor(Color.TRANSPARENT)
        normalIndicator.setBackgroundColor(Color.TRANSPARENT)
        riskyIndicator.setBackgroundColor(Color.TRANSPARENT)
        dangerouslyHighIndicator.setBackgroundColor(Color.TRANSPARENT)

        // Determine which indicator to highlight
        when {
            bloodSugar < dangerouslyLowThreshold -> {
                dangerouslyLowIndicator.setBackgroundColor(Color.RED)
                dangerouslyLowIndicator.text = "Dangerously Low"
            }
            bloodSugar in dangerouslyLowThreshold..normalThreshold -> {
                normalIndicator.setBackgroundColor(Color.GREEN)
                normalIndicator.text = "Normal"
            }
            bloodSugar in normalThreshold..riskyThreshold -> {
                riskyIndicator.setBackgroundColor(Color.YELLOW)
                riskyIndicator.text = "Risky"
            }
            bloodSugar > dangerouslyHighThreshold -> {
                dangerouslyHighIndicator.setBackgroundColor(Color.RED)
                dangerouslyHighIndicator.text = "Dangerously High"
            }
        }
    }

    private fun updateGraph(dataPoints: List<DataPoint>) {
        val series = LineGraphSeries(dataPoints.toTypedArray())

        graph.removeAllSeries()
        graph.addSeries(series)

        graph.title = "Blood Sugar Level Over Time"
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
