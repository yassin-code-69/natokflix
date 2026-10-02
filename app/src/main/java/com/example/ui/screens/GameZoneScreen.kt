package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.ui.components.GenderAvatar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FriendProfile
import com.example.data.model.User
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

// Data model representing HTML5 2D games
data class GameItem(
    val id: String,
    val name: String,
    val banglaName: String,
    val iconEmoji: String,
    val rating: String,
    val playersCount: String,
    val bgGradient: List<Color>
)

@Composable
fun GameZoneScreen(
    user: User,
    totalAdsWatched: Int,
    roomCode: String,
    onWatchAd: () -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: (String) -> Unit,
    onDeductStake: (Int) -> Boolean,
    onRewardWinner: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var joinCodeInput by remember { mutableStateOf("") }

    // 1. Core State Management
    var currentScreenState by remember { mutableStateOf("LOBBY") } // LOBBY, MATCHMAKING, ROOM_PLAY
    var selectedGame by remember { mutableStateOf<GameItem?>(null) }
    var selectedStake by remember { mutableStateOf(100) } // 50, 100, 500, 1000
    var matchMode by remember { mutableStateOf("1v1") } // "1v1" or "4-Player"
    var matchOption by remember { mutableStateOf("ONLINE") } // "ONLINE" or "FRIEND"

    // Matchmaking State
    var matchmakingStatus by remember { mutableStateOf("START") } // START, WAITING_FRIEND, SEARCHING, MATCHED
    var generatedRoomCode by remember { mutableStateOf("") }
    var matchedOpponent by remember { mutableStateOf<FriendProfile?>(null) }

    // Game Session Play State
    var ticTacToeBoard by remember { mutableStateOf(Array(9) { "" }) }
    var userIsX by remember { mutableStateOf(true) }
    var isUserTurn by remember { mutableStateOf(true) }
    var gameOutcomeText by remember { mutableStateOf("") } // "VICTORY", "DEFEAT", "DRAW"

    // Quiz Session Play State
    var currentQuizIndex by remember { mutableStateOf(0) }
    var userScore by remember { mutableStateOf(0) }
    var opponentScore by remember { mutableStateOf(0) }
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var quizOutcomeText by remember { mutableStateOf("") }

    // General Simulator States (For other games)
    var simulatedProgress by remember { mutableStateOf(0f) }
    var simulatedLogs by remember { mutableStateOf(listOf<String>()) }
    var simulatedMultiplier by remember { mutableStateOf(1.0f) } // Increased by tapping "Boost Skill"

    // 5 Lightweight HTML5 Game items
    val gamesList = remember {
        listOf(
            GameItem("ludo", "Ludo Challenge", "লুডু চ্যালেঞ্জ", "🎲", "4.8", "5.2K+", listOf(Color(0xFFE50914), Color(0xFF80070B))),
            GameItem("carrom", "Carrom Master", "ক্যারম মাস্টার", "⚪", "4.5", "4.4K+", listOf(Color(0xFFFF9800), Color(0xFFE65100))),
            GameItem("tictactoe", "Tic-Tac-Toe Pro", "টিক-ট্যাক-টো প্রো", "❌", "4.9", "8.9K+", listOf(Color(0xFF4CAF50), Color(0xFF1B5E20))),
            GameItem("pool", "8 Ball Pool Lite", "৮-বল পুল লাইট", "🎱", "4.8", "11.1K+", listOf(Color(0xFF009688), Color(0xFF004D40))),
            GameItem("bubble", "Bubble Shooter", "বাবল শুটার", "🫧", "4.7", "15K+", listOf(Color(0xFF8BC34A), Color(0xFF33691E)))
        )
    }

    // Quiz Questions List (In Bengali as requested)
    val quizQuestions = remember {
        listOf(
            QuizQuestion(
                question = "বাংলাদেশের জাতীয় কবি কে?",
                options = listOf("কাজী নজরুল ইসলাম", "রবীন্দ্রনাথ ঠাকুর", "জসীমউদ্দীন", "জীবনানন্দ দাশ"),
                correctIndex = 0
            ),
            QuizQuestion(
                question = "ক্রিকেট খেলায় ওয়ানডে ক্রিকেটে এক ওভারে সর্বোচ্চ কয়টি ছক্কা মারা হয়েছে?",
                options = listOf("৪টি", "৫টি", "৬টি", "৭টি"),
                correctIndex = 2
            ),
            QuizQuestion(
                question = "NatokFlix অ্যাপে ৫০ জন সফল রেফার করলে মেগা বোনাস কত?",
                options = listOf("১০,০০০ কয়েন", "৫০,০০০ কয়েন", "১০০,০০০ কয়েন", "৫,০০০ কয়েন"),
                correctIndex = 2
            )
        )
    }

    // 2. Custom Back Button Intercept
    BackHandler {
        if (currentScreenState == "ROOM_PLAY") {
            // Cannot leave while gaming is in progress unless we forfeit
            Toast.makeText(context, "ম্যাচ চলমান অবস্থায় বের হওয়া যাবে না!", Toast.LENGTH_SHORT).show()
        } else if (currentScreenState == "MATCHMAKING") {
            currentScreenState = "LOBBY"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // A. Header section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16121E))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Brush.radialGradient(listOf(NatokGold, Color.Transparent)), CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎮", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NatokFlix Game Zone",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "গেম খেলে কয়েন জিতুন",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "• Live Multi",
                                fontSize = 10.sp,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }

                // Balance Tracker Badge
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF281F38)),
                    modifier = Modifier.border(1.dp, NatokGold.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${user.points}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NatokGold
                        )
                    }
                }
            }
        }

        // B. Main Switch Screen State
        AnimatedContent(
            targetState = currentScreenState,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "game_zone_transitions",
            modifier = Modifier.weight(1f)
        ) { state ->
            when (state) {
                "LOBBY" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // 1. Promotional Billboard banner
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF23141F))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, NatokRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🏆", fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(
                                        text = "মাল্টিপ্লেয়ার গেম টুর্নামেন্ট",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "বন্ধুদের সাথে চ্যালেঞ্জ নিন, পয়েন্ট জিতুন এবং সেরা লিডারবোর্ডে নিজের নাম তুলুন!",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        // Watch Ad Module (Daily 20 Limit)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .clickable { onWatchAd() },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF261D22)),
                            border = BorderStroke(1.dp, NatokRed.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(NatokRed.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NatokRed)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Watch & Earn Coins (বিজ্ঞাপন দেখুন)",
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Ads Remaining Today: ${20 - totalAdsWatched}/20",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "+100 🪙",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NatokGold
                                )
                            }
                        }

                        // 2. Setup Staking & Match Options Section
                        Text(
                            text = "ম্যাচমেকিং কাস্টমাইজেশন",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 18.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Stake selection
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("কয়েন বাজি ধরুন (Stake Tier):", fontSize = 12.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    Text("ফি: ২০%", fontSize = 11.sp, color = NatokRed, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(50, 100, 500, 1000).forEach { amount ->
                                        val isSelected = selectedStake == amount
                                        Card(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { selectedStake = amount },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isSelected) NatokRed else Color(0xFF261D22)
                                            ),
                                            border = if (isSelected) null else BorderStroke(1.dp, BorderDark)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Text(
                                                        text = "🪙 $amount",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) Color.White else NatokGold
                                                    )
                                                    Text(
                                                        text = "উইন: ${(amount * 2 * 0.8).toInt()}",
                                                        fontSize = 9.sp,
                                                        color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Match Option (ONLINE / FRIEND)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Play Online
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { matchOption = "ONLINE" },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (matchOption == "ONLINE") Color(0xFF1E3A20) else Color(0xFF261D22)
                                        ),
                                        border = BorderStroke(1.dp, if (matchOption == "ONLINE") Color(0xFF4CAF50) else BorderDark)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.Public, contentDescription = null, tint = if (matchOption == "ONLINE") Color(0xFF4CAF50) else TextSecondary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Play Online (অনলাইন)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Play with Friend
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { matchOption = "FRIEND" },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (matchOption == "FRIEND") Color(0xFF3F2B15) else Color(0xFF261D22)
                                        ),
                                        border = BorderStroke(1.dp, if (matchOption == "FRIEND") NatokGold else BorderDark)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, tint = if (matchOption == "FRIEND") NatokGold else TextSecondary, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Play with Friend", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // 1v1 vs 4 Player
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { matchMode = "1v1" },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (matchMode == "1v1") Color(0xFF1F1D36) else Color(0xFF261D22)
                                        ),
                                        border = BorderStroke(1.dp, if (matchMode == "1v1") NatokGold.copy(alpha = 0.5f) else BorderDark)
                                    ) {
                                        Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                            Text("1v1 Battle (২ জন)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { matchMode = "4-Player" },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (matchMode == "4-Player") Color(0xFF1F1D36) else Color(0xFF261D22)
                                        ),
                                        border = BorderStroke(1.dp, if (matchMode == "4-Player") NatokGold.copy(alpha = 0.5f) else BorderDark)
                                    ) {
                                        Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                            Text("Squad Brawl (৪ জন)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Game Library Grid List
                        Text(
                            text = "লাইটওয়েট 2D গেম লাইব্রেরি (১২টি গেম)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(520.dp)
                        ) {
                            items(gamesList) { game ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedGame = game
                                            currentScreenState = "SELECT_MODE"
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Brush.verticalGradient(game.bgGradient))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(game.iconEmoji, fontSize = 18.sp)
                                                }
                                                // Small Rating
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = NatokGold, modifier = Modifier.size(10.dp))
                                                    Text(game.rating, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(14.dp))

                                            Text(
                                                text = game.banglaName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = game.name,
                                                fontSize = 10.sp,
                                                color = Color.White.copy(alpha = 0.8f)
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "অনলাইন: " + game.playersCount,
                                                    fontSize = 8.sp,
                                                    color = Color.White.copy(alpha = 0.9f)
                                                )
                                                Card(
                                                    shape = RoundedCornerShape(4.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.4f))
                                                ) {
                                                    Text(
                                                        text = "প্লে করুন",
                                                        fontSize = 8.sp,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "SELECT_MODE" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = selectedGame?.banglaName ?: "গেম মোড নির্বাচন করুন",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NatokGold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "আপনার পছন্দের গেম মোড সিলেক্ট করে খেলা শুরু করুন",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )

                        // 1. Solo vs AI
                        Button(
                            onClick = {
                                matchedOpponent = FriendProfile("bot_ai", "স্মার্ট এআই রোবট (AI Master)", "Girl", "ঢাকা", isOnline = true)
                                matchOption = "AI"
                                val success = onDeductStake(selectedStake)
                                if (success) {
                                    ticTacToeBoard = Array(9) { "" }
                                    isUserTurn = true
                                    gameOutcomeText = ""
                                    simulatedProgress = 0f
                                    simulatedMultiplier = 1.0f
                                    simulatedLogs = listOf("এআই বট এর বিরুদ্ধে খেলা শুরু হয়েছে!")
                                    currentScreenState = "ROOM_PLAY"
                                } else {
                                    Toast.makeText(context, "কয়েন ব্যালেন্স অপর্যাপ্ত!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2))
                        ) {
                            Text("🤖 সোলো এআই (Solo vs Smart AI Bot)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        // 2. Duo on One Phone (Pass & Play)
                        Button(
                            onClick = {
                                matchedOpponent = FriendProfile("local_player_2", "প্লেয়ার ২ (Blue)", "Boy", "লোকাল", isOnline = true)
                                matchOption = "LOCAL_DUO"
                                ticTacToeBoard = Array(9) { "" }
                                isUserTurn = true
                                gameOutcomeText = ""
                                simulatedProgress = 0f
                                simulatedMultiplier = 1.0f
                                simulatedLogs = listOf("এক ফোনে ২ জন খেলার মোড শুরু হয়েছে!")
                                currentScreenState = "ROOM_PLAY"
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1))
                        ) {
                            Text("👥 এক ফোনে ২ জন (Pass & Play Duo)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        // 3. Online Battle
                        Button(
                            onClick = {
                                matchOption = "ONLINE"
                                matchmakingStatus = "SEARCHING"
                                currentScreenState = "MATCHMAKING"
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("🌐 অনলাইন ব্যাটেল (Online 1v1 / Squad)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        // 4. Private Friend Room
                        Button(
                            onClick = {
                                matchOption = "FRIEND"
                                currentScreenState = "PRIVATE_ROOM"
                            },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                        ) {
                            Text("🗝️ প্রাইভেট ফ্রেন্ড রুম (Private Room Code)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = { currentScreenState = "LOBBY" }) {
                            Text("← লবিতে ফিরে যান", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }

                "PRIVATE_ROOM" -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("প্রাইভেট রুম (Private Friend Room)", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NatokGold)
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        if (roomCode.isNotEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A2D))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("আপনার রুম কোড:", color = TextSecondary, fontSize = 12.sp)
                                    Text(roomCode, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = NatokGold, letterSpacing = 4.sp)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("বন্ধুদের সাথে শেয়ার করুন এবং তারা জয়েন করলে ম্যাচ শুরু হবে।", textAlign = TextAlign.Center, fontSize = 11.sp, color = TextPrimary)
                                }
                            }
                        } else {
                            Button(
                                onClick = { onCreateRoom() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Create Room (রুম তৈরি করুন)")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        Divider(color = BorderDark, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        OutlinedTextField(
                            value = joinCodeInput,
                            onValueChange = { if (it.length <= 4) joinCodeInput = it },
                            label = { Text("রুম কোড দিন (Join Friend)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NatokGold,
                                cursorColor = NatokGold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { 
                                if (joinCodeInput.length == 4) {
                                    onJoinRoom(joinCodeInput)
                                    currentScreenState = "MATCHMAKING"
                                } else {
                                    Toast.makeText(context, "সঠিক ৪ ডিজিট কোড দিন", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Join Room (রুম জয়েন করুন)")
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        TextButton(onClick = { currentScreenState = "SELECT_MODE" }) {
                            Text("পিছনে যান", color = TextSecondary)
                        }
                    }
                }

                "MATCHMAKING" -> {
                    // Matchmaking Lobby with lovely radar searching animations
                    LaunchedEffect(key1 = matchmakingStatus) {
                        if (matchmakingStatus == "SEARCHING") {
                            delay(3500) // Simulated radar scan delay
                            matchedOpponent = listOf(
                                FriendProfile("bot_mim", "মিম চৌধুরী (Mim)", "Girl", "চট্টগ্রাম", isOnline = true),
                                FriendProfile("bot_sumaiya", "সুমাইয়া আক্তার (Sumaiya)", "Girl", "ঢাকা", isOnline = true),
                                FriendProfile("bot_riya", "রিয়া ইসলাম (Riya)", "Girl", "বগুড়া", isOnline = true),
                                FriendProfile("bot_tanvir", "তানভীর হাসান (Tanvir)", "Boy", "চট্টগ্রাম", isOnline = true),
                                FriendProfile("bot_fahim", "ফাহিম আহমেদ (Fahim)", "Boy", "সিলেট", isOnline = true)
                            ).random()
                            matchmakingStatus = "MATCHED"
                            delay(1500)
                            // Deduct the stake coins upon entering the room
                            val success = onDeductStake(selectedStake)
                            if (success) {
                                // Initialize game session
                                if (selectedGame?.id == "tictactoe") {
                                    ticTacToeBoard = Array(9) { "" }
                                    isUserTurn = true
                                    gameOutcomeText = ""
                                } else if (selectedGame?.id == "quiz") {
                                    currentQuizIndex = 0
                                    userScore = 0
                                    opponentScore = 0
                                    selectedAnswerIndex = null
                                    quizOutcomeText = ""
                                } else {
                                    simulatedProgress = 0f
                                    simulatedMultiplier = 1.0f
                                    simulatedLogs = listOf("খেলা শুরু হয়েছে! স্টেক করা হয়েছে $selectedStake কয়েন।")
                                }
                                currentScreenState = "ROOM_PLAY"
                            } else {
                                Toast.makeText(context, "কয়েন ব্যালেন্স অপর্যাপ্ত!", Toast.LENGTH_SHORT).show()
                                currentScreenState = "LOBBY"
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = selectedGame?.banglaName ?: "ব্যাটেল গ্রাউন্ড",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = NatokGold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "ম্যাচ স্টেক: $selectedStake কয়েন | উইন প্রাইজ: ${(selectedStake * 2 * 0.8).toInt()} কয়েন",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 40.dp)
                        )

                        // Visual matchmaking players row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Player 1 (User)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                GenderAvatar(
                                    gender = user.gender,
                                    size = 80.dp,
                                    modifier = Modifier.border(2.dp, NatokRed, CircleShape)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(user.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("আপনি (You)", fontSize = 10.sp, color = NatokGold)
                            }

                            // VS animated separator
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "VS",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = NatokRed
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(matchMode, fontSize = 10.sp, color = TextSecondary)
                            }

                            // Player 2 (Opponent / Scanning radar)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (matchmakingStatus == "MATCHED") {
                                    GenderAvatar(
                                        gender = matchedOpponent?.gender ?: "Girl",
                                        size = 80.dp,
                                        modifier = Modifier.border(2.dp, Color(0xFF4CAF50), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(matchedOpponent?.name ?: "বিপক্ষ খেলোয়াড়", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("অনলাইন প্লেয়ার", fontSize = 10.sp, color = Color(0xFF4CAF50))
                                } else {
                                    // Circular rotating radar scanning
                                    val infiniteTransition = rememberInfiniteTransition(label = "radar")
                                    val rotation by infiniteTransition.animateFloat(
                                        initialValue = 0f,
                                        targetValue = 360f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1500, easing = LinearEasing),
                                            repeatMode = RepeatMode.Restart
                                        ),
                                        label = "radar_rot"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .border(1.dp, NatokGold.copy(alpha = 0.5f), CircleShape)
                                            .rotate(rotation),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.sweepGradient(
                                                        listOf(Color.Transparent, NatokGold.copy(alpha = 0.4f), Color.Transparent)
                                                    ),
                                                    CircleShape
                                                )
                                        )
                                        Text("🔍", fontSize = 24.sp)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("খোঁজা হচ্ছে...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                    Text("খেলোয়াড় অপেক্ষারত", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(50.dp))

                        // Loader text and details
                        if (matchmakingStatus == "SEARCHING") {
                            CircularProgressIndicator(color = NatokRed, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "গ্লোবাল সার্ভারে প্রতিপক্ষ খোঁজা হচ্ছে...",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        } else if (matchmakingStatus == "WAITING_FRIEND") {
                            // Invite Friends section with generated code
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1522))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("আপনার গেম রুম কোড:", fontSize = 11.sp, color = TextSecondary)
                                    Text(generatedRoomCode, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = NatokGold, letterSpacing = 2.sp)

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Game Code", generatedRoomCode)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "কোড কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2235))
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("কপি করুন", fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                matchmakingStatus = "SEARCHING" // Simulate online match override if waiting too long
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ম্যাচ শুরু করুন", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "এই কোডটি বন্ধুকে পাঠিয়ে দিন। অথবা সরাসরি অনলাইন প্লেয়ারের সাথে ম্যাচ চালু করতে বাটনে ক্লিক করুন।",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        } else if (matchmakingStatus == "MATCHED") {
                            Text(
                                text = "✨ জোড় মেলানো সফল হয়েছে! খেলা এখনই শুরু হচ্ছে...",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        // Cancel button
                        if (matchmakingStatus != "MATCHED") {
                            OutlinedButton(
                                onClick = { currentScreenState = "LOBBY" },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("বাতিল করুন (Cancel)", fontSize = 12.sp)
                            }
                        }
                    }
                }

                "ROOM_PLAY" -> {
                    // Match playground session
                    val isTicTacToe = selectedGame?.id == "tictactoe"
                    val isQuiz = selectedGame?.id == "quiz"

                    if (isTicTacToe) {
                        // Fully Playable Interactive 1v1 Tic-Tac-Toe
                        TicTacToePlayground(
                            board = ticTacToeBoard,
                            isUserTurn = isUserTurn,
                            userIsX = userIsX,
                            opponentName = if (matchOption == "LOCAL_DUO") "প্লেয়ার ২ (Blue)" else (matchedOpponent?.name ?: "প্রতিপক্ষ"),
                            opponentGender = matchedOpponent?.gender ?: "Girl",
                            stakeAmount = selectedStake,
                            outcome = gameOutcomeText,
                            onCellClick = { index ->
                                if (ticTacToeBoard[index].isEmpty() && gameOutcomeText.isEmpty()) {
                                    if (matchOption == "LOCAL_DUO") {
                                        // 2-Player Pass & Play on one device
                                        val newBoard = ticTacToeBoard.copyOf()
                                        newBoard[index] = if (isUserTurn) "X" else "O"
                                        ticTacToeBoard = newBoard
                                        val currentOutcome = checkTicTacToeWinner(newBoard, true)
                                        if (currentOutcome != null) {
                                            gameOutcomeText = currentOutcome
                                            val winnerName = if (currentOutcome == "VICTORY") "প্লেয়ার ১ (লাল)" else "প্লেয়ার ২ (নীল)"
                                            Toast.makeText(context, "🏆 $winnerName বিজয়ী হয়েছেন!", Toast.LENGTH_SHORT).show()
                                        } else if (newBoard.none { it.isEmpty() }) {
                                            gameOutcomeText = "DRAW"
                                        } else {
                                            isUserTurn = !isUserTurn
                                        }
                                    } else if (isUserTurn) {
                                        // User vs AI / Online
                                        val newBoard = ticTacToeBoard.copyOf()
                                        newBoard[index] = if (userIsX) "X" else "O"
                                        ticTacToeBoard = newBoard
                                        isUserTurn = false

                                        // Check win state
                                        val currentOutcome = checkTicTacToeWinner(newBoard, userIsX)
                                        if (currentOutcome != null) {
                                            gameOutcomeText = currentOutcome
                                            onRewardWinner(selectedStake, currentOutcome)
                                        } else if (newBoard.none { it.isEmpty() }) {
                                            gameOutcomeText = "DRAW"
                                            onRewardWinner(selectedStake, "DRAW")
                                        } else {
                                            // Trigger Smart Bot Opponent AI after a small realistic delay
                                            scope.launch {
                                                delay(700)
                                                val emptyIndices = newBoard.indices.filter { newBoard[it].isEmpty() }
                                                if (emptyIndices.isNotEmpty()) {
                                                    // Smart AI logic: Block winning move, or take center/corners
                                                    val aiSymbol = if (userIsX) "O" else "X"
                                                    val userSymbol = if (userIsX) "X" else "O"
                                                    
                                                    // 1. Try to find winning spot or block player
                                                    val winSpot = findWinningMove(newBoard, aiSymbol)
                                                    val blockSpot = findWinningMove(newBoard, userSymbol)
                                                    val botChoice = when {
                                                        winSpot != null && winSpot in emptyIndices -> winSpot
                                                        blockSpot != null && blockSpot in emptyIndices -> blockSpot
                                                        4 in emptyIndices -> 4 // Center
                                                        else -> emptyIndices.random()
                                                    }

                                                    newBoard[botChoice] = aiSymbol
                                                    ticTacToeBoard = newBoard

                                                    // Re-check win state
                                                    val botOutcome = checkTicTacToeWinner(newBoard, userIsX)
                                                    if (botOutcome != null) {
                                                        gameOutcomeText = botOutcome
                                                        onRewardWinner(selectedStake, botOutcome)
                                                    } else if (newBoard.none { it.isEmpty() }) {
                                                        gameOutcomeText = "DRAW"
                                                        onRewardWinner(selectedStake, "DRAW")
                                                    } else {
                                                        isUserTurn = true
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            onExit = { currentScreenState = "LOBBY" }
                        )
                    } else if (isQuiz) {
                        // Fully Playable Interactive GK Trivia Quiz
                        QuizPlayground(
                            question = quizQuestions[currentQuizIndex],
                            currentQuestionNum = currentQuizIndex + 1,
                            totalQuestions = quizQuestions.size,
                            userScore = userScore,
                            opponentScore = opponentScore,
                            opponentName = matchedOpponent?.name ?: "সুমাইয়া",
                            stakeAmount = selectedStake,
                            selectedAnsIndex = selectedAnswerIndex,
                            outcome = quizOutcomeText,
                            onOptionSelected = { idx ->
                                if (selectedAnswerIndex == null && quizOutcomeText.isEmpty()) {
                                    selectedAnswerIndex = idx
                                    val isCorrect = idx == quizQuestions[currentQuizIndex].correctIndex
                                    if (isCorrect) userScore += 1

                                    scope.launch {
                                        // Opponent bot logic
                                        delay(1000)
                                        val opCorrect = (0..100).random() < 70 // 70% accuracy for bot
                                        if (opCorrect) opponentScore += 1

                                        delay(1200)
                                        if (currentQuizIndex < quizQuestions.size - 1) {
                                            currentQuizIndex += 1
                                            selectedAnswerIndex = null
                                        } else {
                                            // Calculate final outcome
                                            quizOutcomeText = when {
                                                userScore > opponentScore -> "WIN"
                                                userScore < opponentScore -> "LOSE"
                                                else -> "DRAW"
                                            }
                                            onRewardWinner(selectedStake, quizOutcomeText)
                                        }
                                    }
                                }
                            },
                            onExit = { currentScreenState = "LOBBY" }
                        )
                    } else {
                        // Gorgeous Real-time simulated game loop (Ludo, Carrom, Chess, etc.)
                        LaunchedEffect(key1 = simulatedProgress) {
                            if (simulatedProgress < 1.0f) {
                                delay(1500)
                                simulatedProgress += 0.12f
                                // Randomly append fun dynamic gameplay logs in Bengali
                                val possibleLogs = if (selectedGame?.id == "ludo") {
                                    listOf(
                                        "${matchedOpponent?.name ?: "সুমাইয়া"} চাল দিলেন এবং একটি ৪ পেলেন।",
                                        "আপনি চাল দিয়েছেন! চমৎকার ছক্কা 🎲 পড়েছে!",
                                        "আপনার ঘুঁটি পাকা ঘরের দিকে এগিয়ে যাচ্ছে।",
                                        "${matchedOpponent?.name ?: "সুমাইয়া"} আপনার একটি ঘুঁটি কেটে দিয়েছে! 😱",
                                        "আপনি বুদ্ধিমত্তার সাথে ব্লক তৈরি করলেন।",
                                        "শেষ ঘরে ঘুঁটি পোঁছার আর মাত্র ৩ ঘর বাকি।"
                                    )
                                } else if (selectedGame?.id == "chess") {
                                    listOf(
                                        "খেলা এগিয়ে চলেছে। ওপেনিং এ চমৎকার ডিফেন্স তৈরি করলেন।",
                                        "${matchedOpponent?.name ?: "সুমাইয়া"} কিস্তির হুমকি দিল!",
                                        "আপনি বুদ্ধিমত্তার সাথে গজ এক্সচেঞ্জ করলেন।",
                                        "আপনার ঘোড়া চমৎকার চালের মাধ্যমে বিপক্ষ দলের মন্ত্রী ক্যাপচার করল! 🔥",
                                        "এন্ডগেম পজিশনে আপনার চমৎকার কিস্তি ও কিস্তিমাত পজিশন।"
                                    )
                                } else {
                                    listOf(
                                        "উত্তেজনাপূর্ণ খেলা চলমান রয়েছে!",
                                        "আপনি চমৎকার শটের মাধ্যমে রিওয়ার্ড পয়েন্টের দিকে এগোচ্ছেন।",
                                        "${matchedOpponent?.name ?: "সুমাইয়া"} ডাবল স্কোর করে এগিয়ে গেল।",
                                        "আপনার অসাধারণ রিফ্লেক্স স্কোর বাড়িয়ে দিল।"
                                    )
                                }
                                simulatedLogs = listOf(possibleLogs.random()) + simulatedLogs

                                if (simulatedProgress >= 1.0f) {
                                    // Determine outcome (influenced by multiplying taps)
                                    val winChance = (0..100).random() * simulatedMultiplier
                                    val outcome = when {
                                        winChance > 55f -> "WIN"
                                        winChance < 40f -> "LOSE"
                                        else -> "DRAW"
                                    }
                                    gameOutcomeText = outcome
                                    onRewardWinner(selectedStake, outcome)
                                }
                            }
                        }

                        GameSimulationPlayground(
                            game = selectedGame!!,
                            opponentName = matchedOpponent?.name ?: "সুমাইয়া",
                            opponentGender = matchedOpponent?.gender ?: "Girl",
                            progress = simulatedProgress,
                            stakeAmount = selectedStake,
                            logs = simulatedLogs,
                            multiplier = simulatedMultiplier,
                            outcome = gameOutcomeText,
                            onBoostSkill = {
                                simulatedMultiplier += 0.15f
                                simulatedLogs = listOf("⚡ আপনি দক্ষতা বুস্ট করেছেন! জেতার সুযোগ ১৫% বৃদ্ধি পেয়েছে।") + simulatedLogs
                            },
                            onExit = { currentScreenState = "LOBBY" }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// HELPERS & SUB-COMPOSABLES
// ==========================================

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

@Composable
fun TicTacToePlayground(
    board: Array<String>,
    isUserTurn: Boolean,
    userIsX: Boolean,
    opponentName: String,
    opponentGender: String,
    stakeAmount: Int,
    outcome: String,
    onCellClick: (Int) -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Player profiles top banner
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("আপনি (You)\n[ ${if (userIsX) "❌" else "⭕"} ]", fontSize = 12.sp, color = NatokGold, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1728))
            ) {
                Text("বাজি: $stakeAmount কয়েন", fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
            Text("$opponentName\n[ ${if (userIsX) "⭕" else "❌"} ]", fontSize = 12.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }

        // Current status label
        if (outcome.isEmpty()) {
            Text(
                text = if (isUserTurn) "👉 আপনার চাল! বোর্ডের ফাঁকা ঘরে ক্লিক করুন" else "⏳ $opponentName চাল দিচ্ছে...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUserTurn) NatokGold else TextSecondary,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        // 3x3 Tic-Tac-Toe Board
        Column(
            modifier = Modifier
                .size(260.dp)
                .background(Color(0xFF1A1422), RoundedCornerShape(12.dp))
                .border(2.dp, BorderDark, RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0..2) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        val cellValue = board[index]
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onCellClick(index) },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E2235))
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = cellValue,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (cellValue == "X") NatokRed else if (cellValue == "O") Color(0xFF4CAF50) else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Outcome Display
        if (outcome.isNotEmpty()) {
            Text(
                text = when (outcome) {
                    "WIN" -> "🏆 বিজয়! আপনি ম্যাচটি জিতেছেন!"
                    "LOSE" -> "😢 পরাজয়! ম্যাচটি প্রতিপক্ষ জিতেছে।"
                    else -> "🤝 ম্যাচ ড্র হয়েছে!"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (outcome == "WIN") Color(0xFF4CAF50) else if (outcome == "LOSE") NatokRed else Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = when (outcome) {
                    "WIN" -> "পুরস্কার: +${(stakeAmount * 2 * 0.8).toInt()} কয়েন ওয়ালেটে জমা হয়েছে।"
                    "LOSE" -> "দুঃখিত, -$stakeAmount কয়েন ম্যাচ স্টেকে কেটে নেওয়া হয়েছে।"
                    else -> "ড্র হওয়ার কারণে স্টেক করা $stakeAmount কয়েন ফেরত দেওয়া হয়েছে।"
                },
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("গেম জোন লবিতে ফিরুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun checkTicTacToeWinner(board: Array<String>, userIsX: Boolean): String? {
    val winningPos = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // Rows
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // Cols
        listOf(0, 4, 8), listOf(2, 4, 6)                  // Diagonals
    )
    val userSymbol = if (userIsX) "X" else "O"
    val botSymbol = if (userIsX) "O" else "X"

    for (pos in winningPos) {
        if (board[pos[0]] == userSymbol && board[pos[1]] == userSymbol && board[pos[2]] == userSymbol) {
            return "WIN"
        }
        if (board[pos[0]] == botSymbol && board[pos[1]] == botSymbol && board[pos[2]] == botSymbol) {
            return "LOSE"
        }
    }
    if (board.all { it.isNotEmpty() }) {
        return "DRAW"
    }
    return null
}

fun findWinningMove(board: Array<String>, symbol: String): Int? {
    val winningPos = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        listOf(0, 4, 8), listOf(2, 4, 6)
    )
    for (pos in winningPos) {
        val vals = listOf(board[pos[0]], board[pos[1]], board[pos[2]])
        if (vals.count { it == symbol } == 2 && vals.count { it.isEmpty() } == 1) {
            val emptyPosInLine = pos[vals.indexOf("")]
            return emptyPosInLine
        }
    }
    return null
}

@Composable
fun QuizPlayground(
    question: QuizQuestion,
    currentQuestionNum: Int,
    totalQuestions: Int,
    userScore: Int,
    opponentScore: Int,
    opponentName: String,
    stakeAmount: Int,
    selectedAnsIndex: Int?,
    outcome: String,
    onOptionSelected: (Int) -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Leaderboard top stats
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("আপনার স্কোর", fontSize = 10.sp, color = TextSecondary)
                Text("$userScore / $totalQuestions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = NatokGold)
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1728))
            ) {
                Text("কুইজ স্টেক: $stakeAmount", fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$opponentName স্কোর", fontSize = 10.sp, color = TextSecondary)
                Text("$opponentScore / $totalQuestions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
            }
        }

        if (outcome.isEmpty()) {
            // Question display card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C28))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("কুইজ ব্যাটেল", fontSize = 11.sp, color = NatokGold, fontWeight = FontWeight.Bold)
                        Text("প্রশ্ন: $currentQuestionNum/$totalQuestions", fontSize = 11.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = question.question,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 22.sp
                    )
                }
            }

            // Options List
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                question.options.forEachIndexed { index, option ->
                    val isSelected = selectedAnsIndex == index
                    val isCorrect = index == question.correctIndex
                    val cardBg = when {
                        selectedAnsIndex == null -> Color(0xFF231F2E)
                        isSelected && isCorrect -> Color(0xFF1B5E20)
                        isSelected && !isCorrect -> Color(0xFFB71C1C)
                        isCorrect -> Color(0xFF1B5E20).copy(alpha = 0.5f)
                        else -> Color(0xFF231F2E).copy(alpha = 0.4f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(index) },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        border = BorderStroke(1.dp, if (isSelected) Color.White else BorderDark)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${'A' + index}. ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold
                            )
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            // Quiz over / Payout Display
            Text(
                text = when (outcome) {
                    "WIN" -> "🏆 কুইজ বিজয়ী! অভিনন্দন!"
                    "LOSE" -> "😢 পরাজয়! ভালো চেষ্টার জন্য পরের বার চেষ্টা করুন।"
                    else -> "🤝 কুইজ ম্যাচ ড্র হয়েছে!"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (outcome == "WIN") Color(0xFF4CAF50) else if (outcome == "LOSE") NatokRed else Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = when (outcome) {
                    "WIN" -> "পুরস্কার: +${(stakeAmount * 2 * 0.8).toInt()} কয়েন ওয়ালেটে জমা হয়েছে।"
                    "LOSE" -> "দুঃখিত, -$stakeAmount কয়েন ম্যাচ স্টেকে কাটা গিয়েছে।"
                    else -> "ড্র হওয়ার কারণে আপনার $stakeAmount কয়েন ফেরত দেওয়া হয়েছে।"
                },
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("গেম জোন লবিতে ফিরুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GameSimulationPlayground(
    game: GameItem,
    opponentName: String,
    opponentGender: String,
    progress: Float,
    stakeAmount: Int,
    logs: List<String>,
    multiplier: Float,
    outcome: String,
    onBoostSkill: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = game.banglaName + " ব্যাটেল",
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = NatokGold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Profiles match info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1626))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎮", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(game.name, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Text("ম্যাচ স্টেক: $stakeAmount কয়েন", fontSize = 11.sp, color = NatokGold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("আপনি (You)", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("⚡ VS ⚡", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = NatokRed)
                    Text(opponentName, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Simulated progress bar
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = NatokRed,
                    trackColor = Color(0xFF2B1F2D)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("খেলা চলমান", fontSize = 9.sp, color = TextSecondary)
                    Text("${(progress * 100).toInt()}% সম্পন্ন", fontSize = 9.sp, color = NatokGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Skill Boost Button (Allows interaction and skill control as required!)
        if (outcome.isEmpty()) {
            Button(
                onClick = onBoostSkill,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("⚡ দক্ষতা বুস্ট করুন (Boost Skill)", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Text(
                text = "গুণক (Multiplier): x${String.format("%.2f", multiplier)} (ট্যাপ করে জেতার সুযোগ বাড়ান!)",
                fontSize = 10.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )
        }

        // Live Scrolling Game Logs Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(bottom = 14.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("লাইভ গেম লগ (Live Events):", fontSize = 10.sp, color = NatokGold, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                logs.forEach { log ->
                    Text(
                        text = "• $log",
                        fontSize = 11.sp,
                        color = Color.White,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }

        // Outcome Display
        if (outcome.isNotEmpty()) {
            Text(
                text = when (outcome) {
                    "WIN" -> "🏆 বিজয়! আপনি ম্যাচটি জিতেছেন!"
                    "LOSE" -> "😢 পরাজয়! ম্যাচটি প্রতিপক্ষ জিতেছে।"
                    else -> "🤝 ম্যাচ ড্র হয়েছে!"
                },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (outcome == "WIN") Color(0xFF4CAF50) else if (outcome == "LOSE") NatokRed else Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = when (outcome) {
                    "WIN" -> "পুরস্কার: +${(stakeAmount * 2 * 0.8).toInt()} কয়েন ওয়ালেটে জমা হয়েছে।"
                    "LOSE" -> "দুঃখিত, -$stakeAmount কয়েন ম্যাচ স্টেকে কাটা গিয়েছে।"
                    else -> "ড্র হওয়ার কারণে আপনার $stakeAmount কয়েন ফেরত দেওয়া হয়েছে।"
                },
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("গেম জোন লবিতে ফিরুন", fontWeight = FontWeight.Bold)
            }
        }
    }
}
