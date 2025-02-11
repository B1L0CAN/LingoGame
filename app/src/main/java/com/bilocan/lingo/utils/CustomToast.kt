package com.bilocan.lingo.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bilocan.lingo.R

object CustomToast {
    private var currentToast: Toast? = null

    fun show(context: Context, message: String, isSuccess: Boolean? = null) {
        try {
            // Önceki toast mesajını iptal et
            currentToast?.cancel()

            val inflater = LayoutInflater.from(context)
            val layout = inflater.inflate(R.layout.custom_toast, null)
            
            val cardView = layout.findViewById<CardView>(R.id.toastCardView)
            val textView = layout.findViewById<TextView>(R.id.toastText)
            
            // Duruma göre arka plan rengi
            val backgroundColor = when (isSuccess) {
                true -> Color.parseColor("#4CAF50")  // Yeşil (Kazandınız)
                false -> Color.parseColor("#F44336") // Kırmızı (Kaybettiniz)
                null -> Color.parseColor("#673AB7")  // Mor (Diğer mesajlar)
            }
            
            cardView.setCardBackgroundColor(backgroundColor)
            textView.setTextColor(Color.WHITE)
            textView.text = message

            // Card view özellikleri
            cardView.radius = 25f
            cardView.elevation = 10f

            // Toast oluştur ve konumlandır
            Toast(context).apply {
                setGravity(if (isSuccess != null) Gravity.CENTER else Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, if (isSuccess != null) 0 else 150)
                duration = Toast.LENGTH_SHORT
                view = layout
                currentToast = this
                show()
            }
        } catch (e: Exception) {
            currentToast?.cancel()
            Toast.makeText(context, message, Toast.LENGTH_SHORT).also {
                currentToast = it
                it.show()
            }
        }
    }

    fun cancelCurrentToast() {
        currentToast?.cancel()
        currentToast = null
    }
} 