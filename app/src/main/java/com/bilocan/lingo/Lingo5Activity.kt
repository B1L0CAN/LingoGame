package com.bilocan.lingo

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bilocan.lingo.databinding.ActivityLingo5Binding
import com.bilocan.lingo.utils.CustomToast
import java.util.*

class Lingo5Activity : AppCompatActivity() {
    private lateinit var binding: ActivityLingo5Binding
    private lateinit var viewModel: LingoViewModel
    private lateinit var adapter: GuessAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLingo5Binding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, LingoViewModelFactory(application, 5))
            .get(LingoViewModel::class.java)
        
        binding.backButton.setOnClickListener {
            finish()
        }
        
        setupViews()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupViews() {
        binding.guessInput.filters = arrayOf(android.text.InputFilter.AllCaps(), android.text.InputFilter.LengthFilter(5))
        binding.guessInput.transformationMethod = android.text.method.SingleLineTransformationMethod.getInstance()
        binding.nextWordButton.visibility = View.GONE
        binding.correctWordText.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        adapter = GuessAdapter(5)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.itemAnimator = null
    }

    private fun setupObservers() {
        viewModel.guesses.observe(this) { guessResults ->
            adapter.submitList(guessResults)
            if (guessResults.isNotEmpty()) {
                binding.recyclerView.smoothScrollToPosition(guessResults.size - 1)
            }
        }

        viewModel.score.observe(this) { score ->
            binding.scoreText.text = "Puan: $score"
        }

        viewModel.hintAvailable.observe(this) { available ->
            binding.hintButton.isEnabled = available
            binding.hintButton.alpha = if (available) 1f else 0.5f
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                CustomToast.show(this, it)
            }
        }

        viewModel.gameState.observe(this) { gameState ->
            when (gameState) {
                GameState.WON -> {
                    binding.correctWordText.visibility = View.GONE
                    hideKeyboard()
                    binding.guessInput.isEnabled = false
                    binding.submitButton.isEnabled = false
                    binding.nextWordButton.visibility = View.VISIBLE
                    CustomToast.show(this, "Tebrikler! Kazandınız!", true)
                }
                GameState.LOST -> {
                    binding.correctWordText.text = "Doğru kelime: ${viewModel.getCurrentWord()}"
                    binding.correctWordText.visibility = View.VISIBLE
                    hideKeyboard()
                    binding.guessInput.isEnabled = false
                    binding.submitButton.isEnabled = false
                    binding.nextWordButton.visibility = View.VISIBLE
                    CustomToast.show(this, "Üzgünüm, kaybettiniz!", false)
                }
                GameState.ERROR -> {
                    CustomToast.show(this, "Lütfen 5 harfli bir kelime girin")
                }
                GameState.WRONG_FIRST_LETTER -> {
                    val firstLetter = viewModel.getCurrentWord().firstOrNull()
                    CustomToast.show(this, "Kelime '${firstLetter}' harfi ile başlamalıdır!")
                }
                else -> {
                    binding.guessInput.isEnabled = true
                    binding.submitButton.isEnabled = true
                    binding.nextWordButton.visibility = View.GONE
                    binding.correctWordText.visibility = View.GONE
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.submitButton.setOnClickListener {
            val guess = binding.guessInput.text.toString().trim().uppercase(Locale.forLanguageTag("tr"))
            if (guess.length == 5) {
                viewModel.makeGuess(guess)
                binding.guessInput.text?.clear()
                binding.guessInput.hint = ""
            } else {
                CustomToast.show(this, "Lütfen 5 harfli bir kelime girin")
            }
        }

        binding.nextWordButton.setOnClickListener {
            CustomToast.cancelCurrentToast()
            viewModel.resetGame()
            binding.guessInput.text?.clear()
            binding.guessInput.hint = "Lütfen 5 harfli bir kelime giriniz"
            binding.correctWordText.visibility = View.GONE
            binding.correctWordText.text = ""
        }

        binding.hintButton.setOnClickListener {
            val hintLetter = viewModel.useHint()
            if (hintLetter != null) {
                CustomToast.show(this, "İpucu: Kelimede '${hintLetter}' harfi var")
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let { view ->
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
} 