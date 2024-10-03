package com.example.diab

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.example.diab.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.util.Calendar

class GlucoseChartActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var graph: GraphView
    private lateinit var goto: Button
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_glucose_chart)

        graph = findViewById(R.id.graph)
        goto = findViewById(R.id.gotoentry)

        auth = FirebaseAuth.getInstance()

        // Initialize Firestore
        firestore = Firebase.firestore

        // Set up drawer and navigation view
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val startTextView: TextView = findViewById(R.id.startTextView)

        // Set up the toolbar and toggle
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.Open, R.string.Close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Enable the hamburger icon to open the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Set navigation view item click listener
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, GlucoseEntryActivity::class.java))
                }
                R.id.nav_medication -> {
                    Toast.makeText(this, "Medication", Toast.LENGTH_SHORT).show()
                    // Navigate to MedicationActivity (if exists)
                    startActivity(Intent(this, ReportsActivity::class.java))

                }
                R.id.nav_appointment -> {
                    Toast.makeText(this, "Appointment", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AppointmentActivity::class.java))
                }
                R.id.nav_chat -> {
                    Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show()
                    // Navigate to ChatActivity (if exists)
                    startActivity(Intent(this, GlucoseChartActivity::class.java))

                }
                R.id.nav_logout -> {
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Open drawer when startTextView is clicked
        startTextView.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // Button click listener to navigate to GlucoseEntryActivity
        goto.setOnClickListener {
            startActivity(Intent(this, GlucoseEntryActivity::class.java))
        }

        // Fetch data from Firestore
        fetchBloodSugarData()
    }
    private fun fetchBloodSugarData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Get the current user's UID

        if (userId != null) {
            firestore.collection("glucose_entries")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener { documents ->
                    val dataPoints = mutableListOf<DataPoint>()
                    val calendar = Calendar.getInstance()
                    val currentDate = calendar.time

                    for (document in documents) {
                        val bloodSugar = document.getLong("bloodSugar")?.toDouble()
                        val timestamp = document.getTimestamp("timestamp")

                        if (bloodSugar != null && timestamp != null) {
                            val date = timestamp.toDate()
                            calendar.time = date

                            // Check if the entry is from the current day
                            if (calendar.get(Calendar.YEAR) == currentDate.year + 1900 &&
                                calendar.get(Calendar.MONTH) == currentDate.month &&
                                calendar.get(Calendar.DAY_OF_MONTH) == currentDate.date) {

                                val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
                                dataPoints.add(DataPoint(hourOfDay.toDouble(), bloodSugar))
                            }
                        }
                    }
                    updateGraph(dataPoints)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateGraph(dataPoints: List<DataPoint>) {
        val series = LineGraphSeries(dataPoints.toTypedArray())
        graph.removeAllSeries()
        graph.addSeries(series)

        graph.title = "Blood Sugar Level Over Time"
        graph.gridLabelRenderer.horizontalAxisTitle = "Time (Hours)"
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
        }

        addHorizontalLines()
        series.color = Color.parseColor("#007BFF")
        series.isDrawDataPoints = true
        series.dataPointsRadius = 10f
    }

    private fun addHorizontalLines() {
        val dangerouslyLowThreshold = LineGraphSeries<DataPoint>(
            arrayOf(DataPoint(0.0, 70.0), DataPoint(23.0, 70.0))
        )
        dangerouslyLowThreshold.color = Color.RED
        dangerouslyLowThreshold.thickness = 8
        graph.addSeries(dangerouslyLowThreshold)

        val normalThreshold = LineGraphSeries<DataPoint>(
            arrayOf(DataPoint(0.0, 130.0), DataPoint(23.0, 130.0))
        )
        normalThreshold.color = Color.GREEN
        normalThreshold.thickness = 8
        graph.addSeries(normalThreshold)

        val riskyThreshold = LineGraphSeries<DataPoint>(
            arrayOf(DataPoint(0.0, 180.0), DataPoint(23.0, 180.0))
        )
        riskyThreshold.color = Color.YELLOW
        riskyThreshold.thickness = 8
        graph.addSeries(riskyThreshold)

        val dangerouslyHighThreshold = LineGraphSeries<DataPoint>(
            arrayOf(DataPoint(0.0, 250.0), DataPoint(23.0, 250.0))
        )
        dangerouslyHighThreshold.color = Color.RED
        dangerouslyHighThreshold.thickness = 8
        graph.addSeries(dangerouslyHighThreshold)
    }

    // Handle the back button to close the drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (toggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
