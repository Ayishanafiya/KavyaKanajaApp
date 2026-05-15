package com.example.kavyakanaja

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KavyaKanajaRoot()
        }
    }
}

data class WordMeaning(
    val word: String,
    val meaning: String,
    val modernKannada: String,
    val example: String
)

data class Poem(
    val id: Int,
    val title: String,
    val poet: String,
    val text: String,
    val bhavartha: String,
    val audioFile: String,
    val difficultWords: List<WordMeaning>
)

data class Poet(
    val name: String,
    val years: String,
    val bio: String,
    val works: List<String>,
    val award: String
)

data class KavyaData(
    val poems: List<Poem>,
    val poets: List<Poet>
)

enum class Screen(val label: String) {
    Today("Today"),
    Poems("Poems"),
    Poets("Poets"),
    Settings("Settings")
}

enum class AppTheme(val label: String, val description: String) {
    Classic("Classic", "Warm paper and literary maroon"),
    Forest("Forest", "Soft green reading theme"),
    Royal("Royal", "Calm blue scholarly theme")
}

@Composable
fun KavyaKanajaRoot() {
    val context = LocalContext.current
    val preferences = remember { context.getSharedPreferences("kavya_settings", 0) }
    val savedTheme = remember {
        runCatching {
            AppTheme.valueOf(preferences.getString("theme", AppTheme.Classic.name) ?: AppTheme.Classic.name)
        }.getOrDefault(AppTheme.Classic)
    }
    var selectedTheme by remember { mutableStateOf(savedTheme) }

    KavyaKanajaTheme(selectedTheme) {
        KavyaKanajaApp(
            selectedTheme = selectedTheme,
            onThemeChange = {
                selectedTheme = it
                preferences.edit().putString("theme", it.name).apply()
            }
        )
    }
}

@Composable
fun KavyaKanajaTheme(theme: AppTheme, content: @Composable () -> Unit) {
    val colors = when (theme) {
        AppTheme.Classic -> androidx.compose.material3.lightColorScheme(
            primary = Color(0xFF8F3E2F),
            secondary = Color(0xFF336B57),
            background = Color(0xFFFFF8EA),
            surface = Color(0xFFFFFBF2),
            onPrimary = Color.White,
            onBackground = Color(0xFF2D211A),
            onSurface = Color(0xFF2D211A)
        )
        AppTheme.Forest -> androidx.compose.material3.lightColorScheme(
            primary = Color(0xFF2F6B4F),
            secondary = Color(0xFF9B6A2F),
            background = Color(0xFFF3FAF3),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF17251D),
            onSurface = Color(0xFF17251D)
        )
        AppTheme.Royal -> androidx.compose.material3.lightColorScheme(
            primary = Color(0xFF315D8C),
            secondary = Color(0xFF8F3E2F),
            background = Color(0xFFF2F7FC),
            surface = Color(0xFFFFFFFF),
            onPrimary = Color.White,
            onBackground = Color(0xFF182333),
            onSurface = Color(0xFF182333)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography.copy(
            headlineLarge = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            ),
            titleLarge = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            bodyLarge = TextStyle(fontSize = 18.sp, lineHeight = 30.sp),
            bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 22.sp)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KavyaKanajaApp(
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    val context = LocalContext.current
    var data by remember { mutableStateOf<KavyaData?>(null) }
    var screen by remember { mutableStateOf(Screen.Today) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val json = context.assets.open("kavya_data.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<KavyaData>() {}.type
        data = Gson().fromJson(json, type)
    }

    val currentData = data
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (!isLoggedIn) {
            LoginScreen(
                onLogin = {
                    username = it
                    isLoggedIn = true
                }
            )
        } else if (currentData == null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("ಕಾವ್ಯ ಕಣಜ", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(8.dp))
                Text("Loading poems...")
            }
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        NavigationBarItem(
                            selected = screen == Screen.Today,
                            onClick = { screen = Screen.Today },
                            icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                            label = { Text(Screen.Today.label) }
                        )
                        NavigationBarItem(
                            selected = screen == Screen.Poems,
                            onClick = { screen = Screen.Poems },
                            icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                            label = { Text(Screen.Poems.label) }
                        )
                        NavigationBarItem(
                            selected = screen == Screen.Poets,
                            onClick = { screen = Screen.Poets },
                            icon = { Icon(Icons.Default.Person, contentDescription = null) },
                            label = { Text(Screen.Poets.label) }
                        )
                        NavigationBarItem(
                            selected = screen == Screen.Settings,
                            onClick = { screen = Screen.Settings },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text(Screen.Settings.label) }
                        )
                    }
                }
            ) { padding ->
                when (screen) {
                    Screen.Today -> TodayScreen(currentData.poems.dailyPoem(), padding, username)
                    Screen.Poems -> PoemsScreen(currentData.poems, padding)
                    Screen.Poets -> PoetsScreen(currentData.poets, padding)
                    Screen.Settings -> SettingsScreen(
                        username = username,
                        selectedTheme = selectedTheme,
                        onThemeChange = onThemeChange,
                        onLogout = {
                            username = ""
                            isLoggedIn = false
                            screen = Screen.Today
                        },
                        padding = padding
                    )
                }
            }
        }
    }
}

fun List<Poem>.dailyPoem(): Poem {
    val day = LocalDate.now().dayOfYear
    return this[(day - 1) % size]
}

@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
                Text("ಕಾವ್ಯ ಕಣಜ", style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
                Text(
                    "Login to continue learning Kannada poetry",
                    color = Color(0xFF6B5C4E),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Username") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )

                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        val cleanName = username.trim()
                        if (cleanName.isNotBlank() && password == "1234") {
                            onLogin(cleanName)
                        } else {
                            errorMessage = "Enter any username and use password: 1234"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                TextButton(
                    onClick = {
                        username = "student"
                        password = "1234"
                        errorMessage = null
                    }
                ) {
                    Text("Use demo account")
                }
            }
        }
    }
}

@Composable
fun TodayScreen(poem: Poem, padding: PaddingValues, username: String) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            AppHeader(username)
            Spacer(Modifier.height(10.dp))
            PoemCard(poem = poem, isFeatured = true)
        }
    }
}

@Composable
fun PoemsScreen(poems: List<Poem>, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Poetry Granary", style = MaterialTheme.typography.headlineLarge)
            Text("Read, listen, and learn Kannada literature.", color = Color(0xFF6B5C4E))
        }
        items(poems) { poem ->
            PoemCard(poem = poem, isFeatured = false)
        }
    }
}

@Composable
fun PoetsScreen(poets: List<Poet>, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Poet's Corner", style = MaterialTheme.typography.headlineLarge)
            Text("Jnanpith awardees and Kannada literary icons.", color = Color(0xFF6B5C4E))
        }
        items(poets) { poet ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(poet.name, style = MaterialTheme.typography.titleLarge)
                    Text(poet.years, color = Color(0xFF8F3E2F), fontWeight = FontWeight.SemiBold)
                    Text(poet.bio, style = MaterialTheme.typography.bodyMedium)
                    Text("Famous works: ${poet.works.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                    Text(poet.award, color = Color(0xFF336B57), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    username: String,
    selectedTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit,
    onLogout: () -> Unit,
    padding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Profile & Settings", style = MaterialTheme.typography.headlineLarge)
            Text("Manage your learner profile and reading theme.", color = Color(0xFF6B5C4E))
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(username, style = MaterialTheme.typography.titleLarge)
                        Text("Demo learner account", color = Color(0xFF6B5C4E))
                    }
                }
            }
        }

        item {
            Text("Choose App Theme", style = MaterialTheme.typography.titleLarge)
        }

        items(AppTheme.values()) { theme ->
            ThemeChoiceCard(
                theme = theme,
                selected = selectedTheme == theme,
                onClick = { onThemeChange(theme) }
            )
        }

        item {
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun ThemeChoiceCard(theme: AppTheme, selected: Boolean, onClick: () -> Unit) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(if (selected) 3.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeSwatches(theme)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(theme.label, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(theme.description, color = Color(0xFF6B5C4E), fontSize = 14.sp)
            }
            Text(
                if (selected) "Selected" else "Tap",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ThemeSwatches(theme: AppTheme) {
    val colors = when (theme) {
        AppTheme.Classic -> listOf(Color(0xFF8F3E2F), Color(0xFF336B57), Color(0xFFFFF8EA))
        AppTheme.Forest -> listOf(Color(0xFF2F6B4F), Color(0xFF9B6A2F), Color(0xFFF3FAF3))
        AppTheme.Royal -> listOf(Color(0xFF315D8C), Color(0xFF8F3E2F), Color(0xFFF2F7FC))
    }

    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(color, RoundedCornerShape(11.dp))
            )
        }
    }
}

@Composable
fun AppHeader(username: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text("ಕಾವ್ಯ ಕಣಜ", style = MaterialTheme.typography.headlineLarge)
        }
        Text(
            "Kavya-Kanaja",
            color = Color(0xFF336B57),
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Text(
            "Welcome, $username",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun PoemCard(poem: Poem, isFeatured: Boolean) {
    var selectedWord by remember { mutableStateOf<WordMeaning?>(null) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(if (isFeatured) 4.dp else 2.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (isFeatured) {
                Text("Poem of the Day", color = Color(0xFF8F3E2F), fontWeight = FontWeight.Bold)
            }
            Text(poem.title, style = MaterialTheme.typography.titleLarge)
            Text("by ${poem.poet}", color = Color(0xFF6B5C4E))
            TappablePoemText(poem, onWordTap = { selectedWord = it })
            AudioPlayerButton(poem.audioFile, poem.title, poem.text)
            BhavarthaBox(poem.bhavartha)
        }
    }

    selectedWord?.let { word ->
        AlertDialog(
            onDismissRequest = { selectedWord = null },
            confirmButton = {
                TextButton(onClick = { selectedWord = null }) {
                    Text("Close")
                }
            },
            title = { Text(word.word) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Meaning: ${word.meaning}")
                    Text("Modern Kannada: ${word.modernKannada}")
                    Text("Example: ${word.example}")
                }
            }
        )
    }
}

@Composable
fun TappablePoemText(poem: Poem, onWordTap: (WordMeaning) -> Unit) {
    val difficultMap = poem.difficultWords.associateBy { it.word.trim() }
    val difficultWordColor = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        poem.text.lines().forEach { line ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                line.split(" ").forEach { rawWord ->
                    val key = rawWord.trim(',', '.', ';', ':', '!', '?', '।')
                    val meaning = difficultMap[key]
                    val text = buildAnnotatedString {
                        if (meaning != null) {
                            withStyle(
                                SpanStyle(
                                    color = difficultWordColor,
                                    fontWeight = FontWeight.Bold
                                )
                            ) { append(rawWord) }
                        } else {
                            append(rawWord)
                        }
                    }
                    Text(
                        text = text,
                        modifier = Modifier
                            .padding(horizontal = 3.dp, vertical = 2.dp)
                            .then(if (meaning != null) Modifier.clickable { onWordTap(meaning) } else Modifier),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}


@Composable
fun BhavarthaBox(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3E6CF), RoundedCornerShape(8.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("Bhavartha", fontWeight = FontWeight.Bold, color = Color(0xFF8F3E2F))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
@Composable
fun AudioPlayerButton(audioFile: String, poemTitle: String, poemText: String) {
    val context = LocalContext.current
    val mainHandler = remember { Handler(Looper.getMainLooper()) }
    var isPlaying by remember { mutableStateOf(false) }
    var ttsReady by remember { mutableStateOf(false) }
    var kannadaVoiceAvailable by remember { mutableStateOf(true) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val engine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                mainHandler.post {
                    val result = ttsEngine?.setLanguage(Locale("kn", "IN"))
                    kannadaVoiceAvailable = result != null &&
                            result != TextToSpeech.LANG_MISSING_DATA &&
                            result != TextToSpeech.LANG_NOT_SUPPORTED
                    ttsEngine?.setSpeechRate(0.82f)
                    ttsEngine?.setPitch(0.95f)
                    ttsReady = true
                }
            } else {
                mainHandler.post {
                    kannadaVoiceAvailable = false
                    ttsReady = false
                }
            }
        }
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit

            override fun onDone(utteranceId: String?) {
                mainHandler.post { isPlaying = false }
            }

            override fun onError(utteranceId: String?) {
                mainHandler.post { isPlaying = false }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                mainHandler.post { isPlaying = false }
            }
        })
        ttsEngine = engine

        onDispose {
            mediaPlayer?.release()
            engine.stop()
            engine.shutdown()
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            modifier = Modifier
                .size(52.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(26.dp)),
            onClick = {
                if (isPlaying) {
                    mediaPlayer?.pause()
                    ttsEngine?.stop()
                    isPlaying = false
                } else {
                    val player = mediaPlayer ?: if (audioFile.isNotBlank()) {
                        runCatching {
                            val descriptor = context.assets.openFd(audioFile)
                            MediaPlayer().apply {
                                setDataSource(descriptor.fileDescriptor, descriptor.startOffset, descriptor.length)
                                descriptor.close()
                                prepare()
                                setOnCompletionListener { isPlaying = false }
                            }
                        }.getOrNull()
                    } else {
                        null
                    }

                    if (player != null) {
                        ttsEngine?.stop()
                        mediaPlayer = player
                        player.start()
                        isPlaying = true
                    } else if (ttsReady) {
                        val speechText = "$poemTitle. ${poemText.replace("\n", ". ")}"
                        val result = ttsEngine?.speak(
                            speechText,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            "poem_${poemTitle.hashCode()}"
                        )
                        isPlaying = result == TextToSpeech.SUCCESS
                        if (isPlaying && !kannadaVoiceAvailable) {
                            Toast.makeText(
                                context,
                                "Kannada voice not installed. Using default device voice.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play audio",
                tint = Color.White
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            when {
                isPlaying -> "Recitation playing"
                !ttsReady -> "Preparing AI voice"
                !kannadaVoiceAvailable -> "Default Voice Recitation"
                audioFile.isBlank() -> "AI Voice Recitation"
                else -> "Listen & Learn"
            },
            fontWeight = FontWeight.SemiBold
        )
    }
}
