package com.example.firestoredemo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.firestoredemo.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Use your new Firestore collection "cat_discipline_logs"
        db = FirebaseFirestore.getInstance()

        db.collection("cat_discipline_logs")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    showError("Error: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    val combinedData = mutableMapOf<String, Any>()
                    for (document in snapshots) {
                        // Merge all fields from each document
                        combinedData.putAll(document.data)
                    }
                    renderCells(combinedData)
                } else {
                    showError("No data found in cat_discipline_logs")
                }
            }
    }

    private fun renderCells(data: Map<String, Any>) {
        binding.listContainer.removeAllViews()

        for ((key, value) in data) {
            // Field label
            val textView = TextView(this).apply {
                text = "$key: $value"
                textSize = 16f
                setTextColor(Color.BLACK)
                setPadding(0, 12, 0, 12)
            }

            // Divider
            val divider = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    setMargins(0, 0, 0, 0)
                }
                setBackgroundColor(Color.LTGRAY)
            }

            binding.listContainer.addView(textView)
            binding.listContainer.addView(divider)
        }
    }

    private fun showError(message: String) {
        binding.listContainer.removeAllViews()
        val errorView = TextView(this).apply {
            text = message
            textSize = 16f
            setTextColor(Color.RED)
        }
        binding.listContainer.addView(errorView)
    }
}
