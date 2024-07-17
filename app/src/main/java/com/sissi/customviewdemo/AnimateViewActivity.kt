package com.sissi.customviewdemo

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AnimateViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_animate_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val animateview = findViewById<AnimateView>(R.id.animateview)
        val spinner = findViewById<Spinner>(R.id.animateviewSpinner)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.animate,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }

        spinner.setSelection(AnimateView.Type.PropertyAnimation.ordinal)
        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val mode = parent!!.getItemAtPosition(position) as String
                animateview.animateType = AnimateView.Type.valueOf(mode)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        })

    }
}