package com.bilocan.lingo

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LingoViewModel(
    application: Application,
    private val letterCount: Int
) : AndroidViewModel(application) {
    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    private val _currentWord = MutableLiveData<String>()
    private val _guesses = MutableLiveData<List<GuessResult>>()
    val guesses: LiveData<List<GuessResult>> = _guesses

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int> = _score

    private val _hintAvailable = MutableLiveData<Boolean>(true)
    val hintAvailable: LiveData<Boolean> = _hintAvailable

    private val _extraAttemptAvailable = MutableLiveData<Boolean>()
    val extraAttemptAvailable: LiveData<Boolean> = _extraAttemptAvailable

    private var extraAttemptUsed = false
    private var currentAttempt = 0
    private val maxAttempts = 4 // 4 tahmin hakkı

    private val sharedPreferences = application.getSharedPreferences("LingoScores", Context.MODE_PRIVATE)

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // Kelime listelerini daha organize bir şekilde tut
    private val wordLists = mapOf(
        4 to listOf(
            "EVLİ", "ESKİ", "YENİ", "RAZI", "DURU", "ŞİİR", "ÖYKÜ", "ŞAİR",
            "KAPI", "MASA", "KUTU", "KEDİ", "KALE","KASE", "KAFA", "KÖŞE", "KOLA", "KULE","KURU", "KAYA", "KAZA", "KİRA", "KOZA",
            "KÜME", "KURA", "KOKU", "KUYU", "KORO","ACİL", "AÇIK", "AÇMA", "ADAM", "ADET","AĞAÇ", "AĞIR", "AİLE", "AKIL",
            "AKIM", "AKIN", "ALAN", "ALEM", "ANNE",  "ARDA", "ARMA", "ASKI", "ASMA", "ATEŞ", "ATKI", "AVCI", "AVLU", "AYAK",
            "AYAR", "AYAZ",  "AYET", "AYIP", "AYNA", "FARE", "AZAR", "AZİM", "BABA", "BACA",  "BALE", "BANT", "BANK", "BATI", "BAYİ",  "BELA",
            "BESİ", "BLOK", "BORU", "BUĞU", "BURÇ", "BURS", "CAMİ", "CARİ", "CAİZ", "CENK", "ÇABA", "ÇAKI",
            "ÇALI",  "ÇENE", "ÇAPU", "ÇETE", "ÇENE", "ÇIRA","CÜCE",  "DANA", "DART", "DAYI", "DELİ", "DERİ", "DERT", "DESİ", "DEVA",
            "ŞİŞE", "CİPS", "DİRİ", "DİŞİ", "DİZE", "DİZİ", "DOĞA", "ECEL", "EDEP", "ELÇİ", "EKŞİ", "EMİR", "ENSE", "ERİL", "ESKİ", "ESİR", "FANİ", "PANO",
            "HİLE", "TARZ",  "ALEM", "MALİ", "MAZİ", "AYIP", "AZIK", "UFUK", "ANNE", "BABA", "OĞUL", "DEDE", "IŞIK", "RIZA", "REST",
            "MAAŞ", "ARAF", "OKUL", "OYUN", "ALGI", "SAKİ", "DARA", "BADE", "DÖRT", "ALTI", "YEDİ", "ÇENE", "ÇALI", "ÇATI", "BAZI", "ASLA", "KARE", "KURA",
            "KART", "HANE", "TANE", "PANO", "SOLO", "KORO", "BALO", "ÇİLE", "BELA", "UZUN", "KISA", "USLU", "AKİL", "KATI", "ILIK", "SERT", "ADİL", "BAKİ","ASIK",
            "AÇIK", "KITA", "DİZE", "ARZU", "AYAK",  "BELA", "BAHT", "CADI",
            "CÜCE", "ÇIRA", "ÇİFT", "ÇİLE", "DADI", "DELİ", "DANS", "EVLİ", "ESİR", "EBAT", "FUAR", "FÜZE", "GAGA", "GRİP", "GAZİ", "HIRS", "HARF", "HARP", "İADE", "İFŞA",
            "İĞNE", "IŞIN", "JÜRİ", "KİVİ", "KUĞU", "KOVA", "KRAL", "LOCA", "LİRA", "MODA", "MİDE", "MAYA", "MORG", "NENE", "NOTA", "OBEZ", "OMUZ", "OTEL", "ÖDEV",
            "ÖDÜL", "ÖZGÜ", "PİST", "PLAJ", "PRİM", "REİS", "SORU", "SIVI", "ŞANS", "TOKA", "UCUZ", "UZAK", "ÜMİT", "VAKA", "YARA", "YUVA", "ZULA", "İMAN", "AKIM"

        ),
        5 to listOf(
            "VASIF", "FAKİR", "ASABİ", "FERAH", "GÜZEL", "NADİR", "NAZİK", "KİBAR",
            "SABİT", "YAKIN", "DERİN", "TEMİZ", "GİZLİ", "KOLAY", "BASİT",   "CİMRİ",
            "BİBER", "HELVA", "GAZOZ", "HURMA", "SALÇA", "CEVİZ", "BADEM", "KEKİK", "ARMUT", "MARUL", "SOĞAN", "KİRAZ", "ÇİLEK", "VİŞNE", "KAVUN", "BAMYA",
            "SUSAM", "TAHİN", "REÇEL", "AYRAN","GURUR", "KİBİR", "SİNİR", "AHLAK", "MADDE", "NAMUS", "HİTAP",
            "KEBAP", "TARLA", "MOTOR", "FİKİR", "GÖRME", "SERİN", "BÖLÜM", "DENİZ", "İLHAM", "KAĞIT",
            "MEŞRU", "NEHİR", "KÜÇÜK", "BİBER", "YAZAR", "ŞARKI", "İNCİR", "NİMET", "TURNE", "KABLO",
            "KÜRSÜ", "GÜNEŞ", "BAHAR", "TAVUK", "SABAH", "ŞEHİR", "CEVAP", "HAVUZ", "YAZIT", "TARİF", "BULUT",
            "YÜZEY", "BAKIM", "DALGA", "SAKSI", "ALTIN","ÇAKMA", "DERGİ", "DEMET","FİDAN", "ÇAMUR","CÜRET","ÇÖMEZ", "DALGA","DERBİ", "ÇORAP","DEKAN","DAMLA",
            "ÇİZGİ", "ÇEVRE","ÇUBUK", "ÇEKİÇ","ÇIKAR","ÇAYIR","ARABA","AYDIN","ÇUVAL","ÇALGI", "BIÇAK", "ÇELİK", "BİLGE", "ÇELME", "BİLİM", "BEYİN",
            "BEYAN", "BETON","BAKIM", "BARIŞ", "BAVUL", "BAYAN", "BAYAT", "BEBEK","BEDEL", "BEKAR", "BEKÇİ", "BEKLE", "BAGAJ","BESİN",
            "KAZAK", "HIRKA", "CEKET", "KEMER", "FULAR", "KABAN", "PALTO","ÇARIK","DARBE",
            "YILAN", "KÖPEK", "DOMUZ", "KUMRU", "AKREP", "SERÇE", "TAVUK", "HOROZ", "HİNDİ", "ŞAHİN", "KOYUN", "KATIR", "MANDA", "TİLKİ", "GEYİK", "KİRPİ",
            "SADIK", "ZAYIF", "SAKİN", "YALIN", "ALÇAK", "REZİL", "EBEDİ", "EZELİ",  "KELAM", "KANIT", "DELİL", "BATIL",
            "YALAN", "DOĞRU", "YÜZEY", "ÇIKIŞ", "GİRİŞ", "KABİR", "MEZAR", "BAHÇE", "HOTEL", "ANTRE", "SALON", "KİLER", "BANYO", "BETON", "DORUK",
            "DAHİL", "DAVUL", "KABUL", "DİĞER", "ÖTEKİ", "DAİMA", "ŞİMDİ", "YARIN", "BUGÜN", "SONRA", "KAYIT", "SATIR",  "ROMAN",
            "CÜMLE", "ÇÖPÇÜ", "ÇÜRÜK", "ÇÖZÜM", "DİLİM", "DAİMİ", "DİLEK", "DIŞKI", "EZBER", "EVLAT", "ENFES", "FOSİL", "CACIK", "ÇANTA",
            "AÇLIK", "BALON", "BAHÇE", "CACIK", "CÜMLE", "ÇÜRÜK", "ÇİNLİ", "ÇÖZÜM", "DİLİM",
            "DİLEK", "DIŞKI", "EZBER", "EVLAT", "ENFES", "FOSİL", "FELEK", "GİYİM", "GAZOZ", "HAMAK", "HOŞAF", "HAMSİ", "İNMEK", "İNKAR", "İBRAZ",
            "ILGAZ", "JOKER",  "KREDİ", "KALIN", "KABLO", "LOTUS", "LEĞEN", "MEVLA", "MASAL", "MELEZ", "NİŞAN", "NİNNİ", "OĞLAK",
            "ÖRDEK", "PİLOT", "POSTA", "RAMPA", "ROMAN", "SAKIZ", "SAVCI", "ŞİFRE", "TEKNE", "UZMAN", "ÜZGÜN", "VAKIF", "YALIN", "ZEHİR",
            "ADANA", "ACABA", "ACELE", "BAKIM", "BALIK", "BACAK", "BAHAR", "CIVIK", "CÜSSE", "CÜBBE", "CEZVE", "ÇOCUK", "ÇUVAL", "ÇAMUR", "ÇUBUK", "DİREK",
            "DAYAK", "DARBE", "EŞARP", "ECDAT", "ERKEK", "ENDAM", "ENDER", "FULAR", "FIKRA", "GÜREŞ", "GÜMÜŞ", "GÜNAH", "GÖREV", "HAFTA", "HARAM",
            "HOROZ", "İPTAL", "İPUCU", "İSHAL", "İLHAM", "IRGAT", "ISSIZ", "JAPON", "JİLET", "KÖMÜR", "KORNA", "KORSE", "KAYIK", "LEVHA", "LAVAŞ", "LİDER",
            "METRE", "MAKAS", "MERAK", "MİRAS", "NABIZ", "NEZLE", "NARİN", "NAMAZ", "OMLET", "OĞLAN", "ÖNLÜK", "ÖDÜNÇ", "POŞET", "POLİS", "RİMEL",
            "SAVAŞ", "SİNİR", "ŞAFAK", "ŞURUP", "TAVİZ", "TEPSİ", "UYSAL", "ÜÇGEN", "VİŞNE", "YAKIN", "YANKI"

        ),
        6 to listOf(
            "KOLTUK", "PİYANO", "YASTIK", "ÇAKMAK","ÇAPKIN","ÇARŞAF","ÇIĞLIK", "ÇÖPLÜK", "DALGIÇ","CÜZDAN","DİPÇİK",
            "PİJAMA", "FERACE", "GÖMLEK", "ATMACA", "AKBABA", "BALİNA", "LEYLEK", "PİRANA", "BÜLBÜL", "KUZGUN", "MAYMUN", "KAPLAN", "YARASA",
            "TİMSAH","ÇAYLAK", "SESSİZ", "PARLAK", "MÜSRİF", "DÜRÜST",
            "SAĞLAM", "ZENGİN", "KAPALI", "YATKIN", "KORKAK",
            "HAYRAN", "ŞAŞKIN", "AYKIRI", "TEMBEL", "YORGUN", "HUYSUZ",
            "SARHOŞ", "GÜLÜNÇ", "GEVEZE", "ÜRETİM", "VİZYON", "YAPRAK", "YAZLIK", "ZAHMET", "ZİNCİR", "ZEYBEK",
            "MAHREM", "ENDİŞE", "MERHUM", "İŞARET", "ZAHMET", "KÜLFET", "HİZMET", "YARDIM", "TEKNİK", "HÜSRAN", "GURBET",
            "YASTIK", "KIYMET", "KURŞUN", "HIRSIZ", "KİTABE", "CETVEL", "MEVCUT", "MERMER", "TAHSİL", "MERKÜR", "GERÇEK",
            "YAPRAK", "VARLIK", "TABİAT", "DÜŞMAN", "SEVİNÇ", "ŞÖMİNE", "DÜRBÜN", "TALEBE", "HATIRA", "DENEME", "MAKALE", "HİKAYE",
            "CENNET", "SAĞLIK", "SIHHAT", "DEFANS", "FORVET", "FUTBOL", "OFSAYT", "STOPER", "KALECİ", "KORNER",
            "AMATÖR", "ANTİKA", "ADLİYE", "BAKKAL", "BALKON", "BECERİ", "CAZİBE", "COŞKUN", "ÇAKMAK", "ÇEVİRİ", "DEMLİK", "DEFTER", "DEVLET",
            "EMEKLİ", "ELVEDA", "FORMÜL", "FOSFOR", "GALERİ", "GÖMLEK", "HAYVAN", "HAYLAZ", "HIRSIZ", "İBADET", "İKİLEM",
            "İLİŞKİ", "IZGARA", "JAKUZİ", "KANEPE", "KANGAL", "KARPUZ", "KISMET", "LASTİK", "MEDENİ", "MAKBUZ", "MANGAL", "NAYLON", "NAFAKA",
            "OTOYOL","ÖPÜCÜK", "ÖZENTİ", "PARSEL", "PANCAR", "PAROLA", "RAFİNE", "SEÇMEN", "SİNYAL", "ŞEFKAT", "TABAKA", "TEŞHİR",
            "TIRNAK", "TURİZM", "TERLİK", "ULUSAL", "ÜZÜNTÜ", "VESİLE", "YILDIZ", "YORGAN", "ZABITA", "ZANAAT",
            "ABDEST", "AHİRET", "BALAYI", "BAKİYE", "CÜZDAN", "CÖMERT", "ÇEYREK", "ÇEMBER", "ÇIPLAK", "DAKİKA", "DALGIÇ",
            "DİKKAT", "ECZANE", "EMANET", "ERİŞTE", "FİLTRE", "FİNCAN", "FİNANS", "GOFRET", "GÖZLÜK", "GÜNCEL", "HAKSIZ", "HAMİLE",
            "HANGAR", "İÇECEK", "İNŞAAT", "İYİLİK", "ILIMAN", "JEOLOG", "KAFEİN", "KAKTÜS", "KAMYON", "KİRPİK", "KRAKER", "LAKTOZ",
            "MECLİS", "MERKEZ", "MİGREN", "NEFRET", "NUMUNE", "OKLAVA", "OTOGAR", "ÖZVERİ", "ÖNEMLİ", "PİYASA", "PEYNİR", "PROTEZ", "PARFÜM",
            "RÜZGAR", "SANİYE", "SAĞDIÇ", "SAĞLAM", "TABLET", "TROPİK", "TABİAT", "TERMAL", "ULAŞIM", "ZORLUK",


            )
    )

    private val allWords = wordLists[letterCount] ?: emptyList()
    private var unusedWords = allWords.toMutableList()

    init {
        // Kayıtlı puanı yükle
        _score.value = getStoredScore()
        startNewGame()
    }

    private fun getStoredScore(): Int {
        return sharedPreferences.getInt("score_$letterCount", 0)
    }

    private fun saveScore(newScore: Int) {
        sharedPreferences.edit().putInt("score_$letterCount", newScore).apply()
        _score.value = newScore
    }

    private fun startNewGame() {
        viewModelScope.launch {
            try {
                resetGameState()
                selectNewWord()
                initializeGuesses()
            } catch (e: Exception) {
                e.printStackTrace()
           }
        }
    }

    private fun resetGameState() {
        currentAttempt = 0
        extraAttemptUsed = false
        _gameState.value = GameState.PLAYING
        _hintAvailable.value = true
        _extraAttemptAvailable.value = false
        if (unusedWords.isEmpty()) {
            unusedWords = allWords.toMutableList()
        }
    }

    private fun selectNewWord() {
        val randomWord = unusedWords.random()
        unusedWords.remove(randomWord)
        _currentWord.value = randomWord
    }

    private fun initializeGuesses() {
        val currentGuesses = mutableListOf<GuessResult>()
        
        // İlk harfi göster
        val firstLetter = _currentWord.value?.firstOrNull()?.toString() ?: ""
        val initialWord = StringBuilder().apply {
            append(firstLetter)
            repeat(letterCount - 1) { append(" ") }
        }.toString()
        
        val initialResults = Array(letterCount) { LetterResult.WRONG }.apply {
            this[0] = LetterResult.CORRECT
        }
        
        currentGuesses.add(GuessResult(initialWord, initialResults.toList()))

        // 4 tahmin satırı ekle
        val emptyWord = " ".repeat(letterCount)
        val emptyResults = Array(letterCount) { LetterResult.WRONG }
        repeat(maxAttempts) {
            currentGuesses.add(GuessResult(emptyWord, emptyResults.toList()))
        }

        _guesses.value = currentGuesses
    }

    fun makeGuess(guess: String) {
        if (!isValidGuess(guess)) return

        val currentWord = _currentWord.value ?: return
        val results = checkGuess(guess, currentWord)
        updateGameState(guess, results)
    }

    private fun isValidGuess(guess: String): Boolean {
        val currentWord = _currentWord.value ?: return false
        return when {
            currentAttempt >= maxAttempts || _gameState.value == GameState.WON -> false
            guess.length != letterCount -> {
                _gameState.value = GameState.ERROR
                false
            }
            guess.firstOrNull() != currentWord.firstOrNull() -> {
                _gameState.value = GameState.WRONG_FIRST_LETTER
                false
            }
            else -> true
        }
    }

    private fun checkGuess(guess: String, currentWord: String): Array<LetterResult> {
        val results = Array(letterCount) { LetterResult.WRONG }
        val remainingLetters = currentWord.toMutableList()

        // Doğru pozisyondaki harfleri kontrol et
        for (i in currentWord.indices) {
            if (guess[i] == currentWord[i]) {
                results[i] = LetterResult.CORRECT
                remainingLetters.remove(currentWord[i])
            }
        }

        // Yanlış pozisyondaki harfleri kontrol et
        for (i in guess.indices) {
            if (results[i] != LetterResult.CORRECT && remainingLetters.contains(guess[i])) {
                results[i] = LetterResult.WRONG_POSITION
                remainingLetters.remove(guess[i])
            }
        }

        return results
    }

    private fun updateGameState(guess: String, results: Array<LetterResult>) {
        val guessResult = GuessResult(guess, results.toList())
        val currentGuesses = _guesses.value?.toMutableList() ?: mutableListOf()
        
        // Eğer ekstra hak kullanıldıysa, son satıra yaz
        val targetIndex = if (extraAttemptUsed) currentGuesses.size - 1 else currentAttempt + 1
        currentGuesses[targetIndex] = guessResult
        _guesses.value = currentGuesses

        currentAttempt++

        when {
            results.all { it == LetterResult.CORRECT } -> {
                _gameState.value = GameState.WON
                _extraAttemptAvailable.value = false
                // Puan hesaplama: Temel puan + erken bulma bonusu
                val basePoints = 50
                val earlyGuessBonus = (maxAttempts - currentAttempt + 1) * 10
                val currentScore = _score.value ?: 0
                val newScore = currentScore + basePoints + earlyGuessBonus
                saveScore(newScore)
            }
            currentAttempt >= maxAttempts -> {
                if (!extraAttemptUsed) {
                    // Kelime bilinemediğinde puan düşürme
                    val currentScore = _score.value ?: 0
                    val penalty = when (letterCount) {
                        4 -> 20
                        5 -> 25
                        6 -> 30
                        else -> 20
                    }
                    val newScore = maxOf(0, currentScore - penalty) // Puanın eksi olmamasını sağla
                    saveScore(newScore)
                    
                    // Yeterli puan varsa ek hak teklif et
                    if (newScore >= 50) {
                        _extraAttemptAvailable.value = true
                    } else {
                        _gameState.value = GameState.LOST
                        _extraAttemptAvailable.value = false
                    }
                } else {
                    _gameState.value = GameState.LOST
                    _extraAttemptAvailable.value = false
                }
            }
            else -> {
                _gameState.value = GameState.PLAYING
            }
        }
    }

    fun getCurrentWord(): String = _currentWord.value ?: ""

    fun resetGame() {
        startNewGame()
    }

    fun useHint(): Char? {
        // Oyun bitmişse veya ipucu kullanılamıyorsa null dön
        if (_hintAvailable.value != true || 
            _currentWord.value.isNullOrEmpty() || 
            _gameState.value == GameState.WON || 
            _gameState.value == GameState.LOST) return null
        
        // İpucu için gerekli puan kontrolü
        val currentScore = _score.value ?: 0
        val requiredPoints = when (letterCount) {
            4 -> 10
            5 -> 15
            6 -> 20
            else -> 10
        }
        
        if (currentScore < requiredPoints) {
            _toastMessage.value = "İpucu için $requiredPoints puana ihtiyacınız var. Mevcut puanınız: $currentScore"
            return null // Yeterli puan yoksa ipucu verilmez
        }
        
        val currentWord = _currentWord.value!!
        
        // Tüm tahminlerdeki doğru ve yanlış pozisyondaki harfleri topla
        val knownLetters = mutableSetOf<Char>()
        _guesses.value?.forEach { guess ->
            guess.results.forEachIndexed { index, result ->
                if (result == LetterResult.CORRECT || result == LetterResult.WRONG_POSITION) {
                    knownLetters.add(guess.word[index])
                }
            }
        }
        
        // Henüz bilinmeyen harfleri bul
        val unknownLetters = currentWord.toList().filterNot { it in knownLetters }
        
        if (unknownLetters.isEmpty()) return null
        
        val randomLetter = unknownLetters.random()
        _hintAvailable.value = false
        
        // İpucu kullanımı için puan düşür
        val hintPenalty = when (letterCount) {
            4 -> 10   // 4 harfli kelimeler için daha az ceza
            5 -> 15   // 5 harfli kelimeler için orta ceza
            6 -> 20   // 6 harfli kelimeler için daha fazla ceza
            else -> 10
        }
        val newScore = currentScore - hintPenalty
        saveScore(newScore)
        
        return randomLetter
    }

    fun purchaseExtraAttempt(accepted: Boolean = false): Boolean {
        if (!accepted) {
            _extraAttemptAvailable.value = false
            _gameState.value = GameState.LOST
            return false
        }

        val currentScore = _score.value ?: 0
        if (currentScore >= 50) {
            saveScore(currentScore - 50)
            currentAttempt--
            extraAttemptUsed = true
            _gameState.value = GameState.PLAYING
            _extraAttemptAvailable.value = false

            // Yeni bir boş satır ekle
            val currentGuesses = _guesses.value?.toMutableList() ?: mutableListOf()
            val emptyWord = " ".repeat(letterCount)
            val emptyResults = Array(letterCount) { LetterResult.WRONG }
            currentGuesses.add(GuessResult(emptyWord, emptyResults.toList()))
            _guesses.value = currentGuesses
            return true
        }
        _extraAttemptAvailable.value = false
        _gameState.value = GameState.LOST
        return false
    }
} 