package com.bilo.lingo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Lingo4ViewModel : ViewModel() {
    private val _guesses = MutableLiveData<List<String>>(emptyList())
    val guesses: LiveData<List<String>> = _guesses

    private val _currentWord = MutableLiveData<String>()
    val currentWord: LiveData<String> = _currentWord

    private val _gameEnded = MutableLiveData<Boolean>(false)
    val gameEnded: LiveData<Boolean> = _gameEnded

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val allWords = listOf(
        "KEDI", "KÖPEK", "KUZU", "KAPI", "MASA", "KALE", "KAPI", "KASE",
        "KAFA", "KARA", "KART", "KASA", "KAYA", "KAZA", "KENT", "KIRA",
        "KIŞI", "KITA", "KOCA", "KOKU", "KOLA", "KOLI", "KOMA", "KONU",
        "KOVA", "KRAL", "KUTU", "KUYU", "KÜPE", "KÜRE"
    )
    
    private val unusedWords = allWords.toMutableList()

    init {
        startNewGame()
    }

    fun makeGuess(guess: String) {
        if (currentWord.value.isNullOrEmpty() || gameEnded.value == true) {
            return
        }

        val currentGuesses = _guesses.value?.toMutableList() ?: mutableListOf()
        currentGuesses.add(guess)
        _guesses.value = currentGuesses

        if (guess == currentWord.value) {
            _gameEnded.value = true
            _errorMessage.value = "Tebrikler! Doğru kelimeyi buldunuz!"
        } else {
            if (currentGuesses.size >= 6) {
                _gameEnded.value = true
                _errorMessage.value = "Üzgünüm, doğru kelimeyi bulamadınız."
            }
        }
    }

    fun startNewGame() {
        if (unusedWords.isEmpty()) {
            unusedWords.addAll(allWords)
        }
        
        val randomIndex = (0 until unusedWords.size).random()
        _currentWord.value = unusedWords.removeAt(randomIndex)
        _guesses.value = emptyList()
        _gameEnded.value = false
    }
} 