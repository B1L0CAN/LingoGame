package com.bilocan.lingo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bilocan.lingo.databinding.ItemGuessBinding

class GuessAdapter(private val letterCount: Int) : ListAdapter<GuessResult, GuessAdapter.GuessViewHolder>(GuessDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuessViewHolder {
        val binding = ItemGuessBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuessViewHolder(binding, letterCount)
    }

    override fun onBindViewHolder(holder: GuessViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GuessViewHolder(
        private val binding: ItemGuessBinding,
        private val letterCount: Int
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(guessResult: GuessResult) {
            val letters = guessResult.word.toCharArray()
            val results = guessResult.results

            // Tüm harfleri ve renkleri sıfırla
            val views = listOf(
                binding.letter1 to binding.letterText1,
                binding.letter2 to binding.letterText2,
                binding.letter3 to binding.letterText3,
                binding.letter4 to binding.letterText4,
                binding.letter5 to binding.letterText5,
                binding.letter6 to binding.letterText6
            )

            // Sadece kullanılacak harf sayısı kadar işlem yap
            for (i in 0 until letterCount) {
                val (cardView, textView) = views[i]
                val letterText = if (i < letters.size) letters[i].toString() else ""
                val result = if (i < results.size) results[i] else null
                
                cardView.setCardBackgroundColor(getColorForResult(result))
                textView.text = letterText
            }

            // Kullanılmayan harfleri gizle
            for (i in letterCount until views.size) {
                val (cardView, _) = views[i]
                cardView.visibility = ViewGroup.GONE
            }
        }

        private fun getColorForResult(result: LetterResult?): Int {
            return when (result) {
                LetterResult.CORRECT -> Color.parseColor("#4CAF50") // Yeşil
                LetterResult.WRONG_POSITION -> Color.parseColor("#FF9800") // Turuncu
                LetterResult.WRONG -> Color.parseColor("#9E9E9E") // Gri
                null -> Color.parseColor("#424242") // Varsayılan gri
            }
        }
    }

    class GuessDiffCallback : DiffUtil.ItemCallback<GuessResult>() {
        override fun areItemsTheSame(oldItem: GuessResult, newItem: GuessResult): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GuessResult, newItem: GuessResult): Boolean {
            return oldItem == newItem
        }
    }
}
