package com.sissi.customviewdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun onCircleViewClicked(v: View){
        startActivity(Intent(this, CircleViewActivity::class.java))
    }

    fun onXfermodeViewClicked(v: View){
        startActivity(Intent(this, XfermodeViewActivity::class.java))
    }

    fun onAnimateViewClicked(v: View){
        startActivity(Intent(this, AnimateViewActivity::class.java))
    }
    fun onFlexBoxLayoutClicked(v: View){
        startActivity(Intent(this, FlexboxLayoutActivity::class.java))
    }
}