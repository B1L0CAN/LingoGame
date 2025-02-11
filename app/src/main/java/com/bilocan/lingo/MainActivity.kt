package com.bilocan.lingo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val button4Letters = findViewById<MaterialCardView>(R.id.button4Letters)
        val button5Letters = findViewById<MaterialCardView>(R.id.button5Letters)
        val button6Letters = findViewById<MaterialCardView>(R.id.button6Letters)

        button4Letters.setOnClickListener {
            startActivity(Intent(this, Lingo4Activity::class.java))
        }

        button5Letters.setOnClickListener {
            startActivity(Intent(this, Lingo5Activity::class.java))
        }

        button6Letters.setOnClickListener {
            startActivity(Intent(this, Lingo6Activity::class.java))
        }
    }
} 