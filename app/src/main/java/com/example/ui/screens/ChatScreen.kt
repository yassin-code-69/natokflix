package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.FriendProfile
import com.example.ui.components.GenderAvatar
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.SurfaceDark
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    user: User,
    friend: FriendProfile,
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    onToggleBlock: (Boolean) -> Unit = {},
    isBotTyping: Boolean = false,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showBlockConfirmDialog by remember { mutableStateOf(false) }

    // Scroll to bottom when messages update
    LaunchedEffect(messages.size, isBotTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .imePadding()
            .testTag("chat_screen_container"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("chat_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        GenderAvatar(
                            gender = friend.gender,
                            name = friend.name,
                            size = 38.dp,
                            showOnlineDot = !friend.isBlocked,
                            isOnline = friend.isOnline && !friend.isBlocked,
                            isBot = friend.isBot
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = friend.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (friend.isBot) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(NatokGold.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                            .border(0.5.dp, NatokGold, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "BOT",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = NatokGold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = if (friend.isBlocked) "🚫 ব্লক করা হয়েছে (Blocked)"
                                       else if (isBotTyping) "টাইপ করছে..."
                                       else if (friend.isOnline) "🟢 Active Now • ${friend.location}"
                                       else "Offline • ${friend.location}",
                                fontSize = 11.sp,
                                color = if (friend.isBlocked) Color(0xFFFF5252)
                                        else if (isBotTyping) NatokGold
                                        else if (friend.isOnline) Color(0xFF00E676)
                                        else Color(0xFF888899)
                            )
                        }

                        // Block / Unblock Action Button
                        IconButton(
                            onClick = { showBlockConfirmDialog = true },
                            modifier = Modifier.testTag("chat_block_toggle_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = if (friend.isBlocked) "Unblock" else "Block",
                                tint = if (friend.isBlocked) Color(0xFFFF5252) else Color(0xFFAAAAAA),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                if (friend.isBlocked) {
                    // Blocked notice strip
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF33161A), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color(0xFF881E2A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "এই ব্যবহারকারীকে ব্লক করা হয়েছে। বার্তা প্রেরণ বন্ধ রয়েছে।",
                                fontSize = 11.sp,
                                color = Color(0xFFFFCDD2)
                            )
                        }
                        TextButton(
                            onClick = { onToggleBlock(false) },
                            colors = ButtonDefaults.textButtonColors(contentColor = NatokGold)
                        ) {
                            Text("আনব্লক করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Strict Media Restriction Banner (TEXT-ONLY)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1724), RoundedCornerShape(8.dp))
                            .border(0.5.dp, Color(0xFF3D2C4D), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NatokGold,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "🔒 টেক্সট-অনলি চ্যাট: ডাটা সাশ্রয় ও নিরাপত্তার জন্য মিডিয়া/ফাইল পাঠানো বন্ধ রয়েছে।",
                            fontSize = 10.sp,
                            color = Color(0xFFCCCCCC),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Text Input and Send Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("মেসেজ লিখুন (Text Only)...", fontSize = 13.sp, color = Color(0xFF888899)) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_field"),
                            maxLines = 3,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NatokRed,
                                unfocusedBorderColor = Color(0xFF333344),
                                focusedContainerColor = Color(0xFF16121E),
                                unfocusedContainerColor = Color(0xFF16121E),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank() && !friend.isBlocked) {
                                    onSendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            enabled = inputText.isNotBlank() && !friend.isBlocked,
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (inputText.isNotBlank() && !friend.isBlocked) NatokRed else Color(0xFF333344)
                                )
                                .testTag("chat_send_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BackgroundDark)
        ) {
            // Profile Card Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1528))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GenderAvatar(
                        gender = friend.gender,
                        name = friend.name,
                        size = 46.dp,
                        isBot = friend.isBot
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = friend.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = friend.bio.ifBlank { "নাটকফ্লিক্স ফ্রেন্ড নেটওয়ার্কে আপনাকে স্বাগতম!" },
                            fontSize = 11.sp,
                            color = Color(0xFFB0A4C0),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Message History List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatBubbleItem(message = msg, friendGender = friend.gender, myLevelBadge = user.levelBadge)
                }

                if (isBotTyping && !friend.isBlocked) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        ) {
                            GenderAvatar(
                                gender = friend.gender,
                                size = 26.dp,
                                isBot = friend.isBot
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF282236), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NatokGold))
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NatokGold))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${friend.name.split(" ").firstOrNull() ?: ""} টাইপ করছে...",
                                        fontSize = 11.sp,
                                        color = NatokGold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Block / Unblock Confirmation Dialog
    if (showBlockConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBlockConfirmDialog = false },
            title = {
                Text(
                    text = if (friend.isBlocked) "ব্যবহারকারীকে আনব্লক করবেন?" else "ব্যবহারকারীকে ব্লক করবেন?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (friend.isBlocked)
                        "${friend.name}-কে আনব্লক করলে পুনরায় বার্তা আদান-প্রদান করা যাবে।"
                    else
                        "${friend.name}-কে ব্লক করলে এই আইডি থেকে ভবিষ্যতে কোনো স্বয়ংক্রিয় বা সাধারণ বার্তা আসবে না।",
                    fontSize = 13.sp,
                    color = Color(0xFFDDDDDD)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onToggleBlock(!friend.isBlocked)
                        showBlockConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (friend.isBlocked) Color(0xFF2E7D32) else NatokRed
                    )
                ) {
                    Text(if (friend.isBlocked) "হ্যাঁ, আনব্লক করুন" else "হ্যাঁ, ব্লক করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}

@Composable
fun ChatBubbleItem(message: ChatMessage, friendGender: String, myLevelBadge: String) {
    val isMe = message.isFromMe
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeString = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            GenderAvatar(
                gender = friendGender,
                size = 28.dp,
                modifier = Modifier.align(Alignment.Bottom)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // Display Sender Name and Level Badge above the message bubble
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 2.dp, start = 4.dp, end = 4.dp)
            ) {
                if (isMe) {
                    Text(
                        text = "আপনি • $myLevelBadge",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NatokGold
                    )
                } else {
                    Text(
                        text = message.senderName,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC0AECB)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isMe) {
                            Brush.horizontalGradient(
                                listOf(NatokRed, Color(0xFFC2185B))
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2E263E), Color(0xFF231B32))
                            )
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 13.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = timeString,
                fontSize = 9.sp,
                color = Color(0xFF7E7E94),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
