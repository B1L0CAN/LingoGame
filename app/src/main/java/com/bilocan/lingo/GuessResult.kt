package com.bilocan.lingo

data class GuessResult(
    val word: String,
    val results: List<LetterResult>
) 