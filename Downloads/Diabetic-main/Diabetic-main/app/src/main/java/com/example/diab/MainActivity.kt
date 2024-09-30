package com.example.diab

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.diab.R

class MainActivity : AppCompatActivity() {

    private lateinit var morningSugarInput: EditText
    private lateinit var afternoonSugarInput: EditText
    private lateinit var eveningSugarInput: EditText
    private lateinit var morningMedsInput: EditText
    private lateinit var afternoonMedsInput: EditText
    private lateinit var eveningMedsInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initializing the inputs
        morningSugarInput = findViewById(R.id.morningSugarInput)
        afternoonSugarInput = findViewById(R.id.afternoonSugarInput)
        eveningSugarInput = findViewById(R.id.eveningSugarInput)
        morningMedsInput = findViewById(R.id.morningMedsInput)
        afternoonMedsInput = findViewById(R.id.afternoonMedsInput)
        eveningMedsInput = findViewById(R.id.eveningMedsInput)
        submitButton = findViewById(R.id.submitButton)

        // Set onClickListener for the button
        submitButton.setOnClickListener {
            val morningSugar = morningSugarInput.text.toString()
            val afternoonSugar = afternoonSugarInput.text.toString()
            val eveningSugar = eveningSugarInput.text.toString()

            val morningMeds = morningMedsInput.text.toString()
            val afternoonMeds = afternoonMedsInput.text.toString()
            val eveningMeds = eveningMedsInput.text.toString()

            // Displaying the input values as a Toast (you can save to a database here)
            Toast.makeText(this,
                "Morning: Sugar = $morningSugar, Meds = $morningMeds\n" +
                        "Afternoon: Sugar = $afternoonSugar, Meds = $afternoonMeds\n" +
                        "Evening: Sugar = $eveningSugar, Meds = $eveningMeds",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
