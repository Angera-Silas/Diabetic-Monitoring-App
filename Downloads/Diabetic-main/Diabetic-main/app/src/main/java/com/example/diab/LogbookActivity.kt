package com.example.diab // Replace with your actual package name

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LogbookActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var logbookAdapter: LogbookAdapter
    private var logEntries: List<LogEntry> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logbook)

        // Retrieve log entries from intent
        logEntries = intent.getParcelableArrayListExtra<LogEntry>("logEntries") ?: listOf()

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        logbookAdapter = LogbookAdapter(logEntries)
        recyclerView.adapter = logbookAdapter
    }
}
